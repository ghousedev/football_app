package services

import models.{Stadium, Team}
import org.scalatestplus.play.PlaySpec

import java.util.UUID
import scala.util.Random

class MemoryTeamServiceSpec extends PlaySpec {

  "MemoryTeamService" must {
    "return the size of the list after I create the team" in {
      val memoryTeamService = new MemoryTeamService()
      val team = Team("Grimsby", Stadium(new Random().nextLong(), "Grimsby", "England", 10))
      memoryTeamService.create(team)
      memoryTeamService.findAll().size mustBe 1
    }
    "find a team by an id and return that team if it is in there" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team("Grimsby", Stadium(10L, "Grimsby", "England", 10))
      memoryTeamService.create(team)
      val result = memoryTeamService.findById(team.id)
      result mustBe Some(team)
    }
    "find a team by an id that doesn't exist" in {
      val memoryTeamService = new MemoryTeamService
      val randomId = new Random().nextLong()
      val team = Team("Grimsby", Stadium(randomId, "Grimsby", "England", 10))
      memoryTeamService.create(team)
      val result = memoryTeamService.findById(10L)
      result mustBe Option.empty
    }
    "updating a team must change the values" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team("Grimsby", Stadium(10L, "Grimsby", "England", 10))
      memoryTeamService.create(team)
      val result = team.copy(stadium = Stadium(10L, "Grimsby", "England", 12))
      result mustBe Team("Grimsby", Stadium(10L, "Grimsby", "England", 12))
      memoryTeamService.findAll().size mustBe 1
    }
    "updating a team that doesn't exist" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team("Grimsby", Stadium(10L, "Grimsby", "England", 10))
      memoryTeamService.update(team)
      val result = team.copy(stadium = Stadium(10L, "Grimsby", "England", 12))
    }
  }

}
