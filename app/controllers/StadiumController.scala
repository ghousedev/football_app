package controllers;

import org.mongodb.scala.Document
import javax.inject._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.data.validation.Constraints._
import play.api.mvc._
import services.AsyncStadiumService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.hashing.MurmurHash3

case class StadiumData(name: String, city: String, country: String, seats: Int, imgUrl: String)

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
      "name" -> text.verifying(nonEmpty),
      "city" -> text.verifying(nonEmpty),
      "country" -> text.verifying(nonEmpty),
      "seats" -> number.verifying(min(0), max(300000)),
      "imgUrl" -> text.verifying(nonEmpty))
    (StadiumData.apply) //Construction
    (StadiumData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.stadium.create(stadiumForm))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    stadiumForm.bindFromRequest.fold(
      formWithErrors =>
        Future {
          println("Nay!" + formWithErrors)
          BadRequest(views.html.stadium.create(formWithErrors))
        },
      stadiumData =>
        Future {
          val id = MurmurHash3.stringHash(stadiumData.name)
          stadiumService
            .findById(id)
            .map {
              case Some(stadium) => Ok(views.html.stadium.show(stadium))
              case None          => NotFound("Stadium not found")
            }
          val newStadium = models.Stadium(
            id,
            stadiumData.name,
            stadiumData.city,
            stadiumData.country,
            stadiumData.seats,
            stadiumData.imgUrl
          )
          println("Yay!" + newStadium)
          stadiumService.create(newStadium)
          Redirect(routes.StadiumController.show(id))
        }
    )
  }

  def edit(id: Long) = Action.async { implicit request =>
    stadiumService
      .findById(id)
      .map {
        case Some(stadium) =>
          val filledForm = stadiumForm.fill(
            StadiumData(
              stadium.name,
              stadium.city,
              stadium.country,
              stadium.seats,
              stadium.imgUrl
            )
          )
          Ok(views.html.stadium.update(filledForm))
        case None => NotFound("Stadium not found")
      }
  }

  def update() = Action.async { implicit request =>
    stadiumForm.bindFromRequest.fold(
      formWithErrors =>
        Future {
          println("Nay!" + formWithErrors)
          BadRequest(views.html.stadium.create(formWithErrors))
        },
      stadiumData => {
        val id = MurmurHash3.stringHash(stadiumData.name)
        val newStadium = Document(
          "name" -> stadiumData.name,
          "city" -> stadiumData.city,
          "country" -> stadiumData.country,
          "capacity" -> stadiumData.seats,
          "imgUrl" -> stadiumData.imgUrl
        )
        println("Yay!" + newStadium)
        stadiumService
          .update(id, newStadium)
          .map(s => Redirect(routes.StadiumController.show(s.id)))
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
  }
}
