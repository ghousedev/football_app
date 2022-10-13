package controllers

import models.{Stadium, Team}
import play.api._
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, number, text}
import play.api.mvc._
import services.{MemoryStadiumService, StadiumService, TeamService}

import javax.inject._
import scala.util.Random
import scala.util.hashing.MurmurHash3

case class TeamData(name: String, stadiumId: Long)

class TeamController @Inject() (
    val controllerComponents: ControllerComponents,
    val teamService: TeamService,
    val stadiumService: StadiumService
) extends BaseController
    with play.api.i18n.I18nSupport {
  def list() = Action { implicit request =>
    val result = teamService.findAll()
    Ok(views.html.team.teams(result))
  }

  val teamForm: Form[TeamData] = Form(
    mapping(
      "name" -> text,
      "stadium" -> longNumber
    )(TeamData.apply) //Construction
    (TeamData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    val stadList = stadiumService.findAll()
    println(stadList)
    Ok(views.html.team.create(teamForm, stadList))
  }

  def create() = Action { implicit request =>
    teamForm.bindFromRequest.fold(
      formWithErrors => {
        val stadList = stadiumService.findAll()
        println("Nay!" + formWithErrors)
        BadRequest(views.html.team.create(formWithErrors, stadList))
      },
      teamData => {
        val maybeStadium = stadiumService.findById(teamData.stadiumId)
        val id = MurmurHash3.stringHash(teamData.name)
        maybeStadium
          .map { s =>
            models.Team(
              id,
              teamData.name,
              s
            )
          }
          .map { t =>
            teamService.create(t); t
          }
          .map(t => Redirect(routes.TeamController.show(id)))
          .getOrElse(NotFound("Stadium not found"))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    val maybeTeam = teamService.findById(id)
    maybeTeam
      .map(s => Ok(views.html.team.show(s)))
      .getOrElse(NotFound(s"No team matches id: $id"))
  }
}
