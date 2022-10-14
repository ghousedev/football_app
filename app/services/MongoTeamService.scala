package services

import models.Team
import org.mongodb.scala._
import org.mongodb.scala.model.Filters.equal

import javax.inject._
import scala.concurrent.Future

class MongoTeamService
  extends AsyncTeamService {
  val database =
    MongoClient("mongodb://mongo-root:mongo-password@localhost:27017").getDatabase("football_app")
  val collection = database.getCollection("teams")

  override def create(team: Team): Unit = {
    val aTeam = teamToDocument(team)
    collection.insertOne(aTeam).subscribe(r => println(s"Successful insert: $r"), t => t.printStackTrace(), () => println("Insert Complete"))
  }

  override def update(team: Team): Future[Option[Team]] = {
    val aTeam = teamToDocument(team)
    collection
      .findOneAndUpdate(equal("_id", team.id), aTeam)
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
      .foldLeft(List.empty[Team])((list, team) => team :: list).head()

  override def findByName(name: String): Future[Option[Team]] =
    collection
      .find(equal("name", name))
      .map(d => documentToTeam(d))
      .toSingle().headOption()


  private def teamToDocument(team: Team): Document = {
    Document(
      "_id" -> team.id,
      "name" -> team.name,
      "stadium" -> team.stadium
    )
  }

  private def documentToTeam(d: Document) = {
    Team(
      d.getLong("_id"),
      d.getString("name"),
      d.get("stadium")
    )
  }

}
