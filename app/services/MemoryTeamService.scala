package services
import models.Team

import scala.collection.mutable.ListBuffer
import scala.util.Try

class MemoryTeamService extends TeamService {
  val mutableList: ListBuffer[Team] = ListBuffer.empty
  override def create(team: Team): Unit = mutableList += team

  override def update(team: Team): Try[Team] = {
    Try(mutableList.find(t => t.id == team.id).getOrElse(
      Try(mutableList.filterInPlace(t => t.id != team.id).addOne(team))
    )
    )
  }

  override def findById(id: Long): Option[Team] = mutableList.find(t => t.id == id)

  override def findAll(): List[Team] = mutableList.toList

  override def findByName(name: String): Option[Team] = mutableList.find(t => t.name == name)
}
