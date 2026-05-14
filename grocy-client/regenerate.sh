#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SPEC_URL="https://raw.githubusercontent.com/grocy/grocy/master/grocy.openapi.json"
SPEC_FILE="$SCRIPT_DIR/grocy.openapi.json"
OUT_DIR="$SCRIPT_DIR/out/kotlin-kmp"

echo ">>> Downloading Grocy OpenAPI spec..."
curl -fsSL "$SPEC_URL" -o "$SPEC_FILE"

echo ">>> Running OpenAPI Generator..."
podman run --rm \
  -v "$SCRIPT_DIR:/local:Z" \
  docker.io/openapitools/openapi-generator-cli generate \
  -i /local/grocy.openapi.json \
  -g kotlin \
  --library multiplatform \
  --additional-properties dateLibrary=kotlinx-datetime \
  -o /local/out/kotlin-kmp \
  --skip-validate-spec

echo ">>> Replacing generated sources..."
rm -rf "$SCRIPT_DIR/src/commonMain" "$SCRIPT_DIR/src/test" "$SCRIPT_DIR/docs"
cp -r "$OUT_DIR/src/commonMain" "$SCRIPT_DIR/src/commonMain"
cp -r "$OUT_DIR/src/test"       "$SCRIPT_DIR/src/test"
cp -r "$OUT_DIR/docs"           "$SCRIPT_DIR/docs"

echo ">>> Updating generator manifest..."
cp "$OUT_DIR/.openapi-generator/FILES"   "$SCRIPT_DIR/.openapi-generator/FILES"
cp "$OUT_DIR/.openapi-generator/VERSION" "$SCRIPT_DIR/.openapi-generator/VERSION"

echo ">>> Cleaning up..."
rm -rf "$SCRIPT_DIR/out" "$SPEC_FILE"

echo "Done. Run './gradlew :grocy-client:compileKotlinJvm' to verify."
