#!/usr/bin/env python3
"""Backward-compatible wrapper for patching the Home Assistant layer from the CSV."""

from __future__ import annotations

import argparse
import sys

from ha_workflow import patch_layer


def main() -> int:
    parser = argparse.ArgumentParser(description="Update the Home Assistant layer from a parsed CSV")
    parser.add_argument("version", help="Home Assistant version/date")
    args = parser.parse_args()
    return patch_layer(args.version)


if __name__ == "__main__":
    sys.exit(main())