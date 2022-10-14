package services

import models.Stadium

import scala.concurrent.Future
import scala.util.Try

trait AsyncStadiumService {
  def create(stadium: Stadium): Unit

  def update(stadium: Stadium): Future[Option[Stadium]]

  def findById(id: Long): Future[Option[Stadium]]

  def findAll(): Future[List[Stadium]]

  def findByCountry(country: String): Future[List[Stadium]]

  def findByName(name: String): Future[Option[Stadium]]
}
