#!/bin/bash
# Script to source the Yocto build environment and run the upgrade helper for a given recipe

set -e

if [ $# -ne 2 ]; then
    echo "Usage: $0 <recipe-name> $1 <build-dir>"
    exit 1
fi

RECIPE_NAME="$1"
BUILD_DIR="$2"

# Source the Yocto build environment in the 'build' directory
source "$BUILD_DIR/build/init-build-env"
cd ../..

# Run the upgrade helper
./auto-upgrade-helper/upgrade-helper.py -c utils/upgrade-helper.conf "$RECIPE_NAME"
