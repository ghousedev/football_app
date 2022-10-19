package controllers;

import models._
import org.mongodb.scala.Document

import javax.inject._
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, text}
import play.api.mvc._

import scala.util.hashing.MurmurHash3
import services.{AsyncPlayerService, AsyncStadiumService, AsyncTeamService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class PlayerData(
    teamId: Long,
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

  def list() = Action.async { implicit request =>
    playerService.findAll().map(xs => Ok(views.html.players.players(xs)))
  }

  val playersForm: Form[PlayerData] = Form(
    mapping(
      "team" -> longNumber,
      "name" -> text,
      "surname" -> text,
      "position" -> text
    )(PlayerData.apply) //Construction
    (PlayerData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action.async { implicit request =>
    teamService
      .findAll()
      .map(xs => Ok(views.html.players.create(playersForm, xs)))
  }

  def create() = Action.async { implicit request =>
    playersForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        teamService
          .findAll()
          .map(xs => BadRequest(views.html.players.create(formWithErrors, xs)))
      },
      playersData => {
        val maybeTeam = teamService.findById(playersData.teamId)
        val id =
          MurmurHash3.stringHash(playersData.firstName)
        maybeTeam
          .map { t =>
            Player(
              id,
              t match {
                case Some(t) => t.id
              },
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
              playersData.firstName,
              playersData.surname
            )
          }
          .map { p =>
            playerService.create(p)
            Redirect(routes.PlayerController.show(p.id))
          }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit request =>
    playerService
      .findById(id)
      .map {
        case Some(player) => {
          val filledForm = playersForm.fill(
            PlayerData(
              player.teamId,
              player.position.toString,
              player.firstName,
              player.surname
            )
          )
          Ok(views.html.players.update(filledForm))
        }
        case None => NotFound("Player not found")
      }
  }

  def update() = Action.async { implicit request =>
    playersForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        teamService
          .findAll()
          .map(xs => BadRequest(views.html.players.create(formWithErrors, xs)))
      },
      playersData => {
        val maybeTeam = teamService.findById(playersData.teamId)
        val id =
          MurmurHash3.stringHash(playersData.firstName + playersData.surname)
        maybeTeam
          .map { t =>
            Player(
              id,
              t match {
                case Some(t) => t.id
              },
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
              playersData.firstName,
              playersData.surname
            )
          }
          .map { p =>
            playerService.update(p)
            Redirect(routes.PlayerController.show(p.id))
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
