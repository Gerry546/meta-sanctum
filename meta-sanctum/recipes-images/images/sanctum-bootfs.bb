DESCRIPTION = "Bootfs"
LICENSE = "MIT"

require include/vfat-image.inc

do_retrieve_artifacts[depends] += "\
    ${@bb.utils.contains("MACHINE", "qemux86-64n", "rauc-qemu-grubconf:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemux86-64n", "grub-efi:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemuarm64-a72", "sanctum-fitimage:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "qemuarm64-a72", "u-boot:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "reterminal", "u-boot:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "reterminal", "rpi-u-boot-scr:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINE", "reterminal", "linux-raspberrypi:do_deploy", "",d)} \
"

# Make the size for a reterminal fat partition bigger
FAT_SIZE:reterminal = "65572"

PARTITION_NAME = "sanctum-bootfs"
