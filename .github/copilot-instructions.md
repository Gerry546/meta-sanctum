# Copilot instructions for this repository (meta-sanctum)

Last reviewed: 2026-03-01

This repository is a Yocto Project workspace centered around the `meta-sanctum` layer and related upstream layers in `sources/`.

## Instruction strength

Interpret wording consistently:
- **MUST / MUST NOT**: mandatory constraints.
- **SHOULD / SHOULD NOT**: strong default; deviate only with clear rationale.
- **MAY**: optional guidance.

When making changes:
- Keep diffs minimal and behavior stable unless the request requires change.
- Do **not** edit generated build output (`build/`, `tmp/`, `sstate-cache/`, `downloads/`) except for debugging locally.
- You **should** change local layers (`meta-sanctum/`, `sources/meta-homeassistant/`) instead of upstream layers unless explicitly requested.
- When repository workflows, paths, layers, or tooling change, you **should** update this instructions file in the same change set.

## Repo setup

### Host caches (recommended)
This repo is commonly configured to share Yocto caches across builds:
- `/cache/downloads`
- `/cache/sstate`

### Fetch/update layers
- Upstream layers live under `sources/` and are git repos.
- Use the helper script to fetch updates for all repos under `sources/`:
  - `./scripts/pull_modules`

### Development environment
- This repo supports building with `kas` (recommended for a reproducible toolchain).
- See the top-level README for the Docker-based workflow and kas entry points.

Key files/directories:
- `README.md`: high-level project overview and build/run examples.
- `meta-sanctum/`: the main layer (recipes, images, configs).
- `sources/meta-homeassistant/`: Home Assistant layer and many Python recipes.
- `configurations/`: repo-specific build configuration inputs.

## Building (keep it working)

There are two common workflows.

Before proposing substantial recipe changes, you **must** identify which workflow the user is currently using and keep commands consistent with that workflow.

### A) Build with kas
- Choose a kas config under `kas/` (machine-specific).
- Run:
  - `kas build kas/<machine>/<config>.yml`

This fetches layers and runs BitBake with the right configuration.

### B) Build with BitBake directly (using this repo’s build dir layout)
- Build directories live under `build/` (e.g., `build/build-homeassistant/`).
- The environment init script is at `build/build-<name>/build/init-build-env`.

Creating a build directory (repo-supported approach):
- The repo includes BitBake’s `bitbake-setup` and VS Code tasks wrap it.
- The underlying command is `sources/bitbake/bin/bitbake-setup ... init ...` with repo settings (see the VS Code task “Initialize Yocto Environment”).

Typical usage:
- `source build/build-homeassistant/build/init-build-env`
- `bitbake <target>`

Useful existing targets in this workspace:
- Home Assistant images are in `sources/meta-homeassistant/recipes-homeassistant/images/`.
- Example aggregate ptest image: `meta-homeassistant-image-ptest-all`.

VS Code tasks are provided for common actions (init/build/test). You **should** use those when available.

### Build reliability checklist (after edits)
When changing recipes/images/classes, validate at the smallest useful scope first:
- Recipe-only change:
  - build/test the specific recipe or ptest image first.
- Image/packagegroup change:
  - run the related image build before broader tests.
- Wide dependency changes:
  - expect more than one target build and communicate that explicitly.

Prefer these workspace tasks where applicable:
- `Initialize Yocto Environment`
- `Build Yocto Target`
- `Test HomeAssistant Package`
- `Test HomeAssistant Layer`
- `Test HomeAssistant PTest`
- `HomeAssistant Pip Check`

You **must not** trigger large full-layer rebuilds when a narrow package-level validation is enough.

## Documentation locations

### BitBake documentation
- BitBake’s manuals are in the BitBake repo:
  - `sources/bitbake/doc/`
  - Entry point: `sources/bitbake/doc/index.rst`

### Poky / Yocto (integration) documentation
- In this workspace, Yocto manuals are in:
  - `sources/yocto-docs/`
- In this workspace, Poky is split into separate repositories (not a monolithic `poky/` folder).
  - Poky overview/readme material is in `sources/meta-yocto/README.poky.md` (and `sources/meta-yocto/README.md`).
  - OE-Core readmes are in `sources/openembedded-core/README.md` and `sources/openembedded-core/README.OE-Core.md`.

When a user explicitly asks for “Poky docs”, check in this order:
1. `sources/yocto-docs/`
2. `sources/meta-yocto/README.poky.md`
3. `sources/openembedded-core/README*.md` for OE-Core behavior/context

If you need full Yocto manual content, prefer `sources/yocto-docs/`; use the readmes above for Poky/OE-Core integration context.

## Layer + recipe layout

### Where recipes live
- Local layer recipes:
  - `meta-sanctum/recipes-*/*/*.bb`
- Home Assistant layer recipes:
  - `sources/meta-homeassistant/recipes-*/*/*.bb`

Common directory conventions:
- `recipes-<domain>/<pn>/` contains the recipe and `files/` assets.
- Recipe filename is usually `${PN}_${PV}.bb` (e.g., `python3-homeassistant_2029.9.3.bb`).
- Use `.bbappend` to extend/modify an upstream recipe without copying it.

### Writing a correct recipe (general)
A “correct” Yocto recipe typically includes:
- Metadata:
  - `SUMMARY`, `HOMEPAGE` (and optionally `DESCRIPTION`, `SECTION`)
- Licensing:
  - `LICENSE`
  - `LIC_FILES_CHKSUM` pointing at a license file in the unpacked sources
- Source:
  - `SRC_URI` with either tarball(s) + checksums, or git URL + `SRCREV`
  - `S` if the source directory is not the default
- Build system:
  - `inherit <classes>` appropriate for the project
- Dependencies:
  - `DEPENDS` for build-time
  - `RDEPENDS:${PN}` for runtime

Rules of thumb:
- Prefer fixed `SRCREV` for git recipes; avoid `AUTOREV` except for local iteration.
- Keep `RDEPENDS` sorted and split logically when long.
- Use modern override syntax (`VAR:append`, `RDEPENDS:${PN}`), matching the style already present.

Recipe QA checklist before finalizing:
- License fields are present and checksums match actual files.
- Fetch source is reproducible (`SRCREV` pinned or tarball checksum set).
- Added runtime deps are justified and not duplicated unnecessarily.
- Service/user changes include all required classes/params (`systemd`, `useradd`).
- Changes are limited to requested scope (no unrelated variable churn/reformatting).

### Python recipes (common in this repo)
Patterns you should follow in this codebase:
- For PyPI projects, use:
  - `inherit pypi python_setuptools_build_meta`
  - Example pattern exists in OE layers (see e.g. Python recipes under `sources/openembedded-core/meta/recipes-devtools/python/`).
- Do **not** blindly use `setuptools3` for PEP-517 projects; OE-Core has QA checks that require the correct class depending on `pyproject.toml`.

For packages with tests:
- Use the ptest helpers when appropriate (e.g., `inherit ptest-python-pytest`).
- Add `RDEPENDS:${PN}-ptest` for test runtime dependencies.

### Service / user integration
If the software runs as a system service:
- Use `inherit systemd` and install units into `${systemd_unitdir}/system`.
- If the service needs a dedicated user/group:
  - Use `inherit useradd` and set `USERADD_PARAM:${PN}` / `GROUPADD_PARAM:${PN}`.

An example of these patterns is `python3-homeassistant_2029.9.3.bb` in the Home Assistant layer.

## Common workflows for adding/updating recipes

### Add a new PyPI-based Python recipe
This repo includes a helper script that runs `devtool` inside an initialized build environment:
- `./scripts/devtool-pypi-helper.sh add <recipe-name> <build-dir-suffix>`
- `./scripts/devtool-pypi-helper.sh finish <recipe-name> <build-dir-suffix>`

By default it finishes into:
- `sources/meta-homeassistant/recipes-devtools/python/`

After finishing:
- Ensure `LICENSE`/`LIC_FILES_CHKSUM` are correct.
- Verify runtime deps (`RDEPENDS:${PN}`) are accurate.
- If checksums are required (tarball fetch), ensure `SRC_URI[sha256sum]` is present.
- Prefer a pinned recipe version and avoid `AUTOREV` in submitted changes.

## Change scope and safety

- You **must not** edit `sources/` upstream layers unless user asks for that location specifically.
- You **should** prefer `.bbappend` for upstream recipe adjustments over copying whole recipes.
- Keep ordering/style consistent with neighboring recipes in the same folder.
- For uncertain dependency changes, explain assumptions and suggest a targeted build/test command.

## Commit messages (Yocto style)

Commit message and patch structuring guidance follows Yocto documentation in:
- `sources/yocto-docs/documentation/contributor-guide/submit-changes.rst`
- Section: **Implement and commit changes**

When preparing commits (or suggested commit messages):
- You **should** keep changes small and isolated (one logical change per commit).
- You **should** use `git commit -s` so commits include a `Signed-off-by:` line (DCO).
- You **should** use a short, descriptive one-line summary, prefixed by recipe name or short path (matching existing history style).
- You **should** include a body explaining what changed, why, and how it was tested.
- If applicable, you **should** reference Yocto bugs as `Fixes [YOCTO #<id>]` in the commit body.
- If applicable, you **may** add contributor credit tags (`Reported-by`, `Suggested-by`, `Tested-by`, `Reviewed-by`, `Cc`).

Important: commit-message format
- You **MUST NOT** use Conventional Commit style prefixes such as `fix(<scope>):`, `feat(<scope>):`, or other `type(scope):` forms when preparing commits for this repo. These differ from the project's historical commit format and cause inconsistent history entries.
- Always start the one-line summary with the recipe name or short path followed by a colon and a space. Example: `python3-siobrultech-protocols: remove conflicting tests package during installation`.
- Keep the recipe-name prefix short and use the upstream recipe name where applicable (e.g., `python3-<name>:`). This helps automated tooling and searching by package name.

Note: do not create commits unless the user explicitly asks for committing changes.

## Guidance for Copilot edits

When asked to change recipes or images:
- You **should** adjust the local layer first (`meta-sanctum/` or `sources/meta-homeassistant/`).
- Keep recipe variables and overrides consistent with existing style.
- Avoid introducing new layers or restructuring directories unless explicitly requested.
- When referencing files in explanations, use repo-relative paths.
- When generating commit messages (including suggestions by Copilot), always format the one-line summary as `recipe-name: short description` and avoid `type(scope):` or other Conventional Commit-style prefixes.
