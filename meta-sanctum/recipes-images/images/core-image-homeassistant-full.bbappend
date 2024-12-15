require include/ptests.inc

IMAGE_FEATURES:append = " \
    allow-empty-password \
    empty-root-password \
    allow-root-login \
    post-install-logging \
"

IMAGE_INSTALL:append = " \
    openssh-sshd \
    openssh-scp \
    nano \
"

IMAGE_INSTALL:append:qemuall = " \
    python3-pip \
"
