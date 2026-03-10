#!/bin/bash
# Script to source the Yocto build environment and run the upgrade helper for a given recipe

set -e

if [ $# -ne 2 ]; then
    echo "Usage: $0 <recipe-name> <build-dir>"
    exit 1
fi

RECIPE_NAME="$1"
BUILD_DIR="$2"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

source "$BUILD_DIR/build/init-build-env"

"$REPO_ROOT/auto-upgrade-helper/upgrade-helper.py" -c "$REPO_ROOT/utils/upgrade-helper.conf" "$RECIPE_NAME"