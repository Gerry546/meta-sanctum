# meta-sanctum

meta-sanctum is a Yocto layer designed to provide a flexible platform for home automation projects. It offers custom machines, distributions, and recipes to support experimentation, learning, and practical deployments in home automation environments.

## Project Goals

- **Provide a Platform for Home Automation:**

  - Build and maintain images tailored for home automation devices and gateways.
  - Integrate and test open source home automation software, such as Home Assistant.

- **Experimentation and Learning:**
  - Explore the Yocto Project build system, layer structure, and recipe creation.
  - Develop and test custom recipes, overlays, and configurations.

## Supported Machines

- QEMU (qemuarm64-a72, qemux86-64, etc.)
- reTerminal
- Additional custom and emulated machines (see `kas/` and `meta-sanctum/conf/machine/`)

## Features

- Custom machine and distribution definitions for various hardware targets
- Integration with upstream Yocto layers (meta-openembedded, meta-browser, etc.)
- Recipes for Home Assistant and related Python packages
- Example images for QEMU and real hardware
- RAUC integration

## Setup

1. **Create recommended cache directories:**
   It is advised to create the following folders on your host system to speed up builds and share downloads between builds:

   - `/cache/downloads` — used for storing downloaded source files
   - `/cache/sstate` — used for sharing sstate-cache objects
     (Both directories are referenced in the Yocto configuration and will help avoid repeated downloads and rebuilds.)

   ```sh
   sudo mkdir -p /cache/downloads /cache/sstate
   sudo chown $(id -u):$(id -g) /cache/downloads /cache/sstate
   ```

2. **Clone this repository:**

   ```sh
   git clone https://github.com/yourusername/meta-sanctum.git
   cd meta-sanctum
   ```

3. **(Optional) Install kas:**
   If you prefer not to use the provided Docker container, you can install kas directly on your system:

   ```sh
   pip install kas
   # or
   pipx install kas
   ```

   **Recommended:** Use the development Docker container described below, which includes kas and all required tools pre-installed.

## Building the Project (with kas)

1. **Choose a kas configuration file:**
   - Example: `kas/reterminal/reterminal.yml` or another file in the `kas/` directory.
2. **Build the image:**

   ```sh
   kas build kas/reterminal/reterminal.yml
   ```

   This will fetch all required layers and build the selected image for the target machine.

## Running in QEMU

1. **Locate the built image:**

   - Images are typically found in `build/tmp/deploy/images/<machine>/`.

2. **Run the image in QEMU:**
   There are several options you can use with `runqemu` to tailor the emulation environment:

   - `publicvnc` — Enables VNC server for graphical output (connect with a VNC client)
   - `nographic` — No graphical output, serial console only
   - `kvm` — Enables hardware virtualization (recommended for x86/x86_64 hosts)
   - `slirp` — User-mode networking (default, easy setup)
   - `ovmf` — UEFI firmware (useful for modern x86 images)
   - `wic` — Use the generated .wic disk image

   **Example for x86-qemu-reterminal-kas.yml:**

   ```sh
   runqemu build/tmp/deploy/images/reterminal-qemux86-64/reterminal-image.qemuboot.conf slirp ovmf kvm publicvnc wic
   ```

   This command will start the reTerminal x86 QEMU image with UEFI, VNC graphical output, KVM acceleration, and the .wic disk image.

   **Example for Home Assistant builds:**

   ```sh
   runqemu build/tmp/deploy/images/qemux86-64/core-image-homeassistant-full.qemuboot.conf slirp kvm nographic
   ```

   This command will start the Home Assistant QEMU image with KVM acceleration and serial console only (no graphical output).

   - You can mix and match these options depending on your needs and hardware support.
   - Port forwarding and memory settings can be adjusted in `kas/include/common-kas.yml`.

## Using the Development Docker Container

A pre-configured Docker container is provided to facilitate easier building and debugging. This container includes all the necessary tools and dependencies for working with Yocto and meta-sanctum, ensuring a consistent and reproducible development environment.

### Building the Docker Container

From the project root, build the container with:

```sh
docker build -t meta-sanctum-dev utils/dockerfile
```

### Using the Container

You can start an interactive shell in the container with:

```sh
docker run --rm -it -v "$PWD:/workspace" -v /cache:/cache -w /workspace meta-sanctum-dev /bin/bash
```

This mounts your project directory into the container and also mounts your host's `/cache` directory under `/cache` in the container, allowing you to build and debug as if you were on your host system and to share downloads and sstate cache efficiently.

The container is especially useful for:

- Building images with kas or bitbake
- Running Yocto development tools
- Ensuring all dependencies are available and up to date

## Contributing

Contributions are welcome! If you have improvements, bug fixes, or new features, please open a pull request on GitHub. All contributions—whether for new machines, recipes, documentation, or build improvements—are appreciated. Please ensure your changes are well-documented and tested where possible.

For questions or discussion, feel free to open an issue or join the conversation on the project's GitHub page.

## License

This project is licensed under the Apache-2.0 License. See the LICENSE file for details.
