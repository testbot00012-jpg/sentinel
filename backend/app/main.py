import os
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
from contextlib import asynccontextmanager
import logging

from app.config.settings import settings
from app.database.session import engine, Base
from app.api.v1.endpoints import api_router
from app.auth.firebase import init_firebase

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("sentinel_ai")

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Initializing Sentinel AI database schema...")
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    init_firebase()
    logger.info("Sentinel AI Security Platform ready.")
    yield
    await engine.dispose()

app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="Real production-grade cybersecurity and fraud-protection API for Sentinel AI Android and Web.",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router, prefix=settings.API_V1_STR)

# Mount web directory if it exists
workspace_root = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
web_dir = os.path.join(workspace_root, "web")
web_static_dir = os.path.join(web_dir, "static")

if os.path.exists(web_static_dir):
    app.mount("/static", StaticFiles(directory=web_static_dir), name="static")

@app.get("/")
async def serve_index():
    index_file = os.path.join(web_dir, "index.html")
    if os.path.exists(index_file):
        return FileResponse(index_file)
    return {
        "status": "HEALTHY",
        "service": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "environment": settings.ENV
    }

@app.get("/health")
async def health_check():
    return {
        "status": "HEALTHY",
        "service": settings.PROJECT_NAME,
        "version": settings.VERSION,
        "environment": settings.ENV
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)

