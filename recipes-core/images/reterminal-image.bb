SUMMARY = "Image for a reterminal"
LICENSE = "Apache-2.0"

inherit core-image

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    \
    python3-homeassistant \
    python3-homeassistant-amazon-polly \
    python3-homeassistant-assist-pipeline \
    python3-homeassistant-axis \
    python3-homeassistant-backup \
    python3-homeassistant-bluetooth \
    python3-homeassistant-cast \
    python3-homeassistant-cloud \
    python3-homeassistant-conversation \
    python3-homeassistant-debugpy \
    python3-homeassistant-dhcp \
    python3-homeassistant-ffmpeg \
    python3-homeassistant-file-upload \
    python3-homeassistant-fritz \
    python3-homeassistant-fritzbox \
    python3-homeassistant-frontend \
    python3-homeassistant-google-translate \
    python3-homeassistant-hardware \
    python3-homeassistant-hacs \
    python3-homeassistant-hue \
    python3-homeassistant-image-upload \
    python3-homeassistant-ipp \
    python3-homeassistant-keyboard-remote \
    python3-homeassistant-matter \
    python3-homeassistant-met \
    python3-homeassistant-mobile-app \
    python3-homeassistant-modbus \
    python3-homeassistant-octoprint \
    python3-homeassistant-otp \
    python3-homeassistant-pulseaudio-loopback \
    python3-homeassistant-radio-browser \
    python3-homeassistant-recorder \
    python3-homeassistant-route53 \
    python3-homeassistant-scrape \
    python3-homeassistant-sentry \
    python3-homeassistant-shelly \
    python3-homeassistant-ssdp \
    python3-homeassistant-stream \
    python3-homeassistant-systemmonitor \
    python3-homeassistant-tts \
    python3-homeassistant-upnp \
    python3-homeassistant-usb \
    python3-homeassistant-vlc \
    python3-homeassistant-zeroconf \
    \
    chromium-ozone-wayland \
    nano \
"
