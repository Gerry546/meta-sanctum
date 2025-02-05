FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://rauc.cfg \
    file://reterminal-i2c.cfg \
    file://reterminal.dts;subdir=git/arch/arm/boot/dts/broadcom/ \
    file://0001-Add-reterminal-dts.patch \
    file://0001-add-ilitek-ili9881d.c-driver.patch \
"
