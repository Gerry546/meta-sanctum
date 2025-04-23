IMAGE_INSTALL:append:qemuall = " \
    ptest-runner \
    \
"

IMAGE_INSTALL:append:qemuall:sanctum-dev = " \
    python3-accuweather-ptest \
    python3-adext-ptest \
    python3-aioairq-ptest \
    python3-aiodiscover-ptest \
    python3-aiohasupervisor-ptest \
    python3-aiohttp-asyncmdnsresolver-ptest \
    python3-aioshelly-ptest \
    python3-aiousbwatcher-ptest \
    python3-airthings-ble-ptest \
    python3-alarmdecoder-ptest \
    python3-async-upnp-client-ptest \
    python3-audioop-lts-ptest \
"
