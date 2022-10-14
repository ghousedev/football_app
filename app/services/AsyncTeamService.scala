package services

import models.Team

import scala.concurrent.Future

trait AsyncTeamService {
  def create(team: Team): Unit

  def update(team: Team): Future[Option[Team]]

  def findById(id: Long): Future[Option[Team]]

  def findAll(): Future[List[Team]]

  def findByName(name: String): Future[Option[Team]]

}
