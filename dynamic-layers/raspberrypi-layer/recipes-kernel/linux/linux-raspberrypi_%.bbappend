FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://rauc.cfg \
    file://reterminal.dts;subdir=git/arch/arm/boot/dts/overlays/ \
    file://0001-add-reterminal-dts.patch \
"
