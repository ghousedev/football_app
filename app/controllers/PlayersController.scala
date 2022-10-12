package controllers;

import models._

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.mvc._

import scala.util.hashing.MurmurHash3

case class PlayersData(team: String, position: String)

class PlayersController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  val GrimsbyTown =
    Team(55L, "Grimsby Town", Stadium(15L, "The Dripping Pan", "A", "B", 5))

  def list() = Action { implicit request =>
    val result = List(
      Player(GrimsbyTown, Striker),
      Player(GrimsbyTown, GoalKeeper),
      Player(GrimsbyTown, LeftMidfielder)
    )
    Ok(views.html.players.players(result))
  }

  val playersForm = Form(
    mapping(
      "team" -> text,
      "position" -> text
    )(PlayersData.apply) //Construction
    (PlayersData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.players.create(playersForm))
  }

  def create() = Action { implicit request =>
    playersForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        BadRequest(views.html.players.create(formWithErrors))
      },
      playersData => {
        val id = MurmurHash3.stringHash(playersData.team)
        val newUser = models.Player(
          id,
          playersData.team,
          playersData.position
        )
        println("Yay!" + newUser)
        Redirect(routes.PlayersController.show(id))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Ok("This is placeholder for this player")
  }
}
