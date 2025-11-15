#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
JAR_PATH="$BACKEND_DIR/target/dorm-maintenance-backend-0.0.1-SNAPSHOT.jar"

echo "▶ Building backend jar..."
pushd "$BACKEND_DIR" > /dev/null
./mvnw clean package spring-boot:repackage >/dev/null
popd > /dev/null

echo "▶ Starting backend (CTRL+C to stop at the end)..."
java -jar "$JAR_PATH" > "$ROOT_DIR/backend-demo.log" 2>&1 &
BACKEND_PID=$!

cleanup() {
  echo ""
  echo "▶ Stopping backend (PID $BACKEND_PID)..."
  kill "$BACKEND_PID" >/dev/null 2>&1 || true
}
trap cleanup EXIT INT TERM

echo "▶ Installing frontend deps (if needed) and starting Vite dev server..."
pushd "$FRONTEND_DIR" > /dev/null
npm install >/dev/null
npm run dev
popd > /dev/null
