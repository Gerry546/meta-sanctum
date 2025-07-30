DESCRIPTION = "Bootfs"
LICENSE = "MIT"

require include/vfat-image.inc

do_deploy[depends] += "\
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemux86-64n", "rauc-qemu-grubconf:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemux86-64n", "grub-efi:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemuarm64-a72", "sanctum-fitimage:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemuarm64-a72", "virtual/bootloader:do_deploy", "",d)} \
"

PARTITION_NAME = "sanctum-bootfs"
