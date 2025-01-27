FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:remove = "file://boot.cmd.in"

SRC_URI += "file://boot.cmd.in;subdir=${S}"

do_deploy:append() {
    install -m 0644 ${WORKDIR}/boot.cmd ${DEPLOYDIR}/boot.cmd
}
