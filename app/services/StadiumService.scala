package services

import models.Team

trait StadiumService {
  def create(team: Team)

  def update(team: Team)

  def findById(id: Long)

  def findAll()

  def findByCountry(firstName: String)
}
