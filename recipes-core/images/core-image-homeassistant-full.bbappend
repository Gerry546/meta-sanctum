require include/ptests.inc

IMAGE_FEATURES += "\
    debug-tweaks \
    weston \
"

IMAGE_INSTALL += "\
    openssh-sshd \
    openssh-scp \
    python3-pip \
    \
    weston \
"
