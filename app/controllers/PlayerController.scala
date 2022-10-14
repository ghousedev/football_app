package controllers;

import models._

import javax.inject._
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.mvc._

import scala.util.hashing.MurmurHash3
import services.{AsyncPlayerService, AsyncStadiumService, AsyncTeamService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, CanAwait, Future}
import scala.concurrent.duration.Duration


case class PlayerData(
    team: String,
    position: String,
    firstName: String,
    surname: String
)

class PlayerController @Inject() (
    val controllerComponents: ControllerComponents,
    val playerService: AsyncPlayerService,
    val teamService: AsyncTeamService,
    val stadiumService: AsyncStadiumService
) extends BaseController
    with play.api.i18n.I18nSupport {

  implicit val permit: CanAwait = ???
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

  def create() = Action.async { implicit request =>
    playersForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          println("Nay!" + formWithErrors)
          BadRequest(views.html.players.create(formWithErrors))
        }
      },
      playersData => {
        Future {
          val id = MurmurHash3.stringHash(
            playersData.firstName + playersData.surname + playersData.position
          )
          val maybeTeam = teamService.findByName(playersData.team)
          val newPlayers = Player(
            id,
            maybeTeam.map {
              case Some(team) => team
            },
            playersData.position match {
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
            },
            playersData.firstName,
            playersData.surname
          )
          println("Yay!" + newPlayers)
          playerService.create(newPlayers)
          Redirect(routes.PlayerController.show(id))
        }
      }
    )
  }

  def show(id: Long) = Action.async { implicit request =>
    playerService.findById(id).map {
      case Some(player) => Ok(views.html.players.show(player))
      case None         => NotFound("Player not found")
    }
  }
}
