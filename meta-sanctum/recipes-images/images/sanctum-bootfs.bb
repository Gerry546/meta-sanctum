DESCRIPTION = "Bootfs"
LICENSE = "MIT"

require include/vfat-image.inc

do_retrieve_artifacts[depends] += "\
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemux86-64", "rauc-qemu-grubconf:do_deploy", "",d)} \
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemux86-64", "grub-efi:do_deploy", "",d)} \
"

PARTITION_NAME = "sanctum-bootfs"
