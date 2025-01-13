SUMMARY = "RAUC bundle for the reterminal"
DESCRIPTION = "RAUC bundles for updating a variety of Reterminal devices"
LICENSE = "Apache-2.0"

inherit bundle

RAUC_BUNDLE_COMPATIBLE:qemux86-64 = "reterminal-x86"
RAUC_BUNDLE_COMPATIBLE:qemuarm64 = "reterminal-aarch64"
RAUC_BUNDLE_COMPATIBLE:reterminal = "reterminal"

RAUC_BUNDLE_FORMAT = "verity"

RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_BUNDLE_SLOTS:qemux86-64 = "rootfs"
# RAUC_BUNDLE_SLOTS:qemux86-64 = "efi rootfs"
RAUC_BUNDLE_SLOTS:qemuarm64 = "rootfs boot"

RAUC_SLOT_rootfs = "reterminal-image"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[file] = "reterminal-image.ext4"
RAUC_SLOT_rootfs[adaptive] = "block-hash-index"

RAUC_SLOT_boot = "reterminal-boot-image"
RAUC_SLOT_boot[fstype] = "vfat"
RAUC_SLOT_boot[file] = "boot-image.vfat"
RAUC_SLOT_boot[adaptive] = ""

# RAUC_SLOT_efi:qemux86-64 = "boot-image"
# RAUC_SLOT_efi:qemux86-64[file] = "efi-boot.vfat"
# RAUC_SLOT_efi:qemux86-64[type] = "boot"

RAUC_BUNDLE_DESCRIPTION = "Reterminal Bundle"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"
