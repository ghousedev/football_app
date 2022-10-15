package services

import models.{GoalKeeper, Player, Position, Stadium, Team}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.{Document, MongoClient, MongoClientSettings, MongoCredential, ServerAddress, SingleObservable}
import org.mongodb.scala.model.Filters.equal
import services.MemoryTeamService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

class MongoPlayerService extends AsyncPlayerService {
  val mongoClient: MongoClient = MongoClient(
    "mongodb://mongo-root:mongo-password@localhost:27017"
  )
  val myCompanyDatabase = mongoClient.getDatabase("football_app")
  val playerCollection = myCompanyDatabase.getCollection("players")
  val teamCollection = myCompanyDatabase.getCollection("teams")
  val stadiumCollection = myCompanyDatabase.getCollection("stadiums")

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

  private def getStadiumId(player: Player): Long = {
    var result = 0L
    teamCollection.find(equal("_id", player.teamId))
      .map(d => d.getLong("stadium")).toSingle()
      .headOption().map{
      case Some(value) => value
    }.foreach(t => result = t)
    result
  }

  private def playerToDocument(player: Player): Document = {
    Document(
      "_id" -> player.id,
      "firstName" -> player.firstName,
      "surname" -> player.surname,
      "position" -> player.position.toString,
      "team" -> player.teamId,
      //"stadium" -> stadiumCollection.find(equal("_id", getStadiumId(player))).map(d => d.get("_id")),
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
            "team" -> player.teamId
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
      10L,
      //d.get("position"), // Must be of child type of Position
      GoalKeeper,
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
  override def findByPosition(position: Position): Future[List[Player]] = {
    playerCollection
      .find(equal("position", position.toString))
      .map(documentToPlayer)
      .foldLeft(List[Player]())((acc, player) => player :: acc)
      .head()
  }

  def documentToTeam(d: Document): Team = {
    Team(
      d.getLong("_id"),
      d.getString("name"),
      d.getLong("stadium")
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
