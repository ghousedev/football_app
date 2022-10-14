package controllers;

import models.Stadium

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._
import services.{AsyncStadiumService, StadiumService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.hashing.MurmurHash3

case class StadiumData(name: String, city: String, country: String, seats: Int)

class StadiumController @Inject() (
    val controllerComponents: ControllerComponents,
    val stadiumService: AsyncStadiumService
) extends BaseController
    with play.api.i18n.I18nSupport {
  def list() = Action.async { implicit request =>
    stadiumService.findAll().map(xs => Ok(views.html.stadium.stadiums(xs)))
  }

  val stadiumForm: Form[StadiumData] = Form(
    mapping(
      "name" -> text,
      "city" -> text,
      "country" -> text,
      "seats" -> number
    )(StadiumData.apply) //Construction
    (StadiumData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.stadium.create(stadiumForm))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    stadiumForm.bindFromRequest.fold(
      formWithErrors => Future {
        println("Nay!" + formWithErrors)
        BadRequest(views.html.stadium.create(formWithErrors))
      },
      stadiumData => Future {
        val id = MurmurHash3.stringHash(stadiumData.name)
        stadiumService
          .findById(id)
          .map {
            case Some(stadium) => Ok(views.html.stadium.show(stadium))
            case None => NotFound("Stadium not found")
          }
        val newStadium = models.Stadium(
          id,
          stadiumData.name,
          stadiumData.city,
          stadiumData.country,
          stadiumData.seats
        )
        println("Yay!" + newStadium)
        stadiumService.create(newStadium)
        Redirect(routes.StadiumController.show(id))
      }
    )
  }

  def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
    stadiumService
      .findById(id)
      .map {
        case Some(stadium) => Ok(views.html.stadium.show(stadium))
        case None          => NotFound("Stadium not found")
      }
  //maybeStadium.map(s => Ok(views.html.stadium.show(s))).getOrElse(NotFound)
  }
}
