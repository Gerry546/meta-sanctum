inherit nopackages

do_fetch[noexec] = "1"
do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot deploy

DEPENDS += "\
    dosfstools-native \
    mtools-native \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
PARTITION_FILES ?= "${IMAGE_BOOT_FILES}"
PARTITION_NAME ?= "test-bootfs"
FAT_SIZE ?= "32786"

do_retrieve_artifacts[nostamp] = "1"

python do_retrieve_artifacts() {
    import os
    import shutil

    workdir = d.getVar('WORKDIR')
    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    if d.getVar('MACHINEOVERRIDES') is not None:
        machine_overrides = d.getVar('MACHINEOVERRIDES').split(':')
        if 'qemux86-64n' in machine_overrides:
            bb.note("qemux86-64n detected, using /boot/EFI/BOOT directory for FAT source")
            fatsourcedir = os.path.join(workdir, 'boot', 'EFI', 'BOOT')
        else:
            fatsourcedir = os.path.join(workdir, 'boot')
    os.makedirs(fatsourcedir, exist_ok=True)

    partition_files = d.getVar('PARTITION_FILES').split()
    bb.note(f"PARTITION_FILES: {partition_files}")

    for file_entry in partition_files:
        if 'bootfiles' in file_entry:
            src_path = os.path.join(deploy_dir_image, 'bootfiles')
            dst_path = fatsourcedir
            bb.note(f"Copying directory {src_path} to {dst_path}")
            shutil.copytree(src_path, dst_path, dirs_exist_ok=True)
            continue

        if ';' in file_entry:
            src_file, dst_file = file_entry.split(';')
        else:
            src_file = dst_file = file_entry

        if 'overlay_map.dtb' in file_entry:
            dst_path = os.path.join(fatsourcedir, 'overlays', src_file)
        if '.dtbo' in file_entry:
            dst_path = os.path.join(fatsourcedir, 'overlays', src_file)
        else:
            dst_path = os.path.join(fatsourcedir, dst_file)

        src_path = os.path.join(deploy_dir_image, src_file)
        bb.note(f"Copying {src_path} to {dst_path}")

        os.makedirs(os.path.dirname(dst_path), exist_ok=True)
        shutil.copy2(src_path, dst_path)

    d.setVar('FATSOURCEDIR', os.path.join(workdir, 'boot'))
}

do_image_complete () {
    FATSOURCEDIR="${WORKDIR}/boot"
    MKDOSFS_EXTRAOPTS="-S 512"
    FATIMG="${WORKDIR}/${PARTITION_NAME}.vfat"
    BLOCKS=${FAT_SIZE}

    rm -f ${FATIMG}

    mkdosfs -n "BOOT" ${MKDOSFS_EXTRAOPTS} -C ${FATIMG} \
                    ${BLOCKS}
    # Copy FATSOURCEDIR recursively into the image file directly
    mcopy -i ${FATIMG} -s ${FATSOURCEDIR}/* ::/
    chmod 644 ${FATIMG}

    mv ${FATIMG} ${DEPLOY_DIR_IMAGE}/
    rm -rf ${FATSOURCEDIR}
}

python do_deploy(){
    # Your code here
    bb.note("do_deploy is intentionally left empty to avoid default deploy behavior.")
}

addtask retrieve_artifacts after do_install
addtask image_complete after do_retrieve_artifacts
addtask deploy after do_image_complete
