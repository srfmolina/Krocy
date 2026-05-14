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
  --type-mappings "DateTime=kotlin.String,boolean=kotlin.Int" \
  -o /local/out/kotlin-kmp \
  --skip-validate-spec

echo ">>> Preserving custom files not tracked by the generator..."
PROTECTED_DIR="$(mktemp -d)"
# Read relative paths from .openapi-generator-ignore (skip comments and blank lines)
while IFS= read -r pattern; do
  [[ "$pattern" =~ ^#.*$ || -z "$pattern" ]] && continue
  src="$SCRIPT_DIR/$pattern"
  if [ -f "$src" ]; then
    rel_dir="$(dirname "$pattern")"
    mkdir -p "$PROTECTED_DIR/$rel_dir"
    cp "$src" "$PROTECTED_DIR/$pattern"
  fi
done < "$SCRIPT_DIR/.openapi-generator-ignore"

echo ">>> Replacing generated sources..."
rm -rf "$SCRIPT_DIR/src/commonMain" "$SCRIPT_DIR/src/test" "$SCRIPT_DIR/docs"
cp -r "$OUT_DIR/src/commonMain" "$SCRIPT_DIR/src/commonMain"
cp -r "$OUT_DIR/src/test"       "$SCRIPT_DIR/src/test"
cp -r "$OUT_DIR/docs"           "$SCRIPT_DIR/docs"

echo ">>> Fixing boolean default values (false/true → 0/1 for kotlin.Int? fields)..."
find "$SCRIPT_DIR/src/commonMain" -name "*.kt" -exec \
  sed -i 's/kotlin\.Int? = false/kotlin.Int? = 0/g; s/kotlin\.Int? = true/kotlin.Int? = 1/g' {} +

echo ">>> Restoring custom files..."
cp -r "$PROTECTED_DIR/." "$SCRIPT_DIR/"
rm -rf "$PROTECTED_DIR"

echo ">>> Updating generator manifest..."
cp "$OUT_DIR/.openapi-generator/FILES"   "$SCRIPT_DIR/.openapi-generator/FILES"
cp "$OUT_DIR/.openapi-generator/VERSION" "$SCRIPT_DIR/.openapi-generator/VERSION"

echo ">>> Cleaning up..."
rm -rf "$SCRIPT_DIR/out" "$SPEC_FILE"

echo "Done. Run './gradlew :grocy-client:compileKotlinJvm' to verify."
