package services
import models.{Position, Team}

class MemoryPlayerService extends PlayerService {
  override def create(team: Team): Unit = ???

  override def update(team: Team): Unit = ???

  override def findById(id: Long): Unit = ???

  override def findAll(): Unit = ???

  override def findByFirstName(firstName: String): Unit = ???

  override def findByLastName(lastName: String): Unit = ???

  override def findByPosition(position: Position): Unit = ???
}
