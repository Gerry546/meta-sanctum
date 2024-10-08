SUMMARY = "RAUC bundle for the reterminal"
LICENSE = "Apache-2.0"

inherit bundle

RAUC_BUNDLE_COMPATIBLE = "reterminal"
RAUC_BUNDLE_DESCRIPTION = "RAUC Demo Bundle"

RAUC_BUNDLE_FORMAT = "verity"

RAUC_BUNDLE_SLOTS = "rootfs boot"

RAUC_SLOT_rootfs = "reterminal-image"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[adaptive] = "block-hash-index"

RAUC_SLOT_boot = "reterminal-boot-image"
RAUC_SLOT_boot[file] = "boot-part.vfat"
RAUC_SLOT_boot[type] = "boot"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"
