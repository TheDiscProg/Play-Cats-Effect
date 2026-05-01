package playio.controller

import cats.effect.IO
import controllers.AssetsFinder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.mvc.{Action, AnyContent, Results}
import playio.infrastructure.IOActionBuilder

class HelloController(action: IOActionBuilder)(implicit assetsFinder: AssetsFinder) {

  private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
  def hello(name: String): Action[AnyContent] =
    action.asyncIO { request =>
      for
        _ <- logger.info(s"Displaying Hello with $name")
        response = Results.Ok(views.html.hello(name))
      yield response
    }

  def fromQueryParameter(): Action[AnyContent] =
    action.asyncIO { request =>
      val nameOption = request.getQueryString("name")
      nameOption match {
        case None => IO(Results.BadRequest)
        case Some(name) =>
          for
            _ <- logger.info(s"Displaying Hello from query parameter: $name")
            response = Results.Ok(views.html.hello(name))
          yield response
      }
    }

}
