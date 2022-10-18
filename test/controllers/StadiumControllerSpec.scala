package controllers


import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import org.mongodb.scala.MongoClient
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test._
import services.MongoStadiumService

class StadiumControllerSpec extends PlaySpec with ForAllTestContainer with GuiceOneAppPerTest {
  val HTTP_STATUS_OK = 200
  override val container: MongoDBContainer = new MongoDBContainer()
  val host: String = container.host

  "StadiumControllerSpec" should {

    "edit" in {}

    "should list all stadiums" in {
      val controller = new StadiumController(stubControllerComponents(), new MongoStadiumService(getDb))
      val page = controller.list()(FakeRequest(GET, "/stadiums"))
      status(page) mustBe HTTP_STATUS_OK
      contentType(page) mustBe Some("text/html")
      contentAsString(page) must include ("stadiums")
    }

    "show page to create a new stadium in the database" in {
      val controller = new StadiumController(stubControllerComponents(), new MongoStadiumService(getDb))
      val page = controller.list()(FakeRequest(GET, "/stadium"))
      status(page) mustBe HTTP_STATUS_OK
      contentType(page) mustBe Some("text/html")
      contentAsString(page) must include ("stadium")
    }

    "stadiumForm" in {}

    "update" in {}

    "Ok" in {}

    "defaultFormBinding" in {}

    "show" in {}
  }

  private def getDb = {
    val port: Int = container.livenessCheckPortNumbers.head
    val mongoClient: MongoClient = MongoClient(s"mongodb://$host:$port")
    val db = mongoClient.getDatabase("tests")
    db
  }
}
