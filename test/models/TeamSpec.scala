package models

import org.scalatestplus.play.PlaySpec

class TeamSpec extends PlaySpec {
  "Team" should {
    "have a name" in {
      val team = Team(20L, "Arsenal", 10L)
      team.name mustBe "Arsenal"
    }
  }
}
