package services

import models.{GoalKeeper, Player, Stadium, Team}
import org.scalatestplus.play.PlaySpec

class MemoryPlayerServiceSpec extends PlaySpec {
  "MemoryPlayerService" must {
    "return the size of the list after I create the player" in {
      val memoryPlayerService = new MemoryPlayerService()
      val player = Player(10L, 15L ,GoalKeeper, "Ian", "Grim")
      memoryPlayerService.create(player)
      memoryPlayerService.findAll().size mustBe 1
    }
    "find a Player by an id and return that Player if it is in there" in {
      val memoryPlayerService = new MemoryPlayerService
      val player = Player(10L, 15L, GoalKeeper, "Ian", "Grim")
      memoryPlayerService.create(player)
      val result = memoryPlayerService.findById(player.id)
      result mustBe Some(player)
    }
    "find a Player by an id that doesn't exist" in {
      val memoryPlayerService = new MemoryPlayerService
      val player = Player(10L, 15L, GoalKeeper, "Ian", "Grim")
      memoryPlayerService.create(player)
      val result = memoryPlayerService.findById(11L)
      result mustBe Option.empty
    }
    "updating a Player must change the values" in {
      val memoryPlayerService = new MemoryPlayerService
      val player = Player(10L, 15L, GoalKeeper, "Ian", "Grim")
      memoryPlayerService.create(player)
      val updatedPlayer = Player(10L, 15L, GoalKeeper, "Mister", "Grim")
      memoryPlayerService.update(updatedPlayer)
      val result = memoryPlayerService.findById(updatedPlayer.id).get
      result mustBe Player(10L, 15L, GoalKeeper, "Mister", "Grim")
      memoryPlayerService.findAll().size mustBe 1
    }
  }
}
