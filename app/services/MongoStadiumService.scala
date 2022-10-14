package services

import models.{Player, Position, Stadium}
import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document.fromSpecific
import org.mongodb.scala.model.Aggregates.set
import org.mongodb.scala.model.Filters.equal

import javax.inject._
import scala.concurrent.Future
import scala.util.Try

class MongoStadiumService @Inject() (collection: MongoCollection[Document])
    extends AsyncStadiumService {

  override def create(stadium: Stadium): Unit = {
    val aStadium = stadiumToDocument(stadium)
    collection.insertOne(aStadium).subscribe(r => println(s"Successful insert: $r"), t => t.printStackTrace(), () => println("Insert Complete"))
  }

  override def update(id: Long, doc: Document): Future[Stadium] = {
    collection
      .findOneAndUpdate(equal("_id", id),
        set(
          model.Field("name", doc("name")),
          model.Field("city", doc("city")),
          model.Field("country", doc("country")),
          model.Field("capacity", doc("capacity"))
        )
      )
      .map(d => documentToStadium(d))
      .toSingle()
      .head()
  }

  override def findById(id: Long): Future[Option[Stadium]] = {
    collection
      .find(equal("_id", id))
      .map(d => documentToStadium(d))
  }.toSingle().headOption()

  override def findAll(): Future[List[Stadium]] =
    collection
      .find()
      .map(d => documentToStadium(d))
      .foldLeft(List.empty[Stadium])((list, stadium) => stadium :: list).head()

  override def findByCountry(country: String): Future[List[Stadium]] = {
    collection
      .find(equal("country", country))
      .map(d => documentToStadium(d))
      .foldLeft(List.empty[Stadium])((list, stadium) => stadium :: list).head()
  }

  override def findByName(name: String): Future[Option[Stadium]] =
    collection
      .find(equal("name", name))
      .map(d => documentToStadium(d))
      .toSingle().headOption()

  private def stadiumToDocument(stadium: Stadium): Document = {
    Document(
      "_id" -> stadium.id,
      "name" -> stadium.name,
      "country" -> stadium.country,
      "city" -> stadium.city,
      "capacity" -> stadium.seats
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

