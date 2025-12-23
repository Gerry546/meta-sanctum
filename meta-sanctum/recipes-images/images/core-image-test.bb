IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    openssh \
    openssh-scp \
    nano \
    ptest-runner \
    python3-pip \
"

IMAGE_LINGUAS = ""

LICENSE = "MIT"

# 100 MiB of additional storage for config and runtime data
IMAGE_ROOTFS_EXTRA_SPACE = "102400"

inherit core-image
