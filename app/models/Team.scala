package models

import scala.util.Random

case class Team (name: String, stadium: Stadium) {
  val id: Long = new Random().nextLong()

}
