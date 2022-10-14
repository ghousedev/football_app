package controllers;

import models._

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.mvc._

import scala.util.Random
import scala.util.hashing.MurmurHash3

import services.PlayerService
import services.AsyncPlayerService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

case class PlayerData(
    team: String,
    position: String,
    firstName: String,
    surname: String
)

class PlayerController @Inject() (
    val controllerComponents: ControllerComponents,
    val playerService: AsyncPlayerService
) extends BaseController
    with play.api.i18n.I18nSupport {

  def list() = Action.async { implicit request =>
    playerService.findAll().map(xs => Ok(views.html.players.players(xs)))
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
        val newPlayers = models.Player(
          id,
          Team(
            10L,
            playersData.team,
            Stadium(Random.nextLong(), "", "", "", 0)
          ),
          playersData.position match {
            case "GoalKeeper"          => GoalKeeper
            case "RightFullback"       => RightFullback
            case "LeftFullback"        => LeftFullback
            case "CenterBack"          => CenterBack
            case "Sweeper"             => Sweeper
            case "Striker"             => Striker
            case "HoldingMidfielder"   => HoldingMidfielder
            case "RightMidfielder"     => RightMidfielder
            case "Central"             => Central
            case "AttackingMidfielder" => AttackingMidfielder
            case "LeftMidfielder"      => LeftMidfielder
            case _                     => GoalKeeper
          },
          "",
          ""
        )
        println("Yay!" + newPlayers)
        playerService.create(newPlayers)
        Redirect(routes.PlayerController.show(id))
      }
    )
  }

  def show(id: Long) = Action.async { implicit request =>
    playerService.findById(id).map {
      case Some(player) => Ok(views.html.players.show(player))
      case None => NotFound("Player not found")
    }
  }
}
