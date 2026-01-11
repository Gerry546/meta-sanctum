#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 [homeassistant|sanctum]" >&2
  exit 1
}

target="${1:-}"
[[ -z "$target" ]] && usage

case "$target" in
  homeassistant)
    machine="qemux86-64"
    distro="sanctum-dev"
    ;;
  sanctum)
    machine="qemux86-64n"
    distro="sanctum"
    ;;
  *)
    echo "Unknown target: $target" >&2
    usage
    ;;
esac

# Resolve workspace directory from script location
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
workspace_dir="$(cd "$script_dir/.." && pwd)"
workspace_basename="$(basename "$workspace_dir")"
bb_setup="$workspace_dir/sources/bitbake/bin/bitbake-setup"

if [[ ! -x "$bb_setup" ]]; then
  if [[ -f "$bb_setup" ]]; then
    chmod +x "$bb_setup" || true
  fi
fi

if [[ ! -x "$bb_setup" ]]; then
  echo "bitbake-setup not found or not executable at: $bb_setup" >&2
  exit 2
fi

echo "Initializing target '$target' (machine=$machine, distro=$distro)"
echo "Workspace: $workspace_dir"

"$bb_setup" \
  --setting default registry "$workspace_dir" \
  --setting default top-dir-prefix "$workspace_dir/.." \
  --setting default top-dir-name "$workspace_basename" \
  init --non-interactive sanctum-local "$target" "machine/$machine" "distro/$distro"

oe_core_dir="$workspace_dir/sources/openembedded-core"
github_url="git@github.com:Gerry546/openembedded-core.git"

if [[ -d "$oe_core_dir/.git" ]]; then
  echo "Configuring 'github' remote in: $oe_core_dir"
  pushd "$oe_core_dir" > /dev/null
  if git remote | grep -q '^github$'; then
    current_fetch=$(git remote get-url github || true)
    if [[ "$current_fetch" != "$github_url" ]]; then
      git remote set-url github "$github_url"
    fi
    current_push=$(git remote get-url --push github || true)
    if [[ "$current_push" != "$github_url" ]]; then
      git remote set-url --push github "$github_url"
    fi
  else
    git remote add github "$github_url"
    git remote set-url --push github "$github_url"
  fi
  echo "Remote 'github' is set to: $(git remote get-url github)"
  popd > /dev/null
else
  echo "Warning: $oe_core_dir is not a git repository yet; skipping remote setup." >&2
fi
