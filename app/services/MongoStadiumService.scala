package services

import models.{Player, Position, Stadium}
import org.mongodb.scala._
import org.mongodb.scala.model.Filters.equal

import javax.inject._
import scala.concurrent.Future
import scala.util.Try

class MongoStadiumService @Inject() (mongoCollection: MongoCollection[Document])
    extends AsyncStadiumService {

  val database =
    MongoClient("mongodb://mongo-user:mongo-root@localhost:27017").getDatabase("football_app")
  val collection = database.getCollection("stadiums")

  override def create(stadium: Stadium): Unit = {
    val aStadium = Document(
      "_id" -> stadium.id,
      "name" -> stadium.name,
      "country" -> stadium.country,
      "city" -> stadium.city,
      "capacity" -> stadium.seats
    )
    collection.insertOne(aStadium).subscribe(r => println(s"Successful insert: $r"), t => t.printStackTrace(), () => println("Insert Complete"))
  }

  override def update(stadium: Stadium): Future[Try[Stadium]] = {
    ???
//    collection.findOneAndUpdate(
//      equal("_id", stadium.id),
//      Stadium(
//        stadium.id,
//        stadium.name,
//        stadium.city,
//        stadium.country,
//        stadium.seats
//      )
//    )
  }

  override def findById(id: Long): Future[Option[Stadium]] = {
    collection
      .find(equal("_id", id))
      .map(d =>
        Stadium(
          d.getLong("_id"),
          d.getString("name"),
          d.getString("city"),
          d.getString("country"),
          d.getInteger("seats")
        )
      )
  }.toSingle().headOption()

  override def findAll(): Future[List[Stadium]] =
    collection.find().map{ d =>
      Stadium(
      d.getLong("_id"),
      d.getString("name"),
      d.getString("city"),
      d.getString("country"),
      d.getInteger("seats")
      )
    }.foldLeft(List.empty[Stadium])((list, stadium) => stadium :: list).head()

  override def findByCountry(country: String): Future[List[Stadium]] = {
    collection.find(equal("country", country)).map { d =>
      Stadium(
        d.getLong("_id"),
        d.getString("name"),
        d.getString("city"),
        d.getString("country"),
        d.getInteger("seats")
      )
    }.foldLeft(List.empty[Stadium])((list, stadium) => stadium :: list).head()
  }

  override def findByName(name: String): Future[Option[Stadium]] =
    collection
      .find(equal("name", name))
      .map { d =>
        Stadium(
          d.getLong("_id"),
          d.getString("name"),
          d.getString("city"),
          d.getString("country"),
          d.getInteger("seats")
        )
      }.toSingle().headOption()
}

