SUMMARY = "Grub configuration file to use with RAUC"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

require conf/image-uefi.conf

SRC_URI:append = " \
    file://grub.cfg \
    file://grubenv \
"

S = "${UNPACKDIR}"

inherit deploy

do_install () {
        install -d ${D}${EFI_FILES_PATH}
        install -m 644 ${S}/grub.cfg ${D}${EFI_FILES_PATH}/grub.cfg
}

do_deploy () {
    install -m 644 ${S}/grub.cfg ${DEPLOYDIR}
    install -m 644 ${S}/grubenv ${DEPLOYDIR}
}

addtask deploy after do_install before do_build

FILES:${PN} += "${EFI_FILES_PATH}"

RPROVIDES:${PN} += "virtual-grub-bootconf"
