package controllers

import org.mongodb.scala.Document
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, text}
import play.api.mvc._
import services.{AsyncStadiumService, AsyncTeamService, StadiumService, TeamService}

import javax.inject._
import scala.util.hashing.MurmurHash3
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TeamData(name: String, stadiumId: Long)

class TeamController @Inject() (
    val controllerComponents: ControllerComponents,
    val teamService: AsyncTeamService,
    val stadiumService: AsyncStadiumService
) extends BaseController
    with play.api.i18n.I18nSupport {
  def list() = Action.async { implicit request =>
    teamService.findAll().map(xs => Ok(views.html.team.teams(xs)))
  }

  val teamForm: Form[TeamData] = Form(
    mapping(
      "name" -> text,
      "stadium" -> longNumber
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
        stadiumService.findAll().map(xs => BadRequest(views.html.team.create(formWithErrors, xs)))
      },
      teamData => {
        val maybeStadium = stadiumService.findById(teamData.stadiumId)
        val id = MurmurHash3.stringHash(teamData.name)
        maybeStadium
          .map { s =>
            models.Team(
              id,
              teamData.name,
              s match {
                case Some(stadium) => stadium
              }
            )
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
        case Some(team) => {
          val filledForm = teamForm.fill(
            TeamData(
              team.name,
              team.stadium.id
            )
          )
          Ok(views.html.team.update(team, filledForm))
        }
        case None => NotFound("Stadium not found")
      }
  }

  def update() = Action.async { implicit request =>
    teamForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        stadiumService.findAll().map(xs => BadRequest(views.html.team.create(formWithErrors, xs)))
      },
      teamData => {
        val id = MurmurHash3.stringHash(teamData.name)
        val newStadium = Document(
          "name" -> teamData.name,
          "stadium" -> teamData.stadiumId
        )
        println("Yay!" + newStadium)
        stadiumService
          .update(id, newStadium)
          .map(s => Redirect(routes.StadiumController.show(s.id)))
      }
    )
  }

  def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
    teamService
      .findById(id)
      .map{
        case Some(team) => Ok(views.html.team.show(team.id, team))
        case None => NotFound("Team not found")
      }
  }
}
