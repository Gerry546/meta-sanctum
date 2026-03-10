# Meta-Sanctum

Meta-Sanctum is a Yocto workspace for home automation systems. It combines a local layer, a curated set of upstream layers, machine-specific build flows, and a Home Assistant integration workflow that is designed for repeatable release updates.

This README is intentionally short. It should help you understand what the repository is, what it can do, and where to go next. The canonical operational documentation lives in the wiki under [meta-sanctum.wiki/Home.md](meta-sanctum.wiki/Home.md).

## What This Repository Contains

- A local Yocto layer in `meta-sanctum/`
- A maintained Home Assistant layer in `sources/meta-homeassistant/`
- Build and run workflows for QEMU and hardware targets such as reTerminal
- RAUC-enabled images and testing flows
- Automation for Home Assistant dependency analysis and recipe upgrades in `scripts/homeassistant/`

## Quick Overview

- Build workflow: `bitbake-setup` and direct BitBake using the repository build layout
- Main active project area: Home Assistant integration and packaging
- Main targets: QEMU, reTerminal, Raspberry Pi related work, and platform experiments
- Main documentation: the wiki, not the README

## Start Here

- Repository overview and navigation: [meta-sanctum.wiki/Home.md](meta-sanctum.wiki/Home.md)
- First-time and returning setup: [meta-sanctum.wiki/Other/Getting-Started.md](meta-sanctum.wiki/Other/Getting-Started.md)
- Build and run workflows: [meta-sanctum.wiki/Other/Build-Workflows.md](meta-sanctum.wiki/Other/Build-Workflows.md)
- Repository structure and rationale: [meta-sanctum.wiki/Other/Repository-Layout.md](meta-sanctum.wiki/Other/Repository-Layout.md)
- Home Assistant maintenance workflow: [meta-sanctum.wiki/Projects/HomeAssistant.md](meta-sanctum.wiki/Projects/HomeAssistant.md)
- Testing strategy and commands: [meta-sanctum.wiki/Other/Testing.md](meta-sanctum.wiki/Other/Testing.md)

## If You Just Want To Build Something

Use the wiki page [meta-sanctum.wiki/Other/Build-Workflows.md](meta-sanctum.wiki/Other/Build-Workflows.md). It covers:

- recommended host cache setup
- build environment initialization with `bitbake-setup`
- direct BitBake builds
- QEMU launch commands
- Docker-based development environment

## Documentation Policy

- The README is the landing page.
- The wiki is the long-term memory of the project.
- Detailed setup steps, workflows, architecture notes, and project rationale belong in the wiki.

## License

This project is licensed under the Apache-2.0 License. See [LICENSE](LICENSE).
