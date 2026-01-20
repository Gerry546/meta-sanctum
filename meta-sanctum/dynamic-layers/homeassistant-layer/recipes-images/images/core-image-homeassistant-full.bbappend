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
