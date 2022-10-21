package services

import models._

import scala.util.Try

trait PlayerService {
  def create(player: Player): Unit
  def update(player: Player): Try[Player]
  def findById(id: Long): Option[Player]
  def findAll(): List[Player]
  def findByFirstName(firstName: String): Option[Player]
  def findByLastName(surname: String): Option[Player]
  def findByPosition(position: Position): List[Player]
}
