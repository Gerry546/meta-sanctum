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
TARGET_IMAGE_NAME = "${DEPLOY_DIR_IMAGE}/reterminal-image.wic"

python do_extract_boot_part() {
    import subprocess
    import os
    wic_image = d.getVar('TARGET_IMAGE_NAME')
    vfat_image = d.getVar('WORKDIR') + '/' + d.getVar('PARTITION_NAME') + '.vfat'
    bb.warn("Using wic image: {}".format(wic_image))

    bb.warn('wic ls {}'.format(wic_image))
    cmd = ['wic', 'ls', wic_image]
    output = subprocess.check_output(cmd, env=os.environ)
    bb.warn("wic info: {}".format(output))
    lines = output.splitlines()
    # Ensure there are at least 2 lines (header + 1 rows)
    if len(lines) >= 2:
        # Split the second row by spaces and extract Start and End
        columns = lines[1].split()
        start = int(columns[1])
        end = int(columns[2])

        # Log the extracted values
        bb.warn("Start of the first partition: {}".format(start))
        bb.warn("End of the first partition: {}".format(end))
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
