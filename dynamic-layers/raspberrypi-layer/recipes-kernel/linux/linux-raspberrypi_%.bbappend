FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://rauc.cfg \
    file://reterminal-i2c.cfg \
    file://reterminal-overlay.dts;subdir=git/arch/arm/boot/dts/overlays/ \
    file://0001-add-reterminal-overlay.patch \
"
