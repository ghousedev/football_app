package services

import models.Stadium
import org.scalatestplus.play.PlaySpec


class MemoryStadiumServiceSpec extends PlaySpec {

  "MemoryStadiumService" must {
    "return the size of the list after I create the stadium" in {
      val memoryStadiumService = new MemoryStadiumService()
      val stadium = Stadium(10L, "Emirates Stadium", "London", "England", 60000)
      memoryStadiumService.create(stadium)
      memoryStadiumService.findAll().size mustBe 1
    }
    "find a stadium by an id and return that stadium if it is in there" in {
      val memoryStadiumService = new MemoryStadiumService
      val stadium = Stadium(10L, "Emirates Stadium", "London", "England", 60000)
      memoryStadiumService.create(stadium)
      val result = memoryStadiumService.findById(stadium.id)
      result mustBe Some(stadium)
    }
    "find a stadium by an id that doesn't exist" in {
      val memoryStadiumService = new MemoryStadiumService
      val stadium = Stadium(10L, "Emirates Stadium", "London", "England", 60000)
      memoryStadiumService.create(stadium)
      val result = memoryStadiumService.findById(11L)
      result mustBe Option.empty
    }
    "updating a stadium must change the values" in {
      val memoryStadiumService = new MemoryStadiumService
      val stadium = Stadium(10L, "Emirates Stadium", "London", "England", 60000)
      memoryStadiumService.create(stadium)
      val updatedStadium = Stadium(10L, "Emirates Stadium", "London", "England", 62000)
      memoryStadiumService.update(updatedStadium)
      val result = memoryStadiumService.findById(updatedStadium.id).get
      result mustBe Stadium(10L, "Emirates Stadium", "London", "England", 62000)
      memoryStadiumService.findAll().size mustBe 1
    }

  }

}
