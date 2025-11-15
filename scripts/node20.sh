#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
NODE_DIR="$PROJECT_ROOT/.tools/node-v20.19.0-darwin-x64/bin"

if [[ ! -d "$NODE_DIR" ]]; then
  echo "Node 20 binary not found in $NODE_DIR. Run ./scripts/install-node20.sh first." >&2
  exit 1
fi

export PATH="$NODE_DIR:$PATH"
export NPM_CONFIG_CACHE="$PROJECT_ROOT/.npm-cache"
mkdir -p "$NPM_CONFIG_CACHE"
exec "$@"
