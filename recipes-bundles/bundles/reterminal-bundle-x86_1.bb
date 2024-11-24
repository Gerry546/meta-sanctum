SUMMARY = "Debug RAUC bundle for x86"

require rauc-bundle-common.inc

RAUC_BUNDLE_COMPATIBLE = "reterminal-x86"

RAUC_BUNDLE_SLOTS = "efi rootfs"

RAUC_SLOT_efi = "boot-image"
RAUC_SLOT_efi[file] = "efi-boot.vfat"
RAUC_SLOT_efi[type] = "boot"
