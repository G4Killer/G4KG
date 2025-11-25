# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/example/g4kgdemo`: Spring Boot entry `G4kgDemoApplication` plus `controller`, `service`/`impl`, `repository` (basic/diy/enrichment/visualization), `dto`, `model`, `utils`, and `config`.
- `src/main/resources`: `application.properties` for Neo4j/server settings; `static` and `templates` reserved for web assets.
- `src/test/java/com/example/g4kgdemo`: JUnit 5/Spring Boot tests mirroring main packages (controllers, services, repositories).
- Root: `build.gradle`, `settings.gradle`, `gradle/` wrapper, `build/` for compiled outputs; use wrapper scripts `gradlew`/`gradlew.bat`.

## Build, Test, and Development Commands
- `./gradlew bootRun` (or `.\gradlew.bat bootRun` on Windows): start the API locally on `http://localhost:8081`.
- `./gradlew test`: run unit/integration tests; ensure Neo4j is reachable or override `spring.neo4j.*` env vars for isolated runs.
- `./gradlew build`: compile, run tests, and emit the JAR under `build/libs`.
- `./gradlew clean`: remove compiled artifacts before a fresh build.

## Coding Style & Naming Conventions
- Java 17 + Spring Boot 3.3; default to 4-space indentation with brace placement matching existing files.
- Classes use `PascalCase`; methods/fields use `camelCase`; controllers/services/repositories/DTOs/models keep existing suffix patterns.
- Favor Lombok annotations (`@Data`, `@Builder`) where already used; keep validation/null checks at service boundaries.
- REST endpoints stay in `controller`, delegating to services; repositories encapsulate Neo4j queries.

## Testing Guidelines
- Tests live under `src/test/java` and follow the `*Test.java` pattern mirroring package paths.
- Use JUnit 5 with `@SpringBootTest` for integration coverage; mock dependencies only when external calls are unnecessary.
- For Neo4j-dependent tests, supply setup/cleanup data or point tests to an isolated Neo4j URI to avoid polluting shared instances.

## Commit & Pull Request Guidelines
- No repository commit history is available here; use concise, present-tense messages (e.g., `Add subgraph enrichment endpoint`).
- PRs should include purpose, key changes, test evidence (`./gradlew test` output or manual steps), and any config/Neo4j prerequisites.
- Link tracking tickets/issues when present and call out schema changes or new external dependencies.

## Security & Configuration Tips
- `src/main/resources/application.properties` contains connection details; prefer environment-specific overrides and avoid committing secrets.
- API listens on `0.0.0.0:8081`; adjust via `server.address`/`server.port` for deployments.

## G4KG Backend Guidelines

### Backend Overview

- Spring Boot + Gradle 项目。
- 主要包结构：
  - `controller`：REST 接口层；
  - `service` / `service.impl`：业务逻辑；
  - `repository`：数据访问（包括 Neo4j / JPA）；
  - `model` / `entity`：实体与关系；
  - `dto`：数据传输对象；
  - `utils`：工具类。

### Build & Run

- `./gradlew bootRun`：启动后端服务。
- `./gradlew test`：运行测试（如有）。

### Agent Instructions (Backend / Spring Boot)

- **语言与解释**
  - 和作者交流时使用简体中文。
  - 对接口、Service 重构，请先给出 UML 风格的文字说明（谁调用谁），再贴代码片段。

- **架构约束**
  - 保持 Controller-Service-Repository 分层，不要把业务逻辑直接堆在 Controller 中。
  - 对 Neo4j 访问优先使用现有 Repository 模式 / 模板类，不擅自改动底层配置。

- **修改范围**
  - 修改 `application.properties` 或 Neo4j 连接配置前，先说明修改的目的和对现有环境的影响。
  - 避免引入新的框架（如重新接管安全组件、Web 层），除非作者明确要求。

- **代码风格**
  - 使用 `@Slf4j` 或现有日志方式记录关键错误，不随意 `System.out.println`。
  - 异常处理统一通过自定义异常 + 全局异常处理类（如有），不要到处捕获然后吞掉。

- **测试与验证**
  - 对重要逻辑/查询改动，建议补充或更新单元测试/集成测试。
  - 在给出的建议中列出推荐运行的 Gradle 命令（例如 `./gradlew test`）。

- **数据安全**
  - 不在仓库中写死密码、token 等敏感字段。
  - 对可能造成大规模图数据改写的接口（批量删除/更新），不要擅自增加自动调用逻辑。
