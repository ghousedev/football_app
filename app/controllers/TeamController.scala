package controllers

import models.{Stadium, Team}
import services.MemoryTeamService
import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._

import scala.util.hashing.MurmurHash3

case class TeamData(name: String, stadium: String)

class TeamController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController with play.api.i18n.I18nSupport {
  def list() = Action { implicit request =>
    val result = List(
      Team(13L, "", Stadium(12L, "Emirates Stadium", "A", "B", 0)),
      Team(13L, "", Stadium(12L, "Emirates Stadium", "A", "B", 0)),
      Team(13L, "", Stadium(12L, "Emirates Stadium", "A", "B", 0)),
      Team(13L, "", Stadium(12L, "Emirates Stadium", "A", "B", 0))
    )
    Ok(views.html.team.teams(result))
  }

  val teamForm = Form(
    mapping(
      "name" -> text,
      "stadium" -> text
    )(TeamData.apply) //Construction
    (TeamData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.team.create(teamForm))
  }

  def create() = Action { implicit request =>
    teamForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        BadRequest(views.html.team.create(formWithErrors))
      },
      teamData => {
        val id = MurmurHash3.stringHash(teamData.name)
        val newUser = models.Team(
          id,
          teamData.name,
          Stadium(12L, "Emirates Stadium", "A", "B", 0)
           )
        println("Yay!" + newUser)
        Redirect(routes.TeamController.show(id))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Ok("This is placeholder for this team")
  }
}
