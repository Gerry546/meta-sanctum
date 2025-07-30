DESCRIPTION = "Bootfs"
LICENSE = "MIT"

require include/vfat-image.inc

do_retrieve_artifacts[depends] += "\
    ${@bb.utils.contains("MACHINE", "qemux86-64n", "rauc-qemu-grubconf:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemux86-64n", "grub-efi:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemuarm64-a72", "sanctum-fitimage:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemuarm64-a72", "u-boot:do_deploy", "",d)} \
"

PARTITION_NAME = "sanctum-bootfs"
