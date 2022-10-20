package controllers

import models.{Stadium, Team}
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.{Document, MongoDatabase}
import org.mongodb.scala.model.Aggregates._
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, text}
import play.api.mvc._
import services.{AsyncStadiumService, AsyncTeamService}

import javax.inject._
import scala.util.hashing.MurmurHash3
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TeamData(name: String, stadiumId: Long, imgUrl: String)

class TeamController @Inject() (
    val controllerComponents: ControllerComponents,
    val teamService: AsyncTeamService,
    val stadiumService: AsyncStadiumService,
    mongoDatabase: MongoDatabase
) extends BaseController
    with play.api.i18n.I18nSupport {

  def list() = Action.async { implicit request =>
    teamService.findAll().map(xs => Ok(views.html.team.teams(xs)))
  }

  val teamForm: Form[TeamData] = Form(
    mapping(
      "name" -> text,
      "stadium" -> longNumber,
      "imgUrl" -> text
    )(TeamData.apply) //Construction
    (TeamData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action.async { implicit request =>
    stadiumService.findAll().map(xs => Ok(views.html.team.create(teamForm, xs)))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    teamForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        stadiumService
          .findAll()
          .map(xs => BadRequest(views.html.team.create(formWithErrors, xs)))
      },
      teamData => {
        val maybeStadium = stadiumService.findById(teamData.stadiumId)
        val id = MurmurHash3.stringHash(teamData.name)
        maybeStadium
          .map { s =>
            models.Team(id, teamData.name, s match {
                case Some(stadium) => stadium.id
              })
          }
          .map { t =>
            teamService.create(t)
            Redirect(routes.TeamController.show(t.id))
          }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit request =>
    teamService
      .findById(id)
      .map {
        case Some(team) =>
          val filledForm = teamForm.fill(
            TeamData(team.name, team.stadiumId, team.imgUrl)
          )
          Ok(views.html.team.update(team, filledForm))
        case None => NotFound("Stadium not found")
      }
  }

  def update() = Action.async { implicit request =>
    teamForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        stadiumService
          .findAll()
          .map(xs => BadRequest(views.html.team.create(formWithErrors, xs)))
      },
      teamData => {
        val id = MurmurHash3.stringHash(teamData.name)
        val team = Team(id, teamData.name, teamData.stadiumId, teamData.imgUrl)
        println("Yay!" + team)
        teamService
          .update(id, team)
          .map(t => Redirect(routes.TeamController.show(id)))
      }
    )
  }

  def show(id: Long) = Action.async { implicit request =>
    mongoDatabase
      .getCollection("teams")
      .aggregate(
        Seq(
          lookup("stadiums", "stadium", "_id", "stadiumDetails")
        )
      )
      .toSingle()
      .headOption()
      .flatMap {
        case Some(stadiumInfo) =>
          val stadiumDetails = getArrayFromDocument(stadiumInfo, "stadiumDetails").head
          println(stadiumDetails)
          val stadium = Stadium(stadiumDetails("_id").toString.toLong, stadiumDetails("name").toString, stadiumDetails("city").toString, stadiumDetails("country").toString, stadiumDetails("capacity").toString.toInt, stadiumDetails("imgUrl").toString)
          teamService
            .findById(id)
            .map {
              case Some(team) => Ok(views.html.team.show(team.id, team.name, team.imgUrl, stadium))
              case None       => NotFound("Team not found")
            }
        case None => Future(NotFound("Team not found"))
      }
  }

  private def getArrayFromDocument(d: Document, field: String): List[Map[Any, Any]] = {
    import scala.jdk.CollectionConverters._
    d
      .getList(field, classOf[java.util.Map[_, _]])
      .asScala
      .toList
      .map(_.asScala.toMap)
  }
}
