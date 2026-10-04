from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
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
    description="Real production-grade cybersecurity and fraud-protection API for Sentinel AI Android.",
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
