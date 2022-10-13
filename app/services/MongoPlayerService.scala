package services
import models.{Player, Position}
import org.mongodb.scala._
import javax.inject._
import play.api.Configuration
import scala.util.Try

class MongoPlayerService @Inject()(mongoCollection: MongoCollection[Document]) extends PlayerService {
  override def create(player: Player): Unit = ???

  override def update(player: Player): Try[Player] = ???

  override def findById(id: Long): Option[Player] = ???

  override def findAll(): List[Player] = ???

  override def findByFirstName(firstName: String): Option[Player] = ???

  override def findByLastName(lastName: String): Option[Player] = ???

  override def findByPosition(position: Position): List[Player] = ???
}
