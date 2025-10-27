FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PACKAGECONFIG:append = " wayland-only "

inherit systemd

SRC_URI += " \
    file://firefox.service.in \
"

URL ?= "http\://localhost\:8123"

RDEPENDS:${PN} += "weston-init"

do_compile:append() {
    sed -e "s:@@URL@@:${URL}:" \
    ${UNPACKDIR}/firefox.service.in > ${UNPACKDIR}/firefox.service
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/firefox.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "firefox.service"
