package models

import org.junit.Before
import org.scalatestplus.play.PlaySpec

class PlayerSpec extends PlaySpec {

  "PlayerSpec" should {
      val player = Player(10L, 15L ,GoalKeeper, "Ian", "Grim")

    "have an id" in {
      player.id mustEqual 10L
    }

    "have a firstName" in {
      player.firstName mustEqual "Ian"
    }

    "have a teamId" in {
      player.teamId mustEqual 15L
    }

    "have a surname" in {
      player.surname mustEqual "Grim"
    }

    "have a position" in {
      player.position mustEqual GoalKeeper
    }
  }
}
