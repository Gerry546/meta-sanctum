FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS:append:qemuarm64 = " \
    u-boot-mkimage-native \
"

SRC_URI:append:qemuarm64 = " \
    file://boot-qemu.cmd.in \
    file://fragment.cfg \
    file://0001-Reorder-qemuarm-boot_target.patch \
"

do_configure:append:qemuarm64() {
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' "${UNPACKDIR}/boot-qemu.cmd.in" > "${UNPACKDIR}/boot.cmd"
    mkimage -C none -A ${UBOOT_ARCH} -T script -d "${UNPACKDIR}/boot.cmd" boot.scr
}

do_deploy:append:qemuarm64() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/boot.scr ${DEPLOYDIR}
}

addtask do_deploy after do_compile before do_build
