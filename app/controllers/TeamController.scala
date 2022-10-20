package controllers

import models.Stadium
import org.mongodb.scala.{Document, MongoDatabase}
import org.mongodb.scala.bson._
import org.mongodb.scala.model.Aggregates._
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, text}
import play.api.mvc._
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala
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
        //.map(t => Redirect(routes.TeamController.show(t.id)))
        //.getOrElse(NotFound("Stadium not found"))
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
        var newTeam = Document()
        val maybeStadium = stadiumService.findById(teamData.stadiumId)
        val stadiumName = maybeStadium.map {
          case Some(stadium) => stadium.name
          case None          => "Not found"
        }
        stadiumName.map { s =>
          newTeam = Document(
            "name" -> teamData.name,
            "stadium" -> s
          )
        }
        println("Yay!" + newTeam)
        stadiumService
          .update(id, newTeam)
          .map(s => Redirect(routes.TeamController.show(s.id)))
      }
    )
  }

  def show(id: Long) = Action.async { implicit request =>
    mongoDatabase
      .getCollection("teams")
      .aggregate(
        List(
          lookup("stadiums", "stadium", "_id", "stadiumDetails"),
          out("temp")
        )
      )
      .toSingle()
      .headOption()
      .flatMap {
        case Some(teamInfo) =>
          val stadium: Stadium = parseStadiumDoc(teamInfo)
          teamService
            .findById(id)
            .map {
              case Some(team) => Ok(views.html.team.show(team.id, teamInfo, stadium))
              case None       => NotFound("Team not found")
            }
        case None => Future(NotFound("Team not found"))
      }
  }

  private def parseStadiumDoc(teamInfo: Document) = {
    val stadiumInfo = teamInfo("stadiumDetails").toString.split(",").toList
    val stadiumInfoList = stadiumInfo.map(s => s.trim.split(": ").apply(1).replace("\"", ""))
    val stadium = Stadium(stadiumInfoList.head.toLong, stadiumInfoList(1), stadiumInfoList(2), stadiumInfoList(3), stadiumInfoList(4).toInt)
    stadium
  }
}
