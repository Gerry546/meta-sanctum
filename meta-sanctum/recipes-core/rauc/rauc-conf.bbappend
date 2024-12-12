FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_install:append() {
    install -d ${D}/data/rauc
}

FILES:${PN}:append = " \
    /data/rauc \
"

# Additional dependencies required to run RAUC on the target
RDEPENDS:${PN} += "\
    e2fsprogs-mke2fs \
"

RDEPENDS:${PN}:x86-64 += "\
    grub-editenv \
"

RDEPENDS:${PN}:aarch64 += "\
    libubootenv \
    u-boot-env \
    u-boot-fw-utils \
"
