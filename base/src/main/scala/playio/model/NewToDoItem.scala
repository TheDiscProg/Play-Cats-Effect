package playio.model

import play.api.libs.json.{Json, OFormat}

case class NewToDoItem(
    description: String,
    completed: Boolean = false
)

object NewToDoItem {

  given newTodoListJson: OFormat[NewToDoItem] = Json.format[NewToDoItem]
}
