SUMMARY = "Image for a reterminal"
LICENSE = "Apache-2.0"

inherit core-image

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    \
    python3-homeassistant \
    python3-homeassistant-accuweather \
    python3-homeassistant-acer-projector \
    python3-homeassistant-acmeda \
    python3-homeassistant-adax \
    python3-homeassistant-adguard \
    python3-homeassistant-advantage-air \
    python3-homeassistant-airthings-ble \
    python3-homeassistant-amazon-polly \
    python3-homeassistant-aruba \
    python3-homeassistant-assist-pipeline \
    python3-homeassistant-axis \
    python3-homeassistant-backup \
    python3-homeassistant-bluesound \
    python3-homeassistant-bluetooth \
    python3-homeassistant-bluetooth-tracker \
    python3-homeassistant-camera \
    python3-homeassistant-cast \
    python3-homeassistant-cisco-ios \
    python3-homeassistant-cloud \
    python3-homeassistant-compensation \
    python3-homeassistant-conversation \
    python3-homeassistant-cpuspeed \
    python3-homeassistant-debugpy \
    python3-homeassistant-dhcp \
    python3-homeassistant-dlna-dmr \
    python3-homeassistant-dlna-dms \
    python3-homeassistant-dunehd \
    python3-homeassistant-evohome \
    python3-homeassistant-ffmpeg \
    python3-homeassistant-fritz \
    python3-homeassistant-fritzbox \
    python3-homeassistant-fritzbox-callmonitor \
    python3-homeassistant-frontend \
    python3-homeassistant-generic \
    python3-homeassistant-github \
    python3-homeassistant-google-mail \
    python3-homeassistant-google-tasks \
    python3-homeassistant-google-translate \
    python3-homeassistant-hardware \
    python3-homeassistant-hue \
    python3-homeassistant-image-upload \
    python3-homeassistant-ipp \
    python3-homeassistant-isal \
    python3-homeassistant-keyboard-remote \
    python3-homeassistant-kwb \
    python3-homeassistant-matter \
    python3-homeassistant-met \
    python3-homeassistant-mobile-app \
    python3-homeassistant-modbus \
    python3-homeassistant-mqtt \
    python3-homeassistant-namecheapdns \
    python3-homeassistant-network \
    python3-homeassistant-norway-air \
    python3-homeassistant-octoprint \
    python3-homeassistant-ohmconnect \
    python3-homeassistant-otp \
    python3-homeassistant-owntracks \
    python3-homeassistant-pandora \
    python3-homeassistant-private-ble-device \
    python3-homeassistant-proxy \
    python3-homeassistant-pulseaudio-loopback \
    python3-homeassistant-qwikswitch \
    python3-homeassistant-radio-browser \
    python3-homeassistant-recorder \
    python3-homeassistant-route53 \
    python3-homeassistant-scrape \
    python3-homeassistant-sentry \
    python3-homeassistant-seven-segments \
    python3-homeassistant-shelly \
    python3-homeassistant-speedtestdotnet \
    python3-homeassistant-sql \
    python3-homeassistant-ssdp \
    python3-homeassistant-startca \
    python3-homeassistant-stream \
    python3-homeassistant-switchbot \
    python3-homeassistant-systemmonitor \
    python3-homeassistant-ted5000 \
    python3-homeassistant-trafikverket-camera \
    python3-homeassistant-trafikverket-ferry \
    python3-homeassistant-trafikverket-train \
    python3-homeassistant-trafikverket-weatherstation \
    python3-homeassistant-trend \
    python3-homeassistant-tts \
    python3-homeassistant-upnp \
    python3-homeassistant-usb \
    python3-homeassistant-utility-meter \
    python3-homeassistant-vlc \
    python3-homeassistant-zabbix \
    python3-homeassistant-zeroconf \
    python3-homeassistant-zestimate \
    python3-homeassistant-zoneminder \
    python3-homeassistant-zwave-js \
    python3-homeassistant-zwave-me \
    \
    rauc \
    \
    nano \
    evtest \
    i2c-tools \
    curl \
    \
    kernel-modules \
    \
    chromium-ozone-wayland \
    chromium-starter \
    button-handler \
    reterminal-homeassistant-config \
"

IMAGE_FEATURES += "\
    weston \
"

IMAGE_ROOTFS_ALIGNMENT = "4"
# ext4 block size should be set to 4K and use a fixed directory hash seed to
# reduce the image delta size (keep oe-core's 4K bytes-per-inode)
EXTRA_IMAGECMD:ext4 = "-i 4096 -b 4096 -E hash_seed=86ca73ff-7379-40bd-a098-fcb03a6e719d"
