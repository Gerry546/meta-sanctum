FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit systemd

SRC_URI:append = " \
    file://chromium.service.in \
"

URL ?= "http\://localhost\:8123"

RDEPENDS:${PN} += "weston-init"

do_compile:append() {
    sed -e "s:@@URL@@:${URL}:" \
    ${WORKDIR}/chromium.service.in > ${WORKDIR}/chromium.service
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/chromium.service ${D}${systemd_unitdir}/system
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "chromium.service"
