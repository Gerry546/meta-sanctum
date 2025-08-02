IMAGE_INSTALL:append:qemuall = " \
    ptest-runner \
    \
"

IMAGE_INSTALL:append:qemuall:sanctum-test = " \
    python3-homeassistant-ptest \
"
