require include/ptests.inc

IMAGE_INSTALL:append = " \
    openssh-sshd \
    openssh-scp \
    nano \
"

IMAGE_INSTALL:append:qemuall = " \
    python3-pip \
"
