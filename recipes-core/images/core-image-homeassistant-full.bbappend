require include/ptests.inc

IMAGE_FEATURES:append = " \
    debug-tweaks \
    weston \
"

IMAGE_INSTALL:append = " \
    openssh-sshd \
    openssh-scp \
    \
    nano \
    \
    weston \
    \
"

IMAGE_INSTALL:append:qemuarm64-a72 = " \
    python3-pip \
"

# chromium-ozone-wayland
