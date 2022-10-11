package services
import models.Team

class MemoryTeamService extends TeamService {
  override def create(team: Team): Unit = ???

  override def update(team: Team): Unit = ???

  override def findById(id: Long): Team = ???

  override def findAll(): List[Team] = ???

  override def findByName(name: String): Team = ???
}
