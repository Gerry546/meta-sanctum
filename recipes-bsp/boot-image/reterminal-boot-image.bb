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

do_deploy[depends] += "\
    dosfstools-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
"

do_extract_boot_part[depends] += "\
    reterminal-image:do_image_complete \
"

PARTITION_NAME = "boot-part"

python do_extract_boot_part() {
    import subprocess
    # wic_image = d.getVar('IMAGE_NAME') + '.wic'
    wic_image = d.getVar('DEPLOY_DIR_IMAGE') + '/reterminal-image-reterminal.rootfs.wic'
    vfat_image = d.getVar('WORKDIR') + '/' + d.getVar('PARTITION_NAME') + '.vfat'

    cmd = 'wic ls {}'.format(wic_image)
    output = subprocess.check_output(cmd, shell=True)
    lines = output.splitlines()
    # Ensure there are at least 2 lines (header + 1 rows)
    if len(lines) >= 2:
        # Split the second row by spaces and extract Start and End
        columns = lines[1].split()
        start = int(columns[1])
        end = int(columns[2])

        # Log the extracted values
        bb.note("Start of the first partition: {}".format(start))
        bb.note("End of the first partition: {}".format(end))
    else:
        bb.fatal("The wic ls output does not have enough rows to extract the second partition info.")

    # Calculate the size of the partition to extract
    size = round((end - start) / 512)
    start = round(start / 512)

    # Use dd to extract the partition into a vfat image
    dd_cmd = 'dd if={} of={} bs=512 skip={} count={} status=none'.format(wic_image, vfat_image, start, size)
    subprocess.check_call(dd_cmd, shell=True)
}

do_deploy() {
    mv "${WORKDIR}/${PARTITION_NAME}.vfat" ${DEPLOY_DIR_IMAGE}
}

addtask extract_boot_part after do_install
addtask deploy after do_extract_boot_part
