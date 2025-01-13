# Original item copied from:
# Copyright (C) 2021 Enrico Jorns <ejo@pengutronix.de>
# Released under the MIT license (see COPYING.MIT for the terms)
SUMMARY = "Boot image"
LICENSE = "MIT"

inherit nopackages

do_fetch[noexec] = "1"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_extract_boot_part[nostamp] = "1"
deltask do_populate_sysroot

DEPENDS = "\
    dosfstools-native \
    mtools-native \
"

do_image_complete[depends] += "\
    reterminal-image:do_image_complete \
"

PARTITION_NAME = "boot-image"
TARGET_IMAGE_NAME = "${DEPLOY_DIR_IMAGE}/reterminal-image.wic"

do_image_complete() {
    FATSOURCEDIR="${WORKDIR}/boot"
    mkdir -p ${FATSOURCEDIR}/EFI/BOOT

    for file_entry in ${IMAGE_BOOT_FILES}; do
        cp ${DEPLOY_DIR_IMAGE}/${file_entry} ${FATSOURCEDIR}/EFI/BOOT
    done
    
    MKDOSFS_EXTRAOPTS="-S 512"
    FATIMG="${WORKDIR}/${PARTITION_NAME}.vfat"
    BLOCKS=32786

    rm -f ${FATIMG}

    mkdosfs -n "BOOT" ${MKDOSFS_EXTRAOPTS} -C ${FATIMG} \
                    ${BLOCKS}
    # Copy FATSOURCEDIR recursively into the image file directly
    mcopy -i ${FATIMG} -s ${FATSOURCEDIR}/* ::/
    chmod 644 ${FATIMG}

    mv ${FATIMG} ${DEPLOY_DIR_IMAGE}/
}

do_image_complete[cleandirs] += "${WORKDIR}/efi-boot"

addtask image_complete after do_install
