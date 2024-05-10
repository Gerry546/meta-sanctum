FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "\
    file://weston.ini \
"

do_install:append() {
    # Remove upstream weston.ini to avoid conflict with weston-ini-conf package
    rm -f ${D}${sysconfdir}/xdg/weston/weston.ini

    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini
}

SYSTEMD_AUTO_ENABLE_${PN} = "enable"
