#!/bin/sh

set -e

if [ "$#" -lt 4 ]; then
    echo "Usage: $0 <configuration-file|id|url> <bitbake-config-name> <machine/fragment> <distro/fragment> [additional bitbake-setup args...]"
    echo "Example: $0 homeassistant Stable machine/qemux86-64 distro/poky"
    exit 1
fi

CONFIG="$1"; shift
BB_CONFIG="$1"; shift
MACHINE_FRAGMENT="$1"; shift
DISTRO_FRAGMENT="$1"; shift

# Any remaining args are passed directly to bitbake-setup (e.g. --setting overrides)
EXTRA_ARGS="$@"

./sources/bitbake/bin/bitbake-setup \
    init \
    --non-interactive \
    --setup-dir-name build-$CONFIG \
    ${EXTRA_ARGS} \
    "$CONFIG" "$BB_CONFIG" "$MACHINE_FRAGMENT" "$DISTRO_FRAGMENT"
