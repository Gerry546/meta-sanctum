DESCRIPTION = "Gruv environment for Sanctum devices"
LICENSE = "MIT"

require include/vfat-image.inc

PARTITION_FILES = "\
    grubenv \
" 

do_retrieve_artifacts[depends] += "\
    ${@bb.utils.contains("MACHINEOVERRIDES", "qemux86-64", "rauc-qemu-grubconf:do_deploy", "",d)} \
"

PARTITION_NAME = "sanctum-grubenv"
