package services
import models.{Stadium, Team}

class MemoryStadiumService extends StadiumService {
  override def create(stadium: Stadium): Unit = ???

  override def update(stadium: Stadium): Unit = ???

  override def findById(id: Long): Unit = ???

  override def findAll(): Unit = ???

  override def findByCountry(country: String): Unit = ???
}
