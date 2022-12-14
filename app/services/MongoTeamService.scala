package services

import com.google.inject.Inject
import models.{Stadium, Team}
import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates.set
import org.mongodb.scala.model.Filters.equal

import javax.inject._
import scala.concurrent.Future

class MongoTeamService @Inject() (mongoDatabase: MongoDatabase)
    extends AsyncTeamService {

  val collection = mongoDatabase.getCollection("teams")
  val stadiumCollection = mongoDatabase.getCollection("stadiums")

  override def create(team: Team): Unit = {
    val aTeam = teamToDocument(team)
    collection
      .insertOne(aTeam)
      .subscribe(
        r => println(s"Successful insert: $r"),
        t => t.printStackTrace(),
        () => println("Insert Complete")
      )
  }

  override def update(id: Int, team: Team): Future[Option[Team]] = {
    println(id)
    collection
      .findOneAndUpdate(
        equal("_id", id),
        set(
          model.Field("name", team.name),
          model.Field("imgUrl", team.imgUrl)
        )
      )
      .map(d => documentToTeam(d))
      .toSingle()
      .headOption()
  }

  override def findById(id: Long): Future[Option[Team]] = {
    collection
      .find(equal("_id", id))
      .map(d => documentToTeam(d))
  }.toSingle().headOption()

  override def findAll(): Future[List[Team]] =
    collection
      .find()
      .map(d => documentToTeam(d))
      .foldLeft(List.empty[Team])((list, team) => team :: list)
      .head()

  override def findByName(name: String): Future[Option[Team]] =
    collection
      .find(equal("name", name))
      .map(d => documentToTeam(d))
      .toSingle()
      .headOption()

  private def teamToDocument(team: Team): Document = {
    Document(
      "_id" -> team.id,
      "name" -> team.name,
      "stadium" -> team.stadiumId,
      "imgUrl" -> team.imgUrl
    )
  }

  private def documentToTeam(d: Document) = {
    println(d)
    Team(
      d.getLong("_id"),
      d.getString("name"),
      d.getLong("stadium"),
      d.getString("imgUrl")
    )
  }

  private def documentToStadium(d: Document) = {
    Stadium(
      d.getLong("_id"),
      d.getString("name"),
      d.getString("city"),
      d.getString("country"),
      d.getInteger("capacity"),
      d.getString("imgUrl")
    )
  }
}
