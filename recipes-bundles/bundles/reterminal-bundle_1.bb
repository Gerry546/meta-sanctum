SUMMARY = "RAUC bundle for the reterminal"
DESCRIPTION = "RAUC bundles for updating a variety of Reterminal devices"
LICENSE = "Apache-2.0"

inherit bundle

# Qemux86
##############################################################
RAUC_BUNDLE_COMPATIBLE:qemux86-64 = "reterminal-x86"

RAUC_BUNDLE_SLOTS:qemux86-64 = "rootfs"
# RAUC_BUNDLE_SLOTS:qemux86-64 = "efi rootfs"

# RAUC_SLOT_efi:qemux86-64 = "boot-image"
# RAUC_SLOT_efi:qemux86-64[file] = "efi-boot.vfat"
# RAUC_SLOT_efi:qemux86-64[type] = "boot"

# QemuArm64
##############################################################
RAUC_BUNDLE_COMPATIBLE:qemuarm64 = "reterminal"

# RAUC_BUNDLE_SLOTS:qemuarm64 = "rootfs boot"
RAUC_BUNDLE_SLOTS:qemuarm64 = "rootfs"

# RaspberryPi
##############################################################
RAUC_BUNDLE_COMPATIBLE:reterminal = "reterminal"
RAUC_BUNDLE_DESCRIPTION:reterminal = "Reterminal Bundle"

# COMMON
##############################################################
RAUC_BUNDLE_FORMAT = "verity"
RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"

RAUC_SLOT_rootfs = "reterminal-image"
RAUC_SLOT_rootfs[fstype] = "ext4"
RAUC_SLOT_rootfs[file] = "reterminal-image.ext4"
RAUC_SLOT_rootfs[adaptive] = "block-hash-index"
