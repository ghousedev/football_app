package models

import org.scalatestplus.play.PlaySpec

class StadiumSpec extends PlaySpec {
  "Stadium" should {
    "have a name" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10)
      stadium.name mustBe "The Emirates"
    }
    "have a long as an id" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10)
      stadium.id mustBe 20L
    }
    "have a country" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10)
      stadium.country mustBe "England"
    }
    "have a city" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England", 10)
      stadium.city mustBe "London"
    }
    "have a number of seats" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England", 10)
      stadium.seats mustBe 10
    }
  }
}
