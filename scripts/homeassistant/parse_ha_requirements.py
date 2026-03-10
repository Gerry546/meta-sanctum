#!/usr/bin/env python3
"""Backward-compatible wrapper for the Home Assistant requirements parser."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from ha_workflow import cleanup_repo, get_repo, parse_manifests, save_summary_csv


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("-v", "--version", help="HA Version", type=str, default="")
    parser.add_argument(
        "-u",
        "--upgrade",
        help="Only print packages which need upgrading",
        type=str,
        default="n",
        choices=["y", "n"],
    )
    parser.add_argument(
        "-c",
        "--clean",
        help="Clean HA Core Repository after parsing",
        type=str,
        default="y",
        choices=["y", "n"],
    )
    parser.add_argument(
        "-i",
        "--integrate",
        help="Show only available configuration",
        type=str,
        default="n",
        choices=["y", "n"],
    )
    return parser.parse_args()


def main() -> int:
    args = parse_arguments()
    ha_path = Path(__file__).resolve().parent / "HA"
    actual_version = get_repo(ha_path.resolve(), args.version)
    rows = parse_manifests(
        ha_path.resolve(),
        upgrade_only=args.upgrade == "y",
        integrations_only=args.integrate == "y",
    )
    save_summary_csv(rows, actual_version)
    if args.clean == "y":
        cleanup_repo(ha_path.resolve())
    return 0


if __name__ == "__main__":
    sys.exit(main())