package playio.repository

import cats.Monad
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import playio.model.{NewToDoItem, ToDoItem}
import playio.repo.ToDoRepositoryAlgebra

import scala.collection.mutable.ArrayBuffer

class ToDoRepository[F[_]: {Monad, Logger}] extends ToDoRepositoryAlgebra[F] {

  private val todo = ArrayBuffer[ToDoItem](ToDoItem(1, "Go to work"), ToDoItem(2, "Go to the gym"))

  override def getAll(): F[List[ToDoItem]] =
    Monad[F].pure(todo.toList)

  override def getById(id: Long): F[Option[ToDoItem]] =
    Monad[F].pure(todo.find(_.id == id))

  override def create(item: NewToDoItem): F[Long] =
    for
      _ <- Logger[F].info(s"Creating new todo item: ${item.description}")
      allItems <- getAll()
      newId = allItems.size + 1
      newItem = ToDoItem(newId, item.description)
      _ <- Logger[F].info(s"Created new todo item with id: $newId")
      _ = todo += newItem
    yield newId

  override def delete(id: Long): F[Unit] = {
    val itemIndex = todo.indexWhere(_.id == id)
    if itemIndex != -1 then {
      todo.remove(itemIndex)
      Logger[F].info(s"Deleted todo item with id: $id") *>
        Monad[F].unit
    } else {
      Logger[F].warn(s"Todo item with id $id not found") *>
        Monad[F].unit
    }
  }
}
