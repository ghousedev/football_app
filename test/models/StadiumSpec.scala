package models

import org.scalatestplus.play.PlaySpec

class StadiumSpec extends PlaySpec {
  "Stadium" should {
    "have a name" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.name mustBe "The Emirates"
    }
    "have a long as an id" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.id mustBe 20L
    }
    "have a country" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England",10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.country mustBe "England"
    }
    "have a city" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England", 10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.city mustBe "London"
    }
    "have a number of seats" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England", 10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.seats mustBe 10
    }
    "have a stadium image" in {
      val stadium = Stadium(20L, "The Emirates", "London", "England", 10, "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg")
      stadium.imgUrl mustBe "https://tfcstadiums.com/wp-content/uploads/2022/02/18-Emirates-Stadium-Google-Earth-scaled.jpg"
    }
  }
}
