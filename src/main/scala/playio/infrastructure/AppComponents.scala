package playio.infrastructure

import cats.effect.IO
import controllers.{Assets, AssetsComponents, AssetsConfiguration, AssetsFinder}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.routing.Router
import play.api.routing.sird.*
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, NoHttpFiltersComponents}
import playio.repository.ToDoRepository
import playio.service.ToDoService

import scala.concurrent.ExecutionContextExecutor

class AppComponents(context: ApplicationLoader.Context)
    extends BuiltInComponentsFromContext(context)
    with NoHttpFiltersComponents
    with AssetsComponents {

  given ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  given logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
  given af: AssetsFinder = assetsFinder

  val runtime = cats.effect.unsafe.IORuntime.global

  // Repository layer
  val repository = new ToDoRepository[IO]

  // Service layer
  val service = new ToDoService[IO](repository)

  // HTTP layer
  lazy val assetsConfig: AssetsConfiguration =
    AssetsConfiguration.fromConfiguration(configuration)

  val actionBuilder = new IOActionBuilder(controllerComponents, runtime)
  val todoController = new playio.controller.ToDoController(actionBuilder, service)
  val helloController = new playio.controller.HelloController(actionBuilder)

  // Router
  override def router: Router =
    Router.from {
      case GET(p"/assets/$file*") => assets.versioned(path = "/public", file)
      case GET(p"/hello") => helloController.fromQueryParameter()
      case GET(p"/hello/$name") => helloController.hello(name)
      case GET(p"/todo") => todoController.getAll()
      case GET(p"/todo/${long(id)}") => todoController.getById(id)
      case POST(p"/todo") => todoController.create()
      case DELETE(p"/todo/${long(id)}") => todoController.delete(id)
    }
}
