package services

import models.{Player, Position}

import scala.collection.mutable.ListBuffer
import scala.util.Try

class MemoryPlayerService extends PlayerService {
  val mutableList: ListBuffer[Player] = ListBuffer.empty

  override def findById(id: Long): Option[Player] =
    mutableList.find(t => t.id == id)

  override def create(player: Player): Unit = mutableList += player

  override def update(player: Player): Try[Player] = {
    Try(mutableList.find(t => t.id == player.id).head)
      .map(t => {
        mutableList.filterInPlace(t => t.id != player.id).addOne(player);
        player
      })
  }

  override def findAll(): List[Player] = mutableList.toList

  override def findByName(name: String): Option[Player] =
    mutableList.find(t => t.name == name)

  override def findByCountry(country: String): List[Player] =
    mutableList.filter(t => t.country == country).toList
}