package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Grimsby Town")
    }

    "render the Grimsby Town title" in {
        val controller = inject[HomeController]
        val home = controller.index()(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Hello Grimsby Town Fans!")
    }

    "render We are Grimsby by name and GRIM by nature!" in {
        val controller = inject[HomeController]
        val home = controller.index()(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("We are Grimsby by name and GRIM by nature!")
    }

    "render the Grimsby Town logo" in {
        val controller = inject[HomeController]
        val home = controller.index()(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("/assets/images/grimsby-transparent.png")
    }

    "render the meet the players button" in {
        val controller = inject[HomeController]
        val home = controller.index()(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Meet the players")
    }

    "render the home, players, manager, stadium and table button" in {
        val controller = inject[HomeController]
        val home = controller.index()(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Home")
        contentAsString(home) must include ("Players")
        contentAsString(home) must include ("Manager")
        contentAsString(home) must include ("Stadium")
        contentAsString(home) must include ("Table")
    }

//    "render the players page" in {
//        val controller = inject[HomeController]
//        val home = controller.players()(FakeRequest(GET, "/players"))
//
//        status(home) mustBe OK
//        contentType(home) mustBe Some("text/html")
//        contentAsString(home) must include ("Players")
//    }

    "render the manager page" in {
        val controller = inject[HomeController]
        val home = controller.manager()(FakeRequest(GET, "/manager"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Manager")
    }

    "render the stadium page" in {
        val controller = inject[HomeController]
        val home = controller.stadiumInfo()(FakeRequest(GET, "/stadium"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Stadium")
    }

    "render the table page" in {
        val controller = inject[HomeController]
        val home = controller.table()(FakeRequest(GET, "/table"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include ("Table")
    }

  }
}
