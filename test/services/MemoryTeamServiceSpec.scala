package services

import models.{Stadium, Team}
import org.scalatestplus.play.PlaySpec

import scala.util.Random

class MemoryTeamServiceSpec extends PlaySpec {

  "MemoryTeamService" must {
    "return the size of the list after I create the team" in {
      val memoryTeamService = new MemoryTeamService()
      val team = Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.create(team)
      memoryTeamService.findAll().size mustBe 1
    }
    "find a team by an id and return that team if it is in there" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.create(team)
      val result = memoryTeamService.findById(team.id)
      result mustBe Some(team)
    }
    "find a team by an id that doesn't exist" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.create(team)
      val result = memoryTeamService.findById(10L)
      result mustBe Option.empty
    }
    "updating a team must change the values" in {
      val memoryTeamService = new MemoryTeamService
      val team = Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.create(team)
      val updatedTeam = Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.update(updatedTeam)
      val result = memoryTeamService.findById(updatedTeam.id).get
      result mustBe Team(15L, "Grimsby", 10L, "https://someurl.com")
      memoryTeamService.findAll().size mustBe 1
    }

  }

}
