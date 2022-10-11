package services

import models.Stadium

trait StadiumService {
  def create(stadium: Stadium)

  def update(stadium: Stadium)

  def findById(id: Long)

  def findAll()

  def findByCountry(country: String)
}
