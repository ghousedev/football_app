import com.google.inject.AbstractModule
import services._

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TeamService])
      .to(classOf[MemoryTeamService])
    bind(classOf[PlayerService])
      .to(classOf[MemoryPlayerService])
    bind(classOf[StadiumService])
      .to(classOf[MemoryStadiumService])
  }
}
