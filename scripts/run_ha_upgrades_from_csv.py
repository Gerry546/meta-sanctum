#!/usr/bin/env python3
"""
Read upgraded_components_<date>.csv from meta-homeassistant/scripts and run
`scripts/run-upgrade-helper.sh` for every package found that has a recipe in
`sources/meta-homeassistant/recipes-devtools/python`.

The script prompts the user for the date/version (the same value used when
running `update_homeassistant_components.py`) and continues on errors.
"""

import argparse
import csv
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SCRIPTS_DIR = ROOT / 'sources' / 'meta-homeassistant' / 'scripts'
RECIPES_DIR = ROOT / 'sources' / 'meta-homeassistant' / 'recipes-devtools' / 'python'
SCRIPTS_DIR = ROOT / 'scripts'
RUNNER = SCRIPTS_DIR / 'run-upgrade-helper.sh'
LOG_DIR = SCRIPTS_DIR / 'upgrade_logs'


def main():
    parser = argparse.ArgumentParser(description='Run upgrades from upgraded_components CSV')
    parser.add_argument('-v', '--version', help='Home Assistant version/date (CSV filename suffix)')
    args, unknown = parser.parse_known_args()

    if args.version:
        version = args.version
    else:
        # If no version supplied via CLI, prompt interactively
        version = input('Enter Home Assistant version/date (used as CSV filename): ').strip()
    if not version:
        print('No version provided, aborting')
        sys.exit(1)

    csv_path = SCRIPTS_DIR / f'upgraded_components_{version}.csv'
    if not csv_path.exists():
        print(f'CSV not found: {csv_path}')
        sys.exit(1)

    if not RUNNER.exists():
        print(f'Runner script not found: {RUNNER}')
        sys.exit(1)

    upgraded = []
    failed = []

    with csv_path.open(newline='') as f:
        reader = csv.DictReader(f)
        for row in reader:
            pkg = row.get('package') or row.get('package ')
            old_ver = row.get('old_version')
            new_ver = row.get('new_version')
            if not pkg or not new_ver or not old_ver:
                continue
            # Map package name to recipe filename form
            # package in CSV is like 'python3-anel-pwrctrl-homeassistant'
            base_pkg = pkg
            if base_pkg.startswith('python3-'):
                base_pkg = base_pkg
            else:
                base_pkg = f'python3-{base_pkg}'
            recipe_name = f"{base_pkg}_{old_ver}.bb"
            recipe_path = RECIPES_DIR / recipe_name
            if not recipe_path.exists():
                print("**********************************************************************")
                print(f"Skipping {pkg} - recipe not found for version {old_ver}: {recipe_path}")
                print("**********************************************************************")
                continue
            print("**************************************************************")
            print(f"Running upgrade helper for {pkg} -> {new_ver}")
            print("**************************************************************")
            # Ensure log directory exists
            LOG_DIR.mkdir(parents=True, exist_ok=True)
            log_file = LOG_DIR / f"{pkg.replace('/', '_')}_{new_ver}.log"
            # Call the runner with the recipe filename as a single argument
            # Stream subprocess output to console and into the log file
            with log_file.open('w', encoding='utf8') as lf:
                lf.write(f"Running: {RUNNER} {recipe_name}\n\n")
                lf.flush()
                proc = subprocess.Popen(
                    [str(RUNNER), base_pkg],
                    stdout=subprocess.PIPE,
                    stderr=subprocess.STDOUT,
                    text=True,
                )
                # Read stdout line by line and echo to console and log
                if proc.stdout is None:
                    print(f"No stdout from process for {pkg}")
                else:
                    for line in proc.stdout:
                        print(line, end='')
                        lf.write(line)
                        lf.flush()
                ret = proc.wait()
                lf.write(f"\nReturn code: {ret}\n")
                if ret != 0:
                    print(f"Upgrade failed for {pkg}, see log: {log_file}")
                    failed.append((pkg, new_ver))
                    continue
                else:
                    print(f"Upgrade succeeded for {pkg}")
                    upgraded.append((pkg, new_ver))

    print('\nSummary:')
    print(f'Upgraded: {len(upgraded)}')
    for p, v in upgraded:
        print(f' - {p} -> {v}')
    print(f'Failed: {len(failed)}')
    for p, v in failed:
        print(f' - {p} -> {v}')


if __name__ == '__main__':
    main()
