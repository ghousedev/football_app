import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.Configuration
import services._

class Module extends AbstractModule {
  override def configure(): Unit = {
    @Provides
    def databaseProvider(configuration: Configuration): MongoDatabase = {
      val user = configuration.get[String]("mongo.username")
      val pass = configuration.get[String]("mongo.password")
      val dbName = configuration.get[String]("mongo.database")
      val database = {
        MongoClient(s"mongodb://$user:$pass@localhost:27017").getDatabase(dbName)
      }
      database
    }


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
    bind(classOf[AsyncTeamService])
      .to(classOf[MongoTeamService]).in(classOf[javax.inject.Singleton])
  }
}
