package services
import models.Team

class MemoryStadiumService extends StadiumService {
  override def create(team: Team): Unit = ???

  override def update(team: Team): Unit = ???

  override def findById(id: Long): Unit = ???

  override def findAll(): Unit = ???

  override def findByCountry(firstName: String): Unit = ???
}
