package models

import org.scalatestplus.play.PlaySpec

class TeamSpec extends PlaySpec {
  "Team" should {
    "have a name" in {
      val team = Team(20L, "Arsenal", 10L)
      team.name mustBe "Arsenal"
    }
    "have a long as an id" in {
      val team = Team(20L, "Arsenal", 10L)
      team.id mustBe 20L
    }
    "have a long as a stadium id" in {
      val team = Team(20L, "Arsenal", 10L)
      team.stadiumId mustBe 10L
    }
  }
}
