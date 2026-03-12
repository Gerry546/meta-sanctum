IMAGE_INSTALL:append = " \
    openssh-sshd \
    openssh-scp \
    nano \
"

IMAGE_INSTALL:append:qemuall = " \
    python3-pip \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'ptest-runner', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'python3-homeassistant-ptest', '', d)} \
"

# Prevent stacking up old builds and do not symlink everything
IMAGE_NAME = "${IMAGE_BASENAME}"
IMAGE_LINK_NAME = ""
