FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://rauc.cfg \
    file://reterminal-i2c.cfg \
    file://reterminal.dts;subdir=git/arch/arm64/boot/dts/broadcom/ \
    file://0001-add-ilitek-ili9881d.c-driver.patch \
    file://0001-Allow-reterminal-dts.patch \
"
