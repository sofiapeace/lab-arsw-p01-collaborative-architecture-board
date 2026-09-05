# ADR-001 — Repository Boundary

# Status
Accepted

# Context

The Collaborative Board needs to create, read and replace Board state. In this lab iteration, persistence is a single in-memory Map, but the lab guide is explicit that this will change: a future lab may swap the storage mechanism, and RA-02/RA-03 require that BoardApplicationService never depend on a concrete infrastructure class, only on an abstraction it owns.

The question this ADR answers: where exactly does the boundary between "business logic" and "how data is stored" sit, and who is allowed to depend on whom?

# Decision

BoardApplicationService (in application.service) depends exclusively on BoardRepository (in application.port.out), an interface owned by the application layer with three operations: save, findById, existsById. It never imports or references InMemoryBoardRepository.

InMemoryBoardRepository (in infrastructure.persistence) implements BoardRepository and is annotated @Repository. BoardApplicationService is annotated @Service and receives a BoardRepository through constructor injection. No class in the codebase writes new InMemoryBoardRepository() — Spring's IoC container performs component scanning at startup, finds the single bean implementing BoardRepository, and injects it.

Business rules that decide whether a write is allowed (e.g. "a replace must target a board that already exists") live in BoardApplicationService, not in the repository. InMemoryBoardRepository.save() is a plain upsert; it does not know or enforce that distinction.

# Positive consequences
The persistence adapter can be replaced (e.g. by a JPA-backed repository in a later lab) by adding a new class that implements BoardRepository and is the only such bean in the Spring context — without touching BoardApplicationService or BoardRestController. This satisfies the acceptance criterion "cambiar el adaptador de persistencia no exige modificar el controlador REST."
BoardApplicationService can be unit-tested without a Spring context at all: BoardApplicationServiceTest constructs it as new BoardApplicationService(new InMemoryBoardRepository()) directly, with no @SpringBootTest. This is verified, not assumed — those 4 tests pass standalone.
There is a single place (the service) where "does this operation make sense given the current state" is decided, instead of that logic being duplicated across repository implementations.

# Trade-off
For a feature this small (one entity, one in-memory map), the port/adapter split is arguably more indirection than the current functionality needs — a repository interface for a single-implementation HashMap wrapper is overhead if this project never grows past Lab # 4. The trade-off is accepted deliberately because the assignment evaluates the boundary itself, and because Lab #5 is stated to build directly on this structure.
BoardRepository's three methods are generic enough for the current use cases, but are shaped around what an in-memory map can do cheaply (existsById as a cheap key lookup). A future adapter needing richer queries (search by name, pagination, listing all boards) would require growing this interface and touching every implementation, including this one.

# Evidence / validation
grep -r "import edu.eci.arsw.collabboard.infrastructure" src/main/java/.../domain src/main/java/.../application returns no matches — confirmed directly against the delivered source, not asserted from memory.
BoardApplicationServiceTest (4 tests) instantiates the service with a concrete InMemoryBoardRepository manually, with no Spring context, and passes — demonstrating the service's only real dependency is the BoardRepository interface.
BoardRestControllerTest (5 tests, @SpringBootTest with a random port) exercises the full HTTP stack and passes, confirming the Spring-wired path (BoardRestController → BoardApplicationService → BoardRepository → InMemoryBoardRepository) works end-to-end without any manual wiring code.