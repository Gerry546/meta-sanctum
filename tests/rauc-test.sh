#!/bin/bash
set -euo pipefail

MACHINE="qemux86-64n"
if [ $# -ge 1 ]; then
    MACHINE="$1"
fi
# Resolve workspace root (script lives in tests/)
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "Root directory: $ROOT"

# CONFIGURATION
QEMU_CONF="${ROOT}/build/build-sanctum/build/tmp/deploy/images/${MACHINE}/sanctum-rootfs.qemuboot.conf"

echo "Using QEMU config: $QEMU_CONF"

# Auto-detect RAUC bundle (*.raucb) in deploy/images/<MACHINE>
BUNDLE_CANDIDATES=("${ROOT}/build/build-sanctum/build/tmp/deploy/images/${MACHINE}"/*.raucb)
if [ "${#BUNDLE_CANDIDATES[@]}" -eq 0 ] || [ "${BUNDLE_CANDIDATES[0]##*/}" = "*.raucb" ]; then
    echo "ERROR: No .raucb bundle found in ${ROOT}/build-sanctum/build/tmp/deploy/images/${MACHINE}" >&2
    exit 1
fi
BUNDLE="${BUNDLE_CANDIDATES[0]}"
SSH_USER="root"
SSH_PORT=2222
SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR -p $SSH_PORT"
SCP_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR -P $SSH_PORT"
GUEST_BUNDLE_PATH="/tmp/$(basename "$BUNDLE")"

QEMU_PID_FILE="qemu-test.pid"


# Ensure QEMU is shut down on exit (success or error)

cleanup() {
    echo "Cleaning up QEMU..."
    if [ -f "$QEMU_PID_FILE" ]; then
        kill "$(cat "$QEMU_PID_FILE")" 2>/dev/null || true
        rm -f "$QEMU_PID_FILE"
    fi
    # Remove qemu.log if present
    rm -f qemu.log
}
trap cleanup EXIT

# 1. Temporarily disable 'set -u' to avoid unbound variable error, then source Yocto environment and cd to workspace root
set +u
source build/build-sanctum/build/init-build-env
set -u
cd "$ROOT"


# 2. Start QEMU with runqemu in the background, log output to qemu.log
echo "Launching QEMU in the background (headless)..."
if [ "$MACHINE" = "qemux86-64n" ]; then
    runqemu "$QEMU_CONF" slirp kvm nographic snapshot wic ovmf >qemu.log 2>&1 &
elif [ "$MACHINE" = "qemuarm64-a72" ]; then
    runqemu "$QEMU_CONF" slirp nographic wic >qemu.log 2>&1 &
else
    echo "ERROR: Unknown machine '$MACHINE'. Please update the script for this machine type." >&2
    exit 1
fi
QEMU_TERM_PID=$!
echo $QEMU_TERM_PID > "$QEMU_PID_FILE"
sleep 5  # Give QEMU time to start

# 3. Wait for SSH to become available
echo "Waiting for SSH to become available..."
for i in {1..60}; do
    if ssh $SSH_OPTS $SSH_USER@localhost 'echo ok' 2>/dev/null | grep -q ok; then
        echo "SSH is up."
        break
    fi
    sleep 2
done

# 4. Copy RAUC bundle to guest
echo "Copying RAUC bundle..."
scp $SCP_OPTS "$BUNDLE" "$SSH_USER@localhost:$GUEST_BUNDLE_PATH"

echo "Detecting initial booted slot using rauc status..."
RAUC_STATUS_BEFORE=$(ssh $SSH_OPTS $SSH_USER@localhost "rauc status" | sed 's/\x1b\[[0-9;]*m//g')
echo "$RAUC_STATUS_BEFORE" > rauc-status-before.txt
BOOTED_SLOT=$(echo "$RAUC_STATUS_BEFORE" | awk '/^Booted from:/ {print $3}')
echo "Booted slot: $BOOTED_SLOT"
if [[ "$BOOTED_SLOT" == "rootfs.0" ]]; then
    INIT_SLOT="rootfs.0"
    NEXT_SLOT="rootfs.1"
elif [[ "$BOOTED_SLOT" == "rootfs.1" ]]; then
    INIT_SLOT="rootfs.1"
    NEXT_SLOT="rootfs.0"
else
    echo "ERROR: Could not determine booted slot from rauc status. Got: $BOOTED_SLOT" >&2
    exit 1
fi
echo "Initial slot: $INIT_SLOT, will check for switch to $NEXT_SLOT after update."

# 6. Verify bundle is present
echo "Verifying bundle presence..."
ssh $SSH_OPTS $SSH_USER@localhost "ls -l $GUEST_BUNDLE_PATH"


# 7. Get RAUC info and save output (strip ANSI color codes)
echo "Getting RAUC info..."
ssh $SSH_OPTS $SSH_USER@localhost "rauc info $GUEST_BUNDLE_PATH" | sed 's/\x1b\[[0-9;]*m//g' > rauc-info.txt

# 8. Install the bundle
echo "Installing RAUC bundle..."
ssh $SSH_OPTS $SSH_USER@localhost "rauc install $GUEST_BUNDLE_PATH"

# 9. Reboot the guest
echo "Rebooting guest..."
ssh $SSH_OPTS $SSH_USER@localhost "reboot" || true

# 10. Wait for SSH after reboot
echo "Waiting for SSH after reboot..."
for i in {1..60}; do
    if ssh $SSH_OPTS $SSH_USER@localhost 'echo ok' 2>/dev/null | grep -q ok; then
        echo "SSH is up after reboot."
        break
    fi
    sleep 2
done

echo "Checking booted slot after update (should be $NEXT_SLOT)..."
RAUC_STATUS_AFTER=$(ssh $SSH_OPTS $SSH_USER@localhost "rauc status" | sed 's/\x1b\[[0-9;]*m//g')
echo "$RAUC_STATUS_AFTER" > rauc-status-after.txt
BOOTED_SLOT2=$(echo "$RAUC_STATUS_AFTER" | awk '/^Booted from:/ {print $3}')
echo "Booted slot after update: $BOOTED_SLOT2"
if [[ "$BOOTED_SLOT2" != "$NEXT_SLOT" ]]; then
    echo "ERROR: Expected to boot from $NEXT_SLOT after update, but got $BOOTED_SLOT2" >&2
    exit 1
fi

# Optionally, also print RAUC status
cat rauc-status-after.txt

# 11. Shutdown QEMU
echo "Shutting down QEMU..."
ssh $SSH_OPTS $SSH_USER@localhost "poweroff" || true
sleep 5
if [ -f "$QEMU_PID_FILE" ]; then
    kill "$(cat "$QEMU_PID_FILE")" || true
    rm -f "$QEMU_PID_FILE"
fi

echo "Test complete."