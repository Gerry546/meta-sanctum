require include/ptests.inc

IMAGE_FEATURES:append = " \
    debug-tweaks \
"

IMAGE_INSTALL:append = " \
    openssh-sshd \
    openssh-scp \
"

IMAGE_INSTALL:append:qemuarm64-a72 = " \
    python3-pip \
"
