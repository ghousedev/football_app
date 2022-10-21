package services

import models._

import scala.concurrent.Future

trait AsyncPlayerService {
  def create(player: Player): Unit

  def update(player: Player): Future[Option[Player]]

  def findById(id: Long): Future[Option[Player]]

  def findAll(): Future[List[Player]]

  def findByFirstName(firstName: String): Future[Option[Player]]

  def findByLastName(surname: String): Future[Option[Player]]

  def findByPosition(position: Position): Future[List[Player]]
}
