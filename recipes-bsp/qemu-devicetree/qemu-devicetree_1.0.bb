SUMMARY = "QEMU device tree binary"
DESCRIPTION = "Recipe deploying the generated QEMU device tree binary blob"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "dtc-native"

SRC_URI = "file://qemu.dts"
SRC_URI[sha256sum] = "4c6fdb788209b8d28f052ee31f940d809b7e5a40fa0e0ae37b78069b13dd076c"

inherit deploy

do_compile() {
    ${STAGING_BINDIR_NATIVE}/dtc -I dts -O dtb -o qemu.dtb ${UNPACKDIR}/qemu.dts
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0664 ${B}/qemu.dtb ${DEPLOYDIR}/qemu-devicetree.dtb
}

addtask do_deploy after do_compile before do_build
