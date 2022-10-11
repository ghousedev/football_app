package models

import org.scalatestplus.play.PlaySpec

class TeamSpec extends PlaySpec {
  "Team" should {
    "have a name" in {
      val team = Team(20L, "Arsenal", Stadium(1, "London", "England", 60000))
      team.name mustBe "Arsenal"
    }
  }
}
