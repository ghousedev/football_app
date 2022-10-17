package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

//  def players() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.players.players())
//  }

  def stadiumInfo() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.stadiumInfo())
  }

  def table() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.table())
  }

  def teamOfTheYear() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.teamOfTheYear.teamOfTheYear())
  }

  def teamOfTheYearManager() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.teamOfTheYear.teamOfTheYearManager())
  }

  def teamOfTheYearPlayers() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.teamOfTheYear.teamOfTheYearPlayers())
  }
}

