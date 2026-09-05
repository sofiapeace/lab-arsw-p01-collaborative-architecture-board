# ARSW Collaborative Board — Lab #4: Architecture Foundation

> Backend modular, contratos y persistencia desacoplada.
> Asignatura: Arquitecturas de Software (ARSW) — 2026-2
> Repositorio starter: https://github.com/javi-toquica/lab-arsw-p01-collaborative-architecture-board

## Autor(es)

- Nombre: Jose Luis Lancheros Ayola y Gina Sofia Garcia Zapata

## 1. Descripción

Primera versión funcional del backend del **ARSW Collaborative Board**. En esta
entrega el Board se persiste únicamente en memoria del proceso; no hay base de
datos, autenticación, frontend funcional ni tiempo real (eso llega en labs
posteriores). El foco de este laboratorio es la **arquitectura base**:
separación de responsabilidades, inversión de dependencias (DIP), inyección de
dependencias por constructor y manejo uniforme de errores.

```
Web Client (later labs)
      |
      | HTTP/JSON
      v
REST Controller
      |
      v
Application Service
      |
      v
BoardRepository (port)
      |
      v
InMemoryBoardRepository (adapter)
```

## 2. Requisitos previos

- Java 21
- Maven 3.9+ (o el wrapper `mvnw` si el starter lo incluye)

## 3. Cómo ejecutar el proyecto

```bash
# Compilar
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La API queda disponible en: `http://localhost:8080/api/boards` (completar/ajustar
puerto si el starter usa otro).

## 4. Cómo ejecutar las pruebas

```bash
mvn test
```

- [ ] `mvn test` finaliza correctamente
- [ ] Incluye pruebas del `BoardApplicationService` (sin levantar servidor web)
- [ ] Incluye pruebas del contrato REST
- [ ] Incluye una prueba que demuestre el caso "Board inexistente"

## 5. Estructura del proyecto

```
src/main/java/.../collabboard
├── domain/model
│   ├── Board.java
│   ├── BoardElement.java
│   └── ElementType.java
├── application/port/out
│   └── BoardRepository.java
├── application/service
│   └── BoardApplicationService.java
└── infrastructure
    ├── persistence/InMemoryBoardRepository.java
    └── web/rest
        ├── BoardRestController.java
        ├── ApiError.java
        └── GlobalExceptionHandler.java
```

## 6. Contrato REST

Ver detalle completo en [`docs/api-contract.md`](docs/api-contract.md).

| Método | Recurso              | Propósito          | Respuesta esperada       |
|--------|----------------------|---------------------|---------------------------|
| POST   | `/api/boards`        | Crear Board          | 201 + Board creado        |
| GET    | `/api/boards/{id}`   | Consultar Board       | 200 + Board                |
| PUT    | `/api/boards/{id}`   | Reemplazar estado    | 200 + Board actualizado    |

## 7. Decisiones arquitectónicas

Ver [`docs/ADR-001-repository-boundary.md`](docs/ADR-001-repository-boundary.md)
para el detalle de contexto, decisión, consecuencias y trade-offs sobre el
límite entre `BoardRepository` (puerto) e `InMemoryBoardRepository` (adaptador).

## 8. Evidencia arquitectónica

| Artefacto            | Ubicación                                   |
|-----------------------|----------------------------------------------|
| Vista ArchiMate        | `docs/architecture/` (completar nombre archivo) |
| Diagrama de clases     | `docs/architecture/` (completar nombre archivo) |
| ADR                    | `docs/ADR-001-repository-boundary.md`         |
| Declaración uso de IA  | `docs/AI_USAGE.md`                            |

## 9. Checklist de criterios de aceptación (guía del laboratorio, sección 7)

- [ ] El proyecto ejecuta con Java 21 y Maven
- [ ] `mvn test` finaliza correctamente
- [ ] Es posible crear, consultar y reemplazar un Board mediante HTTP
- [ ] Un Board inexistente produce un error HTTP coherente y un `ApiError` uniforme
- [ ] El Application Service puede probarse sin levantar el servidor web
- [ ] Cambiar el adaptador de persistencia no exige modificar el controlador REST
- [ ] No hay referencias a clases de infraestructura dentro del paquete `domain`
- [ ] Los diagramas reflejan nombres y dependencias observables en el código

## 10. Pregunta de sustentación

> Si mañana `InMemoryBoardRepository` se reemplaza por otro adaptador, ¿qué
> componentes deberían cambiar y cuáles deberían permanecer intactos?

**Respuesta:** (completar una vez resuelto el laboratorio — debe coincidir con
el código entregado)

## 11. Alcance y restricciones (para referencia propia)

Fuera de alcance en este laboratorio: WebSockets/STOMP, colaboración
multiusuario, conectores gráficos, autenticación, base de datos externa,
microservicios, despliegue, frontend funcional, concurrencia avanzada.

## 12. Uso de IA

Ver declaración completa en [`docs/AI_USAGE.md`](docs/AI_USAGE.md).
