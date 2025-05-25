FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://rauc.cfg \
    file://reterminal-i2c.cfg \
    file://reterminal-overlay.dts;subdir=git/arch/arm/boot/dts/overlays/ \
    file://0001-Add-reterminal-overlay.patch \
    file://0002-Add-ilitek-ili9881d-driver.patch \
"
