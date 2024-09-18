LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI = "\
    file://chromium.service.in \
"

S = "${UNPACKDIR}"

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
