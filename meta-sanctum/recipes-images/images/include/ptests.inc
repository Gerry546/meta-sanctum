IMAGE_INSTALL:append:qemuall = " \
    ptest-runner \
    \
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
    python3-bluetooth-auto-recovery-ptest \
    python3-ciso8601-ptest \
    python3-click-log-ptest \
    python3-cronsim-ptest \
    python3-emoji-ptest \
    python3-frozendict-ptest \
    python3-geopy-ptest \
    python3-getmac-ptest \
    python3-ha-silabs-firmware-client-ptest \
    python3-hass-nabucasa-ptest \
    python3-hassil-ptest \
    python3-home-assistant-intents-ptest \
    python3-ical-ptest \
    python3-inflect-ptest \
    python3-josepy-ptest \
    python3-keyring-ptest \
    python3-mashumaro-ptest \
    python3-mutagen-ptest \
    python3-psutil-home-assistant-ptest \
    python3-pyfritzhome-ptest \
    python3-pymicro-vad-ptest \
    python3-pymicro-vad-ptest \
    python3-pyserial-asyncio-fast-ptest \
    python3-pyswitchbot-ptest \
    python3-uart-devices-ptest \
    python3-unicode-rbnf-ptest \
    python3-usb-devices-ptest \
    python3-voluptuous-serialize-ptest \
    python3-zigpy-xbee-ptest \
    python3-zigpy-znp-ptest \
    python3-zm-py-ptest \
"

IMAGE_INSTALL:append:qemuall:sanctum-dev = " \
    python3-homeassistant-ptest \
    python3-google-auth-httplib2-ptest \
    python3-pytibber-ptest \
    python3-graphql-core-ptest \
    python3-aioruuvigateway-ptest \
"
