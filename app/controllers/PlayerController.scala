package controllers;

import models._

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.mvc._

import scala.util.Random
import scala.util.hashing.MurmurHash3

case class PlayerData(team: String, position: String, firstName: String, surname: String)

class PlayerController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  val GrimsbyTown =
    Team(55L, "Grimsby Town", Stadium(15L, "The Dripping Pan", "A", "B", 5))

  def list() = Action { implicit request =>
    val result = List(
      Player(GrimsbyTown, Striker, "", ""),
      Player(GrimsbyTown, GoalKeeper, "", ""),
      Player(GrimsbyTown, LeftMidfielder, "", "")
    )
    Ok(views.html.players.players(result))
  }

  val playersForm = Form(
    mapping(
      "name" -> text,
      "surname" -> text,
      "team" -> text,
      "position" -> text
    )(PlayerData.apply) //Construction
    (PlayerData.unapply) //Destructuring
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
        val newUser = models.Player(Team(10L, playersData.team, Stadium(Random.nextLong(), "", "", "", 0)), playersData.position match {
            case "GoalKeeper" => GoalKeeper
            case "RightFullback" => RightFullback
            case "LeftFullback" => LeftFullback
            case "CenterBack" => CenterBack
            case "Sweeper" => Sweeper
            case "Striker" => Striker
            case "HoldingMidfielder" => HoldingMidfielder
            case "RightMidfielder" => RightMidfielder
            case "Central" => Central
            case "AttackingMidfielder" => AttackingMidfielder
            case "LeftMidfielder" => LeftMidfielder
            case _ => GoalKeeper
          }, "", "")
        println("Yay!" + newUser)
        Redirect(routes.PlayerController.show(id))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Ok("This is placeholder for this player")
  }
}
