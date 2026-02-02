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
QEMU_CONF="${ROOT}/build/build-homeassistant/build/tmp/deploy/images/${MACHINE}/core-image-homeassistant-full.qemuboot.conf"

echo "Using QEMU config: $QEMU_CONF"

SSH_USER="root"
SSH_PORT=2222
SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR -p $SSH_PORT"
SCP_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o LogLevel=ERROR -P $SSH_PORT"

QEMU_PID_FILE="qemu-test.pid"
LOG_FILE="${ROOT}/pip-check.log"


# Ensure QEMU is shut down on exit (success or error)

cleanup() {
    echo "Cleaning up QEMU..."
    if [ -f "$QEMU_PID_FILE" ]; then
        kill "$(cat "$QEMU_PID_FILE")" 2>/dev/null || true
        rm -f "$QEMU_PID_FILE"
    fi
    # Remove qemu.log if present
    rm -f qemu.log
    rm -f "$LOG_FILE"
}
trap cleanup EXIT

# 1. Temporarily disable 'set -u' to avoid unbound variable error, then source Yocto environment and cd to workspace root
set +u
source build/build-homeassistant/build/init-build-env
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

# 4. Run `pip check` on the target and append the output to the test logfile
echo "Running 'pip check' on SSH target and appending to $LOG_FILE..."
# Allow non-zero exit from pip check so the script doesn't exit on package issues
set +e
ssh $SSH_OPTS $SSH_USER@localhost 'python3 -m pip check 2>&1 || pip check 2>&1 || true' >>"$LOG_FILE" 2>&1
PIP_EXIT=$?
set -e
echo "pip check exit code: $PIP_EXIT" >>"$LOG_FILE"

# 5. Parse the pip check logfile and split into missing / upgrade lists
MISSING_FILE="${ROOT}/pip-check-missing.txt"
UPGRADE_FILE="${ROOT}/pip-check-upgrade.txt"

rm -f "$MISSING_FILE" "$UPGRADE_FILE"

echo "Parsing $LOG_FILE to generate $MISSING_FILE and $UPGRADE_FILE..." >>"$LOG_FILE"

# Copy lines that indicate 'which is not installed' into the missing file
grep -F "which is not installed" "$LOG_FILE" >"$MISSING_FILE" || true

# Copy lines that indicate version mismatches (contain 'has requirement' and 'but you have') into the upgrade file
grep -F "has requirement" "$LOG_FILE" | grep -F "but you have" >"$UPGRADE_FILE" || true

echo "Wrote: $MISSING_FILE and $UPGRADE_FILE" >>"$LOG_FILE"
