package services
import models.Stadium

import scala.collection.mutable.ListBuffer
import scala.util.Try

class MemoryStadiumService extends StadiumService {
  val mutableList: ListBuffer[Stadium] = ListBuffer.empty

  override def create(stadium: Stadium): Unit =  mutableList += stadium

  override def update(stadium: Stadium): Try[Stadium] = {
    Try(mutableList.find(s => s.id == stadium.id).head).map(s => {
      mutableList.filterInPlace(s => s.id != stadium.id).addOne(stadium)
      s
    })
  }

  override def findById(id: Long): Option[Stadium] =
    mutableList.find(s => s.id == id)

  override def findAll(): List[Stadium] = mutableList.toList

  override def findByCountry(country: String): List[Stadium] =
    mutableList.filter(t => t.country == country).toList
}
