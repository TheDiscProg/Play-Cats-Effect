# Play with Cats-Effect

## Versions
* Scala 3.8.3
* Play Framework 3.0.10
* Cats Effect 3.7.0
* Log4cats-core/slf4j 2.6.0
* Scalatest 3.2.20

## Overview
This project demonstrates the integration of Play Framework with Cats-Effect, showcasing asynchronous programming and 
functional programming principles in Scala. Details of how this is done are presented below in the section "How It works".

## Why?
Integrating Cats Effect into Play improves correctness and maintainability by introducing referential transparency, 
explicit effect management, and structured concurrency. This allows business logic to remain pure and composable while 
isolating side effects, leading to safer resource handling, improved testability, and clearer separation between the 
HTTP layer and core application logic. For more information, see the section "Benefits of Using Cats Effect with Play."

## Program Structure
The directory layout for the project does not follow the standard Play directory structure, but is instead based on the 
SBT project layout:

- `app`:- As per standard Play app, for example, `app/views` for all `.scala.html` files
- `base`:- This is where we define all business logic, such as case classes and traits
- `public`:- As per standard Play public folders for CSS, images and JS files
- `src/main/resources`:- This is where we define all resources, such as configuration files
- `src/main/scala`: Scala source code for the application as per:
  - `playio.controllers`:- This is where we define all HTTP endpoints
  - `playio.infrastructure`:- This is where we define components that bridge the gap between Play and Cats Effect
  - `playio.services`:- This is where we define all business logic
  - `playio.repository`:- This is where we define all data access objects (DAOs)
- `test/scala`:- This is where we define all unit tests

### How It works
To bridge the gap between Play and Cats Effect, we use `infrastructure` package. This way, we can put all impure Play-specific
code, such as controllers, in the `controllers` package. We can put pure FP code in any other packages.

`infrastructure` package contains a number of classes that are responsible for:

* `AppLoader`: The Play entry point for bootstrapping the application.
* `AppComponents`: The main class that wires all components together (more information below)
* `IOActionBuilder`: This is the integration layer between Play's request/response model and Cats Effect's `IO` model (futher information below)


#### AppComponents
As we are not using DI, this class is responsible for creating all components that are needed by the application and wiring them together. We can use `IO` for the runtime execution context and `Resource` for managing resources. In addition, we also define `Router` by defining all routes, instead of using Play's `route` file.


#### IOActionBuilder
This is the integration layer between Play's request/response model and Cats Effect's `IO` model. It gets passed to any 
controllers so that it can map `IO` to `Action`.


## Benefits of Using Cats Effect with Play
Integrating Cats Effect into an application built with Play Framework—specifically through the introduction of an 
`IOActionBuilder` — represents a deliberate architectural choice to improve correctness, maintainability, and long-term
adaptability. Rather than relying solely on Play’s default `Future` based model, this approach establishes a clear 
separation between effectful computation and HTTP concerns, enabling a more disciplined and scalable system design.

At the core of this design is the adoption of functional programming (FP) principles, particularly **referential transparency** 
and **explicit effect management**. In a conventional Play application, `Future` is eager and begins execution immediately, 
which can lead to implicit side effects and reduced predictability. By contrast, Cats Effect’s `IO` type is lazy and 
purely descriptive: it models computations without executing them until explicitly interpreted. This allows developers 
to reason about code as values rather than actions, significantly improving reliability. The `IOActionBuilder` becomes 
the single, well-defined boundary where these effectful programs are executed, ensuring that side effects are controlled, 
visible, and auditable.

This architectural boundary enables a **functional core and imperative shell** pattern. Business logic, services, and 
repositories operate entirely within `IO` (or abstract effect types like `F[_]`), forming a pure and composable core. The Play 
controller layer, mediated by `IOActionBuilder`, acts as the imperative shell responsible for interpreting these 
computations into `Future[Result]`. This separation enhances clarity of responsibilities and prevents the leakage of 
framework-specific concerns into core logic. As a result, the system becomes easier to understand, evolve, and refactor.

A major benefit of this approach is composability. Cats Effect integrates seamlessly with the broader functional ecosystem, 
allowing developers to compose complex workflows using standard abstractions such as `map`, `flatMap`, and higher-level 
constructs like `Resource` for safe lifecycle management. Because all layers share a uniform effect type, there is no 
mismatch between components. This consistency enables modular design, where individual services can be developed, tested, 
and reused independently of the web framework. It also opens the door to advanced patterns such as tagless-final encoding, 
where business logic is written generically over an abstract effect type, further increasing flexibility.

Testability is another significant advantage. Since `IO` defers execution, tests can evaluate logic deterministically 
without the timing uncertainties associated with `Future`. Developers can construct and inspect effectful programs without 
running them immediately, or execute them in controlled environments. This reduces the need for brittle constructs like 
thread sleeps or complex asynchronous test harnesses. Furthermore, because side effects are explicit, it is easier to 
substitute implementations (e.g., mock services or in-memory repositories), leading to more robust and isolated unit tests.

The use of Cats Effect also introduces **structured concurrency and resource safety**, which are not natively provided 
by `Future`. Through constructs like fibers and `Resource`, the system gains predictable concurrency semantics, safe 
cancellation, and guaranteed cleanup of resources such as database connections or file handles. Embedding this within 
the `IOActionBuilder` ensures that all HTTP requests benefit from these guarantees, reducing the risk of leaks or 
inconsistent states under failure conditions.

From an operational perspective, the `IOActionBuilder` provides a centralised mechanism for handling cross-cutting 
concerns such as logging, error handling, metrics, and tracing. Because all requests flow through this abstraction, 
these concerns can be implemented once and applied consistently across the application. Importantly, they can be 
expressed within the same `IO` context, maintaining composability and avoiding fragmented or ad hoc implementations.

Finally, this approach offers a **strategic advantage in terms of future evolution**. By decoupling business logic from 
Play’s execution model, the system is no longer tightly bound to the framework. Should requirements change, it becomes 
significantly easier to migrate to alternative HTTP stacks such as http4s, since the core logic remains unchanged. 
This reduces long-term technical risk and protects the investment in application code.

In summary, adopting Cats Effect within Play via an `IOActionBuilder` is not merely a stylistic preference but a 
substantive architectural improvement. It enforces functional programming principles, enhances composability, 
improves testability, ensures resource safety, and creates a clean separation of concerns. 
For management, the tangible benefits are clear: a more reliable system, lower maintenance costs, improved developer 
productivity, and greater flexibility to adapt to future technological changes.
