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

  override def findByFirstName(fn: String): Option[Player] =
    mutableList.find(t => t.firstName == fn)

  override def findByLastName(ln: String): List[Player] =
    mutableList.filter(t => t.surname == ln).toList

  override def findByPosition(pos: Position): List[Player] =
    mutableList.filter(t => t.position == pos).toList
}