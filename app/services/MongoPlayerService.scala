package services

import models.{Player, Position, Stadium, Team}
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.{Document, MongoClient, MongoClientSettings, MongoCredential, ServerAddress, SingleObservable}
import org.mongodb.scala.model.Filters.equal

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
      d.getString("firstName"),
      d.getString("surname"),
      d.getString("position"),
      d.getString("team")
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
  override def findByPosition(position: Position): Future[List[Player]] = {
    playerCollection
      .find(equal("position", position.toString))
      .map(documentToPlayer)
      .foldLeft(List[Player]())((acc, player) => player :: acc)
      .head()
  }
}
