#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TOOLS_DIR="$PROJECT_ROOT/.tools"
NODE_VERSION="v20.19.0"
NODE_DIST="node-$NODE_VERSION-darwin-x64"
TARBALL="$NODE_DIST.tar.xz"

mkdir -p "$TOOLS_DIR"
cd "$TOOLS_DIR"

if [[ ! -f "$TARBALL" ]]; then
  echo "▶ Downloading Node $NODE_VERSION..."
  curl -LO "https://nodejs.org/dist/$NODE_VERSION/$TARBALL"
fi

if [[ -d "$NODE_DIST" ]]; then
  echo "▶ Node $NODE_VERSION already extracted in $TOOLS_DIR/$NODE_DIST"
else
  echo "▶ Extracting..."
  tar -xf "$TARBALL"
fi

echo "Node $NODE_VERSION ready. Use ./scripts/node20.sh <command> to run with it."
