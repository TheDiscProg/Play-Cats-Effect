package playio.service

import playio.model.{NewToDoItem, ToDoItem}
import playio.repo.ToDoRepositoryAlgebra

class ToDoService[F[_]](
    repository: ToDoRepositoryAlgebra[F]
) extends ToDoServiceAlgebra[F] {

  override def getAll(): F[List[ToDoItem]] =
    repository.getAll()

  override def getById(id: Long): F[Option[ToDoItem]] = repository.getById(id)

  override def create(item: NewToDoItem): F[Long] =
    repository.create(item)

  override def delete(id: Long): F[Unit] =
    repository.delete(id)
}
