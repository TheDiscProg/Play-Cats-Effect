package playio.controller

import cats.effect.IO
import org.scalatestplus.play.*
import play.api.libs.json.*
import play.api.mvc.*
import play.api.test.*
import play.api.test.Helpers.*
import playio.infrastructure.IOActionBuilder
import playio.model.{NewToDoItem, ToDoItem}
import playio.service.ToDoServiceAlgebra

import scala.concurrent.{ExecutionContextExecutor, Future}

class ToDoControllerTest extends PlaySpec {

  given ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  val controller = new ToDoController(actionBuilder, mockService)

  "ToDoController" should {

    "return all items" in {
      val request = FakeRequest(GET, "/todo")

      val result: Future[Result] = controller.getAll()(request)

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(List(ToDoItem(1, "Test all item")))
    }
  }


  // --- Mocks service ---
  private lazy val mockService = new ToDoServiceAlgebra[IO]() {
    def getAll(): IO[List[ToDoItem]] =
      IO.pure(List(ToDoItem(1, "Test all item")))

    override def getById(id: Long): IO[Option[ToDoItem]] = ???

    override def create(item: NewToDoItem): IO[Long] = ???

    override def delete(id: Long): IO[Unit] = ???
  }

  // --- Action builder ---
  private lazy val cc = stubControllerComponents()
  private lazy val runtime = cats.effect.unsafe.IORuntime.global
  private lazy val actionBuilder = new IOActionBuilder(cc, runtime)

}
