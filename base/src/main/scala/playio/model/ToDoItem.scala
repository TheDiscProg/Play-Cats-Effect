package playio.model

import play.api.libs.json.{Json, OWrites}

case class ToDoItem(
    id: Long,
    description: String,
    completed: Boolean = false
)

object ToDoItem {

  given toDoItemWrites: OWrites[ToDoItem] = Json.writes[ToDoItem]
}
