import com.google.inject.AbstractModule
import services._

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TeamService])
      .to(classOf[MemoryTeamService]).in(classOf[javax.inject.Singleton])
    bind(classOf[PlayerService])
      .to(classOf[MemoryPlayerService]).in(classOf[javax.inject.Singleton])
    bind(classOf[StadiumService])
      .to(classOf[MemoryStadiumService]).in(classOf[javax.inject.Singleton])
    bind(classOf[AsyncStadiumService])
      .to(classOf[MongoStadiumService]).in(classOf[javax.inject.Singleton])
    bind(classOf[AsyncPlayerService])
      .to(classOf[MongoPlayerService]).in(classOf[javax.inject.Singleton])
  }
}
