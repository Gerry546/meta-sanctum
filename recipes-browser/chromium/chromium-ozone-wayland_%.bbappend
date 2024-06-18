FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://chromium.service.in \
"

URL ?= "http\://localhost\:8123"

RDEPENDS:${PN} += "weston-init"

do_compile:append() {
    sed -e "s:@@URL@@:${URL}:" \
    ${UNPACKDIR}/chromium.service.in > ${UNPACKDIR}/chromium.service
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/chromium.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "chromium.service"
