package playio.controller

import cats.effect.IO
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import play.api.libs.json.*
import play.api.mvc.*
import playio.infrastructure.IOActionBuilder
import playio.model.NewToDoItem
import playio.service.ToDoServiceAlgebra

class ToDoController(
    action: IOActionBuilder,
    todoService: ToDoServiceAlgebra[IO]
) {

  private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def getAll(): Action[AnyContent] =
    action.asyncIO { _ =>
      for
        _ <- logger.info("Fetching all todo items")
        items <- todoService.getAll()
        response =
          if items.isEmpty then Results.NoContent
          else Results.Ok(Json.toJson(items))
      yield response
    }

  def getById(id: Long): Action[AnyContent] =
    action.asyncIO {_ =>
      for
        _ <- logger.info(s"Fetching todo item with id: $id")
        item <- todoService.getById(id)
        response =
          item match {
            case Some(value) => Results.Ok(Json.toJson(value))
            case None => Results.NotFound
          }
      yield response
    }

  def create(): Action[AnyContent] =
    action.asyncIO { request =>
      val item = getNewToDoItem[NewToDoItem](request)
      for
        _ <- logger.info(s"Creating todo item: $item")
        response <- item match {
          case Some(value) =>
            todoService.create(value).map(id => Results.Created(Json.toJson(id)))
          case None => IO(Results.BadRequest)
        }
      yield response

    }

  def delete(id: Long): Action[AnyContent] =
    action.asyncIO { _ =>
      for
        _ <- logger.info(s"Deleting todo item with id: $id")
        response <- todoService.delete(id).map(_ => Results.NoContent)
      yield response
    }

  private def getNewToDoItem[A](request: Request[AnyContent])(using fjs: Reads[A]): Option[A] = {
    val content = request.body
    val jsonObject = content.asJson
    jsonObject.flatMap(
      Json.fromJson[A](_).asOpt
    )
  }
}
