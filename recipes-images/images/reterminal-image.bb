SUMMARY = "Image for a reterminal"
LICENSE = "Apache-2.0"

inherit core-image

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    \
    rauc \
    \
    nano \
    evtest \
    i2c-tools \
    curl \
    \
    kernel-modules \
    \
    button-handler \
    reterminal-homeassistant-config \
"

IMAGE_FEATURES += "\
    weston \
    allow-empty-password \
    empty-root-password \
    allow-root-login \
    post-install-logging \
"

IMAGE_ROOTFS_ALIGNMENT = "4"
# ext4 block size should be set to 4K and use a fixed directory hash seed to
# reduce the image delta size (keep oe-core's 4K bytes-per-inode)
EXTRA_IMAGECMD:ext4 = "-i 4096 -b 4096 -E hash_seed=86ca73ff-7379-40bd-a098-fcb03a6e719d"

IMAGE_FSTYPES:append = " \
    wic \
"
