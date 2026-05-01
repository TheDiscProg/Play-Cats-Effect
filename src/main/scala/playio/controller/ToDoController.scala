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
    action.asyncIO { request =>
      if isAuthorized(request) then {
        for
          _ <- logger.info("Fetching all todo items")
          items <- todoService.getAll()
          response =
            if items.isEmpty then addHeaders(Results.NoContent)
            else addHeaders(Results.Ok(Json.toJson(items)))
        yield response
      } else {
        IO(Results.Unauthorized)
      }

    }

  def getById(id: Long): Action[AnyContent] =
    action.asyncIO { request =>
      if isAuthorized(request) then {
        for
          _ <- logger.info(s"Fetching todo item with id: $id")
          item <- todoService.getById(id)
          response =
            item match {
              case Some(value) => Results.Ok(Json.toJson(value))
              case None => Results.NotFound
            }
        yield response
      } else {
        IO(Results.Unauthorized)
      }

    }

  def create(): Action[AnyContent] =
    action.asyncIO { request =>
      if isAuthorized(request) then {
        val item = getNewToDoItem[NewToDoItem](request)
        for
          _ <- logger.info(s"Creating todo item: $item")
          response <- item match {
            case Some(value) =>
              todoService.create(value).map(id => Results.Created(Json.toJson(id)))
            case None => IO(Results.BadRequest)
          }
        yield response
      } else IO(Results.Unauthorized)
    }

  def delete(id: Long): Action[AnyContent] =
    action.asyncIO { request =>
      if isAuthorized(request) then {
        for
          _ <- logger.info(s"Deleting todo item with id: $id")
          response <- todoService.delete(id).map(_ => Results.NoContent)
        yield response
      } else IO(Results.Unauthorized)
    }

  private def getNewToDoItem[A](request: Request[AnyContent])(using fjs: Reads[A]): Option[A] = {
    val content = request.body
    val jsonObject = content.asJson
    jsonObject.flatMap(
      Json.fromJson[A](_).asOpt
    )
  }

  private def isAuthorized(request: Request[AnyContent]): Boolean = {
    val acceptHeader = request.headers.get("authorization")
    acceptHeader.isDefined && acceptHeader.get.contains("secret")
  }

  private def addHeaders(response: Result): Result =
    response.withHeaders(
      "authorization" -> "secret",
      "refresh" -> "1234567890"
    )

}
