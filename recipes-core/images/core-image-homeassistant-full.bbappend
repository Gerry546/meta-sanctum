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
    python3-pip \
    \
    weston \
    \
"

# chromium-ozone-wayland
