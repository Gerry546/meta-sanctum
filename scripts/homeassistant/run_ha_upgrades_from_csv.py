#!/usr/bin/env python3
"""Backward-compatible wrapper for the recipe upgrade stage."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from ha_workflow import ROOT, run_recipe_upgrades


def main() -> int:
    parser = argparse.ArgumentParser(description="Run upgrades from the parsed Home Assistant CSV")
    parser.add_argument("-v", "--version", required=True, help="Home Assistant version/date")
    parser.add_argument(
        "--build-dir",
        default=str(ROOT / "build" / "build-homeassistant"),
        help="Yocto build directory that contains build/init-build-env",
    )
    parser.add_argument("--fail-fast", action="store_true", help="Stop after the first failed recipe upgrade")
    args = parser.parse_args()
    return run_recipe_upgrades(
        args.version,
        Path(args.build_dir).expanduser().resolve(),
        continue_on_error=not args.fail_fast,
    )


if __name__ == "__main__":
    sys.exit(main())