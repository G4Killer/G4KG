# Repository Guidelines

## Project Structure & Module Organization
- `G4KG_BACKEND/g4kg-demo/`: Spring Boot (Java 17) service; main code in `src/main/java`, resources in `src/main/resources`, integration/unit tests in `src/test/java`.
- `G4KG_FRONT/`: Vue 3 SPA with Element Plus; entry files in `src/`, static assets in `public/`, built artifacts in `dist/`.
- `G4RAG/`: FastAPI/WebSocket RAG service; core logic in `src/`, workflow graphs in `main_graph/` and `sub_graph/`, prompts in `prompts/`, tests/examples in `test/`.

## Build, Test, and Development Commands
- Backend (run inside `G4KG_BACKEND/g4kg-demo/`):
  - `./gradlew bootRun` – start the Spring service.
  - `./gradlew build` – compile and package the app.
  - `./gradlew test` – run JUnit/Testcontainers suite.
- Frontend (run inside `G4KG_FRONT/`):
  - `npm install` – install dependencies.
  - `npm run serve` – dev server with hot reload.
  - `npm run build` – production build to `dist/`.
  - `npm run lint` – lint Vue/JS code.
- RAG service (run inside `G4RAG/`):
  - `python -m venv .venv && .venv\\Scripts\\activate` then `pip install -r requirements_websocket.txt`.
  - `python start_server.py` – launch FastAPI/uvicorn WebSocket backend.
  - `pytest test` – execute Python tests (add `-q` for terse output).

## Coding Style & Naming Conventions
- Java: keep 4-space indents, Lombok for boilerplate; follow Spring naming (controllers/services/repos) and package-by-feature under `com.example`.
- Vue: single-file components with `<script setup>`, PascalCase component names, kebab-case file names, 2-space indents; respect ESLint (`plugin:vue/vue3-essential`).
- Python: PEP8 with snake_case, prefer type hints in new modules; keep config-driven values in `config.yaml` or env vars.

## Testing Guidelines
- Favor small, deterministic tests; integration tests using Neo4j Testcontainers already enabled in backend.
- Place Vue component tests alongside components if added; lint must pass before merging.
- Python tests live under `test/`; mock external services and keep sample data in `test/data_test`.

## Commit & Pull Request Guidelines
- Use concise, imperative commit subjects (e.g., `Add neo4j query service`, `Fix chat panel layout`); group related changes per module when possible.
- Include brief body lines for motivation/impact; reference tickets/issue IDs when available.
- PRs should describe scope, testing done (`./gradlew test`, `npm run lint`, `pytest test`), and any configuration steps; add screenshots/GIFs for frontend changes.

## Security & Configuration Tips
- Do not commit secrets; prefer environment variables for tokens such as `DASHSCOPE_API_KEY`, Neo4j creds, or external hosts. Replace sample values in `config.yaml` before deploying.
- Keep `server.log` out of commits; validate that test containers and local services are reachable before running integration suites.

## G4KG Project Guidelines

### Project Overview

- 本仓库包含 G4KG 知识图谱的完整工程：
  - `G4KG_BACKEND`：Spring Boot + Neo4j 的后端服务；
  - `G4KG_FRONT`：Vue 3 + Element Plus 的前端可视化与 ChatBot；
  - `G4RAG`：基于 G4KG 的 RAG / 实验性脚本。

### Build & Run (High Level)

- 后端：见 `G4KG_BACKEND/AGENTS.md`。
- 前端：见 `G4KG_FRONT/AGENTS.md`。
- RAG：见 `G4RAG/AGENTS.md`。

### Global Coding & Collaboration Rules

- 所有代码尽量保持小步修改，配合 Git 提交（如有）；不要一次性重写大模块。
- 避免引入新的第三方依赖，如确有需要，请先给出理由和影响评估。
- 禁止提交包含敏感信息的配置（账号、密码、IP 等）。

### Agent Instructions (GLOBAL)

- **Language**
  - All natural-language explanations and summaries must be in **Simplified Chinese by default**.
  - Only switch to English if the user explicitly asks for it.

- **Git & Version Control**
  - Do **not** run `git add`, `git commit`, `git push`, `git reset`, `git rebase` or any history-rewriting commands
    unless I explicitly ask for them.
  - It is OK to use `git status` and `git diff` to inspect the repo state, but **leave staging and commits to me**.
  - Never delete dependency/build directories such as `node_modules/`, `dist/`, `build/`, `.venv/`, `__pycache__/`,
    `.idea/` unless I explicitly request a cleanup.

- **语言与解释**
  - 默认使用 **简体中文** 与作者沟通和解释修改思路。
  - 给出修改方案时，优先用“摘要 + 重点 diff”，而不是整段粘贴源码，
    尤其是带有大量中文注释的文件，以减少终端显示乱码。

- **文件与目录**
  - 不要擅自修改大规模数据文件，除非明确要求。
  - 对配置文件（如 `application.properties`, `vue.config.js`）的修改要先说明影响范围。

- **命令与安全**
  - 默认只运行本地构建/测试命令，不在终端中主动发起 curl/wget 等外部网络请求。
  - 允许代码在运行过程中访问其正常依赖的外部服务（如 Neo4j、向量库、LLM API），但不要为了“试一下”随意改动线上配置。
  - 在未经允许的情况下请不要执行任何 git 命令（包括 git add、git commit、git push）
  - 在未经允许的情况下不要删除依赖目录
