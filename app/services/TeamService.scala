package services

import models.Team

trait TeamService {
  def create(team: Team): Unit

  def update(team: Team): Unit

  def findById(id: Long): Team

  def findAll(): List[Team]

  def findByName(name: String): Team
}
