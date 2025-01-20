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

python do_retrieve_artifacts() {
    import os
    import shutil

    workdir = d.getVar('WORKDIR')
    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    fatsourcedir = os.path.join(workdir, 'boot', 'EFI', 'BOOT')
    os.makedirs(fatsourcedir, exist_ok=True)

    image_boot_files = d.getVar('IMAGE_BOOT_FILES').split()

    for file_entry in image_boot_files:
        if 'bootfiles' in file_entry:
            continue
        if ';' in file_entry:
            src_file, dst_file = file_entry.split(';')
        else:
            src_file = dst_file = file_entry

        src_path = os.path.join(deploy_dir_image, src_file)
        dst_path = os.path.join(fatsourcedir, src_file)
        bb.warn(f"Copying {src_path} to {dst_path}")

        os.makedirs(os.path.dirname(dst_path), exist_ok=True)
        shutil.copy2(src_path, dst_path)

    d.setVar('FATSOURCEDIR', os.path.join(workdir, 'boot'))
}

do_image_complete() {
    FATSOURCEDIR="${WORKDIR}/boot"
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

addtask do_retrieve_artifacts before do_image_complete
addtask image_complete after do_install
