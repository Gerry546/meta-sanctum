#!/bin/bash
# Script to source the Yocto build environment and run the upgrade helper for a given recipe

set -e

if [ $# -ne 1 ]; then
    echo "Usage: $0 <recipe-name>"
    exit 1
fi

RECIPE_NAME="$1"

# Source the Yocto build environment in the 'build' directory
source "sources/poky/oe-init-build-env" build
cd ..

# Run the upgrade helper
./auto-upgrade-helper/upgrade-helper.py -c utils/upgrade-helper.conf "$RECIPE_NAME"
