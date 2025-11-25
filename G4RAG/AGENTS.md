# Repository Guidelines

## Project Structure & Module Organization
- `src/`: FastAPI WebSocket app (`websocket_server.py`) exposed via `start_server.py`, RAG helpers (`embedding.py`, `weaviate_engine.py`, `batch_upload.py`, `search.py`), and a simple Vue chat view (`ChtBot.vue`).
- `main_graph/` and `sub_graph/`: LangGraph definitions that route queries, manage agent state, and execute KG-aware search flows.
- `prompts/`: Prompt templates and examples consumed by the LangGraph pipeline.
- `utils/`: Vector store config, example selectors, and shared helpers (checkpoints, streaming content parsing).
- `data/`: Sample node/relationship corpora used for ingestion; `test/data_test/` is a trimmed set for smoke checks. Avoid editing these in-place unless you are refreshing fixtures.
- `test/` and `test_websocket.py`: Pytest-style smoke tests plus a WebSocket client probe to validate the running service.
- `config.yaml`: Default service endpoints (Weaviate, Neo4j, LLM models). Override via environment where possible.

## Build, Test, and Development Commands
- Use Python 3.10+ and a virtual environment:
  ```
  python -m venv .venv
  .\.venv\Scripts\activate
  pip install -r requirements_websocket.txt
  ```
- Start the API/WebSocket server (FastAPI + Uvicorn): `python start_server.py` (listens on `0.0.0.0:8000`, WS route `/ws/rag`).
- Interactive LangGraph REPL for quick agent loops: `python -m main_graph.run_interactive`.
- WebSocket smoke check against a running server: `python test_websocket.py`.
- Batch ingest helper (requires reachable Weaviate and DASHSCOPE_API_KEY): `python -m src.run_batch_upload`.

## Coding Style & Naming Conventions
- Follow PEP 8 with 4-space indents; prefer type hints and clear function names that match their side effects.
- Keep modules focused (ingest, search, websocket) and co-locate private helpers near their use.
- For Vue, keep single-file components lean and align props/method names with the API payload fields.

## Testing Guidelines
- Default test runner: `python -m pytest test`. Tests assume external services (Weaviate, Aliyun DashScope) are reachable and credentials are set; mark or skip tests locally if endpoints are unavailable.
- Add new tests under `test/` with `test_*.py` naming. Use deterministic fixtures instead of calling production endpoints when possible.
- For integration checks, reuse `test_websocket.py` or add similar clients that log minimally and fail fast.

## Commit & Pull Request Guidelines
- Write imperative, present-tense commits with a short scope prefix when relevant (e.g., `ws: handle stream errors`). Reference issue IDs in the body when available.
- PRs should state intent, testing performed, and any config/env requirements. Include screenshots or sample payloads for UI or API-facing changes.
- Keep diffs focused; split behavioral changes from formatting or data refreshes.

## Security & Configuration Tips
- Do not commit real secrets; use environment variables to override values in `config.yaml` (Weaviate, Neo4j, DashScope keys).
- Treat `data/` as sample content; large or sensitive corpora should live outside the repo and be mounted/configured at runtime.
- Log levels are tuned to reduce noisy third-party output—preserve that behavior when adding new dependencies.

## G4RAG Guidelines

### Overview

- 包含基于 G4KG 的 RAG、embedding、检索实验代码（Python）。
- 可能依赖 GPU、Neo4j、向量数据库等外部服务。

### Agent Instructions (RAG / Python)

- 默认使用简体中文解释实验思路和代码修改。
- 避免自动运行长时间训练或大规模数据处理脚本：
  - 对于明显耗时任务，只给出命令和说明，不自动执行。
- 优先使用已有虚拟环境和依赖，不主动升级全局包版本。
- 对 Notebook (`.ipynb`) 的修改，优先给出“应如何改”的 Python 代码片段，
  让作者手动粘贴，而不是尝试直接操作 Notebook 结构。
- 不主动修改外部服务（Neo4j、Weaviate 等）的线上配置或数据结构，如确有需要，必须先说明风险和预期影响。
