#!/bin/bash
# Script to source the Yocto build environment and run the devtool helper for a given recipe

set -e

if [ $# -ne 3 ]; then
    echo "Usage: $0 <action (add/finish)> <recipe-name> <build-dir-suffix>"
    exit 1
fi

ACTION="$1"
RECIPE_NAME="$2"
BUILD_DIR="build-$3"

# Source the Yocto build environment in the 'build' directory
source "$BUILD_DIR/build/init-build-env"

if [ "$ACTION" != "add" ] && [ "$ACTION" != "finish" ]; then
    echo "Invalid action: $ACTION. Use 'add' or 'finish'."
    exit 1
fi
echo "Running action: $ACTION for recipe: $RECIPE_NAME"
if [ "$ACTION" == "add" ]; then
    # Run the add helper
    devtool add python3-$RECIPE_NAME https://pypi.org/project/$RECIPE_NAME/
    exit 0
fi
if [ "$ACTION" == "finish" ]; then
    # Run the finish helper
    devtool finish -r -f python3-$RECIPE_NAME ../../sources/meta-homeassistant/recipes-devtools/python/
    exit 0
fi
