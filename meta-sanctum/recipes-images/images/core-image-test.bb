IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    openssh-sshd \
    openssh-scp \
    nano \
    ptest-runner \
    python3-pip \
    \
    python3-pytest-httpx-ptest \
"

IMAGE_FEATURES:append = " \
    allow-empty-password \
    empty-root-password \
    allow-root-login \
    post-install-logging \
"

IMAGE_LINGUAS = ""

LICENSE = "MIT"

# 100 MiB of additional storage for config and runtime data
IMAGE_ROOTFS_EXTRA_SPACE = "102400"

inherit core-image
