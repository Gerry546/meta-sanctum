FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_install:append() {
    install -d ${D}/data/rauc
}

FILES:${PN}:append = " \
    /data/rauc \
"

# Additional dependencies required to run RAUC on the target
RDEPENDS:${PN} += "\
    libubootenv \
    u-boot-env \
    u-boot-fw-utils \
"
