require include/ptests.inc

IMAGE_FEATURES += "\
    debug-tweaks \
    weston \
"

IMAGE_INSTALL += "\
    openssh-sshd \
    openssh-scp \
    \
    nano \
    \
    weston \
    \
"

IMAGE_INSTALL:ptest += "\
    python3-pip \
"

# chromium-ozone-wayland
