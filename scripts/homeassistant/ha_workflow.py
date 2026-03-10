#!/usr/bin/env python3
"""Home Assistant layer automation workflow."""

from __future__ import annotations

import argparse
import csv
import json
import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

from git import GitCommandError, Repo
from packaging import version as packaging_version
from packaging.requirements import InvalidRequirement, Requirement


HOMEASSISTANT_SCRIPT_DIR = Path(__file__).resolve().parent
ROOT = HOMEASSISTANT_SCRIPT_DIR.parents[1]
SCRIPT_OUTPUT_DIR = HOMEASSISTANT_SCRIPT_DIR
LAYER_DIR = ROOT / "sources" / "meta-homeassistant"
LAYER_SCRIPTS_DIR = LAYER_DIR / "scripts"
RECIPES_DIR = LAYER_DIR / "recipes-devtools" / "python"
HOMEASSISTANT_RECIPE_DIR = LAYER_DIR / "recipes-homeassistant" / "homeassistant"
INTEGRATIONS_TESTS_FILE = (
    HOMEASSISTANT_RECIPE_DIR / "python3-homeassistant" / "integrations-tests.inc"
)
RUNNER = HOMEASSISTANT_SCRIPT_DIR / "run-upgrade-helper.sh"
LOG_DIR = HOMEASSISTANT_SCRIPT_DIR / "upgrade_logs"
LAYERS_FILE = LAYER_SCRIPTS_DIR / "layers.json"
INTEGRATIONS_FILE = LAYER_SCRIPTS_DIR / "integrations.json"
DEFAULT_HA_CHECKOUT = HOMEASSISTANT_SCRIPT_DIR / "HA"
LAYER_PATH_BASE = LAYER_SCRIPTS_DIR / "HA"
SHELL = shutil.which("bash") or "/bin/bash"
CSV_COLUMNS = [
    "Component",
    "Tests Available",
    "In Test Packages",
    "Requirements",
    "Required HA Version",
    "Available Yocto/OE Version",
]


@dataclass(frozen=True)
class UpgradeCandidate:
    package: str
    old_version: str
    new_version: str


def load_json(path: Path):
    with path.open(encoding="utf8") as file:
        return json.load(file)


def normalize_package_name(name: str) -> str:
    normalized = re.sub(r"[-_.]+", "-", name.strip().lower())
    return f"python3-{normalized}"


def normalize_component_name(name: str) -> str:
    return name.replace("-", "_")


def parse_version_safe(value: str):
    try:
        return packaging_version.parse(value)
    except packaging_version.InvalidVersion:
        return None


def is_newer_version(required_version: str, available_version: str) -> bool:
    required = parse_version_safe(required_version)
    available = parse_version_safe(available_version)
    if required is None or available is None:
        return False
    return required > available


def extract_component_test_packages(file_path: Path) -> set[str]:
    content = file_path.read_text(encoding="utf8")
    return {
        match.group(1)
        for match in re.finditer(r"\$\{PN\}-([a-z0-9\-_]+)\s+\\", content)
    }


def select_latest_tag(repo: Repo) -> str:
    candidates = []
    for tag in repo.tags:
        parsed = parse_version_safe(tag.name.lstrip("v"))
        if parsed is not None:
            candidates.append((parsed, tag.name))
    if candidates:
        return max(candidates, key=lambda item: item[0])[1]
    tags = sorted(repo.tags, key=lambda item: item.commit.committed_datetime)
    if not tags:
        raise RuntimeError("No tags found in Home Assistant core repository")
    return tags[-1].name


def get_repo(ha_path: Path, requested_version: str = "") -> str:
    try:
        repo = (
            Repo(ha_path)
            if ha_path.is_dir()
            else Repo.clone_from("https://github.com/home-assistant/core.git", ha_path)
        )
        if repo.remotes:
            repo.remotes.origin.fetch(tags=True)
        target_version = requested_version or select_latest_tag(repo)
        repo.git.checkout(target_version)
        return target_version
    except GitCommandError as error:
        raise RuntimeError(f"Failed to checkout tag {requested_version or '<latest>'}: {error}") from error


def load_layer_paths() -> list[Path]:
    return [(LAYER_PATH_BASE / layer).resolve() for layer in load_json(LAYERS_FILE)]


def get_recipes() -> dict[str, str]:
    version_pattern = re.compile(r"_(?P<version>[0-9]+(?:\.[0-9]+)*(?:[.\-A-Za-z0-9]+)?)$")
    recipes: dict[str, str] = {}

    for layer_path in load_layer_paths():
        if not layer_path.exists():
            continue
        for recipe_file in layer_path.rglob("*.bb"):
            stem = recipe_file.stem
            match = version_pattern.search(stem)
            if not match:
                continue
            recipe_name = stem.rsplit("_", 1)[0]
            recipe_version = match.group("version")
            current_version = recipes.get(recipe_name)
            if current_version is None:
                recipes[recipe_name] = recipe_version
                continue
            current_parsed = parse_version_safe(current_version)
            new_parsed = parse_version_safe(recipe_version)
            if new_parsed is not None and (current_parsed is None or new_parsed > current_parsed):
                recipes[recipe_name] = recipe_version
    return recipes


def load_manifest(component_path: Path) -> dict:
    with (component_path / "manifest.json").open(encoding="utf8") as file:
        return json.load(file)


def parse_requirement(requirement: str) -> tuple[str, str]:
    try:
        parsed = Requirement(requirement)
        package_name = normalize_package_name(parsed.name)
    except InvalidRequirement:
        package_name, _, _ = requirement.partition("==")
        package_name = normalize_package_name(package_name)

    match = re.search(r"(?:===|==|>=|<=|~=|>|<)\s*([A-Za-z0-9_.+\-]+)", requirement)
    return package_name, match.group(1) if match else ""


def should_include(
    domain: str,
    required_version: str,
    available_version: str,
    integrations: set[str],
    upgrade_only: bool,
    integrations_only: bool,
) -> bool:
    if integrations_only and domain not in integrations:
        return False
    if upgrade_only and required_version and available_version:
        return is_newer_version(required_version, available_version)
    return True


def create_entry(
    domain: str,
    tests_available: str,
    in_test_packages: str,
    package_name: str,
    required_version: str,
    available_version: str,
) -> dict[str, str]:
    return {
        "Component": domain,
        "Tests Available": tests_available,
        "In Test Packages": in_test_packages,
        "Requirements": package_name,
        "Required HA Version": required_version,
        "Available Yocto/OE Version": available_version,
    }


def parse_manifests(ha_path: Path, upgrade_only: bool, integrations_only: bool) -> list[dict[str, str]]:
    integrations = set(load_json(INTEGRATIONS_FILE))
    recipes = get_recipes()
    components_path = ha_path / "homeassistant" / "components"
    test_path = ha_path / "tests" / "components"
    test_packages = extract_component_test_packages(INTEGRATIONS_TESTS_FILE)
    test_components = {item.name for item in test_path.iterdir() if item.is_dir()}
    rows: list[dict[str, str]] = []

    for component in sorted(item for item in components_path.iterdir() if item.is_dir()):
        try:
            manifest = load_manifest(component)
        except FileNotFoundError:
            print(f"Manifest file not found for component: {component.name}")
            continue
        except json.JSONDecodeError:
            print(f"Error decoding JSON for component: {component.name}")
            continue

        domain = manifest["domain"]
        requirements = manifest.get("requirements", [])
        has_test = "y" if domain in test_components else "n"
        in_test_packages = "y" if domain.replace("_", "-") in test_packages else "n"

        if not requirements:
            if should_include(domain, "", "", integrations, upgrade_only, integrations_only):
                rows.append(create_entry(domain, has_test, in_test_packages, "", "", ""))
            continue

        for requirement in requirements:
            package_name, required_version = parse_requirement(requirement)
            available_version = recipes.get(package_name, "")
            if should_include(
                domain,
                required_version,
                available_version,
                integrations,
                upgrade_only,
                integrations_only,
            ):
                rows.append(
                    create_entry(
                        domain,
                        has_test,
                        in_test_packages,
                        package_name,
                        required_version,
                        available_version,
                    )
                )

    return rows


def save_summary_csv(rows: list[dict[str, str]], version_name: str) -> Path:
    output_path = SCRIPT_OUTPUT_DIR / f"{version_name}.csv"
    with output_path.open("w", newline="", encoding="utf8") as file:
        writer = csv.DictWriter(file, fieldnames=CSV_COLUMNS)
        writer.writeheader()
        writer.writerows(rows)
    print(f"Saved CSV: {output_path}")
    return output_path


def cleanup_repo(ha_path: Path) -> None:
    if ha_path.exists():
        shutil.rmtree(ha_path)
        print(f"Removed checked out HA repository: {ha_path}")


def read_summary_rows(version_name: str) -> list[dict[str, str]]:
    candidate_paths = [
        SCRIPT_OUTPUT_DIR / f"{version_name}.csv",
        ROOT / "scripts" / f"{version_name}.csv",
        LAYER_SCRIPTS_DIR / f"{version_name}.csv",
    ]
    csv_path = next((path for path in candidate_paths if path.exists()), None)
    if csv_path is None:
        raise FileNotFoundError(f"Summary CSV not found: {candidate_paths[0]}")
    if csv_path != candidate_paths[0]:
        print(f"Using legacy summary CSV location: {csv_path}")
    with csv_path.open(newline="", encoding="utf8") as file:
        return list(csv.DictReader(file))


def collect_upgrade_candidates(version_name: str) -> list[UpgradeCandidate]:
    recipes = get_recipes()
    candidates: dict[str, UpgradeCandidate] = {}
    for row in read_summary_rows(version_name):
        package_name = row.get("Requirements", "").strip()
        required_version = row.get("Required HA Version", "").strip()
        available_version = row.get("Available Yocto/OE Version", "").strip()
        if package_name and not available_version:
            available_version = recipes.get(package_name, "")
        if not package_name or not required_version or not available_version:
            continue
        if not is_newer_version(required_version, available_version):
            continue
        candidate = UpgradeCandidate(package_name, available_version, required_version)
        current = candidates.get(package_name)
        if current is None or is_newer_version(candidate.new_version, current.new_version):
            candidates[package_name] = candidate
    return sorted(candidates.values(), key=lambda item: item.package)


def write_upgrade_plan(version_name: str, candidates: list[UpgradeCandidate]) -> Path:
    output_path = SCRIPT_OUTPUT_DIR / f"upgraded_components_{version_name}.csv"
    with output_path.open("w", newline="", encoding="utf8") as file:
        writer = csv.writer(file)
        writer.writerow(["package", "old_version", "new_version"])
        for candidate in candidates:
            writer.writerow([candidate.package, candidate.old_version, candidate.new_version])
    print(f"Saved upgrade plan: {output_path}")
    return output_path


def run_recipe_upgrades(
    version_name: str,
    build_dir: Path,
    continue_on_error: bool = True,
) -> int:
    if not RUNNER.exists():
        raise FileNotFoundError(f"Runner script not found: {RUNNER}")

    candidates = collect_upgrade_candidates(version_name)
    write_upgrade_plan(version_name, candidates)

    upgraded: list[tuple[str, str]] = []
    failed: list[tuple[str, str]] = []
    skipped: list[tuple[str, str]] = []

    LOG_DIR.mkdir(parents=True, exist_ok=True)
    for candidate in candidates:
        recipe_path = RECIPES_DIR / f"{candidate.package}_{candidate.old_version}.bb"
        if not recipe_path.exists():
            print(
                f"Skipping {candidate.package} - recipe not found for version "
                f"{candidate.old_version}: {recipe_path}"
            )
            skipped.append((candidate.package, candidate.new_version))
            continue

        print("**************************************************************")
        print(f"Running upgrade helper for {candidate.package} -> {candidate.new_version}")
        print("**************************************************************")

        log_file = LOG_DIR / f"{candidate.package.replace('/', '_')}_{candidate.new_version}.log"
        with log_file.open("w", encoding="utf8") as handle:
            handle.write(f"Running: {SHELL} {RUNNER} {candidate.package} {build_dir}\n\n")
            handle.flush()
            process = subprocess.Popen(
                [SHELL, str(RUNNER), candidate.package, str(build_dir)],
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            if process.stdout is not None:
                for line in process.stdout:
                    print(line, end="")
                    handle.write(line)
                    handle.flush()
            return_code = process.wait()
            handle.write(f"\nReturn code: {return_code}\n")

        if return_code == 0:
            upgraded.append((candidate.package, candidate.new_version))
            print(f"Upgrade succeeded for {candidate.package}")
            continue

        failed.append((candidate.package, candidate.new_version))
        print(f"Upgrade failed for {candidate.package}, see log: {log_file}")
        if not continue_on_error:
            break

    print("\nSummary:")
    print(f"Upgraded: {len(upgraded)}")
    for package_name, new_version in upgraded:
        print(f" - {package_name} -> {new_version}")
    print(f"Failed: {len(failed)}")
    for package_name, new_version in failed:
        print(f" - {package_name} -> {new_version}")
    print(f"Skipped: {len(skipped)}")
    for package_name, new_version in skipped:
        print(f" - {package_name} -> {new_version}")

    return 1 if failed else 0


def find_homeassistant_recipe(version_name: str | None = None) -> Path:
    if version_name:
        requested_path = HOMEASSISTANT_RECIPE_DIR / f"python3-homeassistant_{version_name}.bb"
        if requested_path.exists():
            return requested_path
    recipe_files = list(HOMEASSISTANT_RECIPE_DIR.glob("python3-homeassistant_*.bb"))
    if not recipe_files:
        raise FileNotFoundError("No python3-homeassistant recipe found")
    return max(
        recipe_files,
        key=lambda path: parse_version_safe(path.stem.rsplit("_", 1)[1]) or packaging_version.parse("0"),
    )


def build_update_map(version_name: str) -> dict[str, str]:
    updates: dict[str, str] = {}
    for row in read_summary_rows(version_name):
        package_name = row.get("Requirements", "").strip()
        required_version = row.get("Required HA Version", "").strip()
        if not package_name or not required_version:
            continue
        current_version = updates.get(package_name)
        if current_version is None or is_newer_version(required_version, current_version):
            updates[package_name] = required_version
    return updates


def sanitize_recipe_lines(lines: list[str]) -> list[str]:
    sanitized: list[str] = []
    in_multiline_dep_block = False

    for line in lines:
        if line.startswith("RDEPENDS:") and line.rstrip().endswith('"\\'):
            in_multiline_dep_block = True
            sanitized.append(line)
            continue

        if in_multiline_dep_block and line.strip() == '"':
            in_multiline_dep_block = False
            sanitized.append(line)
            continue

        if in_multiline_dep_block and line.strip() == "":
            continue

        sanitized.append(line)

    return sanitized


def patch_homeassistant_recipe(version_name: str) -> tuple[list[str], Path]:
    updates = build_update_map(version_name)
    recipe_path = find_homeassistant_recipe(version_name)
    target_path = HOMEASSISTANT_RECIPE_DIR / f"python3-homeassistant_{version_name}.bb"
    original_lines = recipe_path.read_text(encoding="utf8").splitlines()
    lines = sanitize_recipe_lines(original_lines)
    pattern = re.compile(
        r"^(\s*)(python3-[a-z0-9][a-z0-9\-]*)(\s+\((?:>=|=|==)\s*)([^)\s]+)(\)\s*\\\s*)$"
    )

    changed: list[str] = []
    new_lines: list[str] = []
    for line in lines:
        match = pattern.match(line)
        if not match:
            new_lines.append(line)
            continue

        package_name = match.group(2)
        new_version = updates.get(package_name)
        if not new_version or new_version == match.group(4):
            new_lines.append(line)
            continue

        new_lines.append(f"{match.group(1)}{package_name}{match.group(3)}{new_version}{match.group(5)}")
        changed.append(f"{package_name}: {match.group(4)} -> {new_version}")

    if changed or recipe_path != target_path or lines != original_lines:
        target_path.write_text("\n".join(new_lines) + "\n", encoding="utf8")
        if recipe_path != target_path:
            recipe_path.unlink()
            print(f"Renamed recipe file: {recipe_path} -> {target_path}")
    print(f"Patched recipe file: {target_path}")
    return changed, target_path


def find_append_block(lines: list[str], section_name: str) -> tuple[int, int]:
    block_start = None
    block_end = None
    start_marker = f"{section_name}:append = \"\\"
    for index, line in enumerate(lines):
        if block_start is None and line.strip().startswith(start_marker):
            block_start = index + 1
            continue
        if block_start is not None and "', '', d)}\"" in line:
            block_end = index
            break
    if block_start is None or block_end is None:
        raise RuntimeError(f"{section_name}:append block not found in {INTEGRATIONS_TESTS_FILE}")
    return block_start, block_end


def collect_block_packages(lines: list[str], start: int, end: int) -> set[str]:
    packages = set()
    for line in lines[start:end]:
        match = re.match(r"\s*\$\{PN\}-([a-z0-9_\-]+) \\", line)
        if match:
            packages.add(normalize_component_name(match.group(1)))
    return packages


def render_test_block(
    lines: list[str],
    start: int,
    end: int,
    remove_tests: set[str],
    add_tests: set[str] | None = None,
) -> list[str]:
    block_lines: list[str] = []
    for line in lines[start:end]:
        match = re.match(r"\s*\$\{PN\}-([a-z0-9_\-]+) \\", line)
        package_name = normalize_component_name(match.group(1)) if match else ""
        if package_name and package_name in remove_tests:
            continue
        if line.strip():
            block_lines.append(line.rstrip("\n"))

    for package_name in sorted(add_tests or set()):
        block_lines.append(f"    ${{PN}}-{package_name.replace('_', '-')} \\")

    return [f"{line}\n" for line in block_lines]


def patch_integration_tests(version_name: str) -> tuple[list[str], list[str]]:
    tests_lines = INTEGRATIONS_TESTS_FILE.read_text(encoding="utf8").splitlines(keepends=True)
    flaky_start, flaky_end = find_append_block(tests_lines, "COMPONENT_TEST_PACKAGES_FLAKY")
    main_start, main_end = find_append_block(tests_lines, "COMPONENT_TEST_PACKAGES")

    flaky_packages = collect_block_packages(tests_lines, flaky_start, flaky_end)
    main_packages = collect_block_packages(tests_lines, main_start, main_end)
    all_current_packages = flaky_packages | main_packages

    add_tests: set[str] = set()
    remove_tests: set[str] = set()
    for row in read_summary_rows(version_name):
        component_name = row.get("Component", "").strip()
        tests_available = row.get("Tests Available", "").strip().lower()
        if not component_name:
            continue
        normalized_name = normalize_component_name(component_name)
        if tests_available == "y" and normalized_name not in all_current_packages:
            add_tests.add(normalized_name)
        if tests_available == "n" and normalized_name in all_current_packages:
            remove_tests.add(normalized_name)

    replacements = [
        (flaky_start, flaky_end, render_test_block(tests_lines, flaky_start, flaky_end, remove_tests)),
        (main_start, main_end, render_test_block(tests_lines, main_start, main_end, remove_tests, add_tests)),
    ]
    new_lines = tests_lines
    for start, end, replacement in sorted(replacements, key=lambda item: item[0], reverse=True):
        new_lines = new_lines[:start] + replacement + new_lines[end:]

    INTEGRATIONS_TESTS_FILE.write_text("".join(new_lines), encoding="utf8")
    return sorted(add_tests), sorted(remove_tests)


def patch_layer(version_name: str) -> int:
    recipe_updates, recipe_path = patch_homeassistant_recipe(version_name)
    added_tests, removed_tests = patch_integration_tests(version_name)

    print(f"Home Assistant recipe path: {recipe_path}")

    if recipe_updates:
        print("Updated recipe dependencies:")
        for item in recipe_updates:
            print(item)
    else:
        print("No recipe dependency updates were required.")

    if added_tests:
        print("Added test packages:")
        for item in added_tests:
            print(item)
    if removed_tests:
        print("Removed test packages:")
        for item in removed_tests:
            print(item)

    return 0


def ensure_branch(branch_name: str) -> None:
    repo = Repo(LAYER_DIR)
    local_branches = {head.name for head in repo.heads}
    if branch_name in local_branches:
        repo.git.checkout(branch_name)
        print(f"Checked out existing branch: {branch_name}")
        return
    repo.git.checkout("-b", branch_name)
    print(f"Created and checked out branch: {branch_name}")


def run_parse_command(args: argparse.Namespace) -> int:
    checkout_dir = Path(args.ha_checkout_dir).expanduser().resolve()
    actual_version = get_repo(checkout_dir, args.version)
    rows = parse_manifests(checkout_dir, args.upgrade_only, args.integrations_only)
    save_summary_csv(rows, actual_version)
    if args.clean:
        cleanup_repo(checkout_dir)
    return 0


def run_upgrade_command(args: argparse.Namespace) -> int:
    build_dir = Path(args.build_dir).expanduser().resolve()
    return run_recipe_upgrades(args.version, build_dir, continue_on_error=not args.fail_fast)


def run_patch_command(args: argparse.Namespace) -> int:
    return patch_layer(args.version)


def run_branch_command(args: argparse.Namespace) -> int:
    ensure_branch(args.branch)
    return 0


def run_all_command(args: argparse.Namespace) -> int:
    if args.branch:
        ensure_branch(args.branch)

    parse_args = argparse.Namespace(
        version=args.version,
        upgrade_only=args.upgrade_only,
        integrations_only=args.integrations_only,
        clean=args.clean,
        ha_checkout_dir=args.ha_checkout_dir,
    )
    result = run_parse_command(parse_args)
    if result != 0:
        return result

    upgrade_args = argparse.Namespace(
        version=args.version,
        build_dir=args.build_dir,
        fail_fast=args.fail_fast,
    )
    result = run_upgrade_command(upgrade_args)
    if result != 0:
        return result

    patch_args = argparse.Namespace(version=args.version)
    return run_patch_command(patch_args)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Home Assistant layer automation workflow")
    subparsers = parser.add_subparsers(dest="command", required=True)

    parse_parser = subparsers.add_parser("parse", help="Parse Home Assistant requirements into a CSV")
    parse_parser.add_argument("-v", "--version", default="", help="Home Assistant version/tag")
    parse_parser.add_argument("--upgrade-only", action="store_true", help="Only keep rows that need recipe upgrades")
    parse_parser.add_argument("--integrations-only", action="store_true", help="Only keep integrations listed in integrations.json")
    parse_parser.add_argument("--clean", action="store_true", help="Remove the temporary Home Assistant checkout when done")
    parse_parser.add_argument(
        "--ha-checkout-dir",
        default=str(DEFAULT_HA_CHECKOUT),
        help="Path used for the temporary Home Assistant core checkout",
    )
    parse_parser.set_defaults(func=run_parse_command)

    upgrade_parser = subparsers.add_parser("upgrade", help="Run the upgrade helper for packages that need updates")
    upgrade_parser.add_argument("-v", "--version", required=True, help="Home Assistant version/tag")
    upgrade_parser.add_argument(
        "--build-dir",
        default=str(ROOT / "build" / "build-homeassistant"),
        help="Yocto build directory that contains build/init-build-env",
    )
    upgrade_parser.add_argument("--fail-fast", action="store_true", help="Stop after the first failed recipe upgrade")
    upgrade_parser.set_defaults(func=run_upgrade_command)

    patch_parser = subparsers.add_parser("patch", help="Update the Home Assistant layer files from the parsed CSV")
    patch_parser.add_argument("-v", "--version", required=True, help="Home Assistant version/tag")
    patch_parser.set_defaults(func=run_patch_command)

    branch_parser = subparsers.add_parser("branch", help="Create or switch to a branch in sources/meta-homeassistant")
    branch_parser.add_argument("--branch", required=True, help="Branch name to create or checkout")
    branch_parser.set_defaults(func=run_branch_command)

    all_parser = subparsers.add_parser("all", help="Run branch, parse, upgrade, and patch as one workflow")
    all_parser.add_argument("-v", "--version", required=True, help="Home Assistant version/tag")
    all_parser.add_argument("--branch", default="", help="Optional branch name to create or checkout first")
    all_parser.add_argument("--upgrade-only", action="store_true", help="Only keep rows that need recipe upgrades during parse")
    all_parser.add_argument("--integrations-only", action="store_true", help="Only keep integrations listed in integrations.json during parse")
    all_parser.add_argument("--clean", action="store_true", help="Remove the temporary Home Assistant checkout when done")
    all_parser.add_argument(
        "--ha-checkout-dir",
        default=str(DEFAULT_HA_CHECKOUT),
        help="Path used for the temporary Home Assistant core checkout",
    )
    all_parser.add_argument(
        "--build-dir",
        default=str(ROOT / "build" / "build-homeassistant"),
        help="Yocto build directory that contains build/init-build-env",
    )
    all_parser.add_argument("--fail-fast", action="store_true", help="Stop after the first failed recipe upgrade")
    all_parser.set_defaults(func=run_all_command)

    return parser


def main(argv: list[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    try:
        return args.func(args)
    except Exception as error:
        print(error, file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())