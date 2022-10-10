package services

import models.{Player, Position}

trait PlayerService {
  def create(player: Player)
  def update(player: Player)
  def findById(id: Long)
  def findAll()
  def findByFirstName(firstName: String)
  def findByLastName(lastName: String)
  def findByPosition(position: Position)
}
