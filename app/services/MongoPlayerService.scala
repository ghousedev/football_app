package services

import models.{AttackingMidfielder, CenterBack, Central, GoalKeeper, HoldingMidfielder, LeftFullback, Player, Position, RightFullback, Stadium, Striker, Sweeper, Team}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.{Document, MongoClient, MongoClientSettings, MongoCredential, ServerAddress, SingleObservable}
import org.mongodb.scala.model.Filters.equal
import services.MemoryTeamService

import scala.concurrent.Future
import scala.util.Try

class MongoPlayerService extends AsyncPlayerService {
  val mongoClient: MongoClient = MongoClient(
    "mongodb://mongo-root:mongo-password@localhost:27017"
  )
  val myCompanyDatabase = mongoClient.getDatabase("football_app")
  val playerCollection = myCompanyDatabase.getCollection("players")

  override def findById(id: Long): Future[Option[Player]] = {
    playerCollection
      .find(equal("_id", id))
      .map { d =>
        documentToPlayer(d)
      }
      .toSingle()
      .headOption()
  }

  override def create(player: Player): Unit = {
    val document: Document = playerToDocument(player)

    playerCollection
      .insertOne(document)
      .subscribe(
        r => println(s"Successful Insert $r"),
        t => t.printStackTrace(),
        () => "Insert Complete"
      )
  }

  private def playerToDocument(player: Player) = {
    Document(
      "_id" -> player.id,
      "firstName" -> player.firstName,
      "surname" -> player.surname,
      "position" -> player.position.toString,
      "team" -> player.team.toString
    )
  }

  override def update(player: Player): Future[Option[Player]] = {
    playerCollection
      .findOneAndUpdate(
        equal("_id", player.id),
        Document(
          "$set" -> Document(
            "firstName" -> player.firstName,
            "surname" -> player.surname,
            "position" -> player.position.toString,
            "team" -> player.team.toString
          )
        )
      )
      .map { d =>
        documentToPlayer(d)
      }
      .toSingle()
      .headOption()
  }

  override def findAll(): Future[List[Player]] = playerCollection
    .find()
    .map(documentToPlayer)
    .foldLeft(List[Player]())((acc, player) => player :: acc)
    .head()

  private def documentToPlayer(d: Document): Player = {
    Player(
      d.getLong("_id"),
      documentToTeam(d.get("team").map(b => Document(b.asDocument())).get),
      documentToPosition(d.get("position").map(b => Document(b.asDocument())).get),
      d.getString("firstName"),
      d.getString("surname")

    )
  }

  override def findByFirstName(firstName: String): Future[Option[Player]] = {
    playerCollection
      .find(equal("firstName", firstName))
      .map(documentToPlayer)
      .toSingle()
      .headOption()
  }
  override def findByLastName(lastName: String): Future[Option[Player]] = {
    playerCollection
      .find(equal("surname", lastName))
      .map(documentToPlayer)
      .toSingle()
      .headOption()
  }
  override def findByPosition(position: Position): Future[List[Player]] = ???

  private def documentToPosition(d: Document) = {
    d.getString("position") match {
      case "GoalKeeper" => GoalKeeper
      case "RightFullback" => RightFullback
      case "LeftFullback" => LeftFullback
      case "CenterBack" => CenterBack
      case "Sweeper" => Sweeper
      case "Striker" => Striker
      case "HoldingMidfielder" => HoldingMidfielder
      case "RightMidfielder" => RightFullback
      case "Central" => Central
      case "AttackingMidfielder" => AttackingMidfielder
      case "LeftMidfielder" => LeftFullback
    }
  }

  private def documentToTeam(d: Document) = {
    Team(
      d.getLong("_id"),
      d.getString("name"),
      documentToStadium(d.get("stadium").map(b => Document(b.asDocument())).get)
    )
  }

  private def documentToStadium(d: Document) = {
    Stadium(
      d.getLong("_id"),
      d.getString("name"),
      d.getString("city"),
      d.getString("country"),
      d.getInteger("capacity")
    )
  }
}
