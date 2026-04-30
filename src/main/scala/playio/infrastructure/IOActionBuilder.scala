package playio.infrastructure

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import play.api.mvc._
import scala.concurrent.ExecutionContext

class IOActionBuilder(
    cc: ControllerComponents,
    runtime: IORuntime
)(using ec: ExecutionContext)
    extends AbstractController(cc) {

  def asyncIO(
      block: Request[AnyContent] => IO[Result]
  ): Action[AnyContent] =
    Action.async { request =>
      block(request).unsafeToFuture()(runtime)
    }

}
