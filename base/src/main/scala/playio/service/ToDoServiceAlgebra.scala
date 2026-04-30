package playio.service

import playio.model.{NewToDoItem, ToDoItem}

trait ToDoServiceAlgebra[F[_]] {

  def getAll(): F[List[ToDoItem]]

  def getById(id: Long): F[Option[ToDoItem]]

  def create(item: NewToDoItem): F[Long]

  def delete(id: Long): F[Unit]
}
