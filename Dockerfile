FROM python:3.13-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app/backend

# Install system runtime & build dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    libpq-dev \
    tesseract-ocr \
    && rm -rf /var/lib/apt/lists/*

# Install python dependencies
COPY backend/requirements.txt /app/backend/requirements.txt
RUN pip install --no-cache-dir -r /app/backend/requirements.txt

# Copy backend application and machine learning model checkpoints
COPY backend/ /app/backend/
COPY ml/saved_models/ /app/ml/saved_models/

WORKDIR /app/backend

# Configure model checkpoint path and storage
ENV MODELS_DIR=/app/ml/saved_models
ENV STORAGE_DIR=/app/backend/storage
ENV EVIDENCE_DIR=/app/backend/storage/evidence

EXPOSE 8000

# Support dynamic PORT from Railway (defaults to 8000 if not set)
CMD ["sh", "-c", "uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8000}"]
