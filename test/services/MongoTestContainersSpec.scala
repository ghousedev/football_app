package services

import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import models.{Stadium, Team}
import org.mongodb.scala._
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext.Implicits.global

class MongoTestContainersSpec extends PlaySpec with ForAllTestContainer {

  override val container: MongoDBContainer = new MongoDBContainer()
  val host: String = container.host

  "MongoTeamService" must {

    "create a team document" in {
      val db: MongoDatabase = getDb
      val teamService = new MongoTeamService(db)
      val team = Team(
        10L,
        "name",
        11L
      )
      teamService.create(team)

      val result = teamService
        .findById(11L)
        .map {
          case Some(t) => t
          case _ => ()
        }
      result.map(t => t mustEqual team)
    }
  }
  "MongoStadiumService" must {

    "create a stadium document" in {

      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)
      val stadium = Stadium(
        11L,
        "name",
        "city",
        "country",
        10,
        "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg"
      )
      stadiumService.create(stadium)

      stadiumService
        .findById(11L)
        .foreach {
          case Some(s) => s mustEqual stadium
          case _ => ()
        }
    }

    "update a stadium document" in {
      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)

      val updatedDocument = Document(
        "_id" -> 10L,
        "name" -> "name",
        "city" -> "city",
        "country" -> "country",
        "capacity" -> 100
      )

      stadiumService.update(10L, updatedDocument)

      stadiumService
        .findById(10L)
        .map(r =>
          stadiumService.stadiumToDocument(r match {
            case Some(stadium) => stadium
            case _ => Stadium(0, "", "", "", 0, "")
          }) mustEqual updatedDocument
        )
    }

    "find a stadium by it's id" in {
      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)
      stadiumService
        .findById(10L)
        .map(r =>
          r mustEqual Document(
            "_id" -> 10L,
            "name" -> "name",
            "city" -> "city",
            "country" -> "country",
            "seats" -> 100,
            "imgUrl" -> "imgUrl"
          )
        )
    }

    "list all stadiums" in {
      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)
      stadiumService.findAll().map(r => r.size mustEqual 1)
    }

    "list stadiums by country" in {
      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)
      val stadium = Stadium(
        12L,
        "name",
        "city",
        "country",
        10,
        "imgUrl"
      )
      stadiumService.create(stadium)
      stadiumService
        .findByCountry("country")
        .map(r => {
          r.size mustEqual 3
          r.map(s => s.country mustBe "country")
        })
    }

    "find a stadium by it's name" in {
      val db: MongoDatabase = getDb
      val stadiumService = new MongoStadiumService(db)
      val result = stadiumService
        .findByName("name")
        .map {
          case Some(r) => r.name
          case _ => ()
        }
      result.foreach(s => s mustEqual "name")
    }

    "convert a stadium to a document" in {}

    "convert a document to a stadium" in {}
  }

  private def getDb = {
    val port: Int = container.livenessCheckPortNumbers.head
    val mongoClient: MongoClient = MongoClient(s"mongodb://$host:$port")
    val db = mongoClient.getDatabase("tests")
    db
  }
}
