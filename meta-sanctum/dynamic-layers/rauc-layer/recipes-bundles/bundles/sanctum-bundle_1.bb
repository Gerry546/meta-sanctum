SUMMARY = "RAUC bundles for the sanctum project"
DESCRIPTION = "RAUC bundles for updating a variety of Sanctum devices"
LICENSE = "Apache-2.0"

inherit bundle

RAUC_BUNDLE_COMPATIBLE:qemux86-64n = "sanctum-x86"
RAUC_BUNDLE_COMPATIBLE:qemuarm64 = "sanctum-aarch64"
RAUC_BUNDLE_COMPATIBLE:reterminal = "sanctum-reterminal"

RAUC_BUNDLE_FORMAT = "verity"

RAUC_BUNDLE_SLOTS = "rootfs boot"
RAUC_BUNDLE_SLOTS:qemux86-64n = "efi rootfs"
RAUC_BUNDLE_SLOTS:qemuarm64 = "rootfs boot"

RAUC_SLOT_rootfs = "sanctum-rootfs"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[file] = "sanctum-rootfs.ext4.zst"
RAUC_SLOT_rootfs[adaptive] = "block-hash-index"
RAUC_SLOT_rootfs[unpack] = "zst"

RAUC_SLOT_boot = "sanctum-bootfs"
RAUC_SLOT_boot[fstype] = "vfat"
RAUC_SLOT_boot[file] = "sanctum-bootfs.vfat"

RAUC_SLOT_efi = "sanctum-bootfs"
RAUC_SLOT_efi[file] = "sanctum-bootfs.vfat"
RAUC_SLOT_efi[type] = "boot"

RAUC_BUNDLE_DESCRIPTION = "Sanctum Bundle"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"
