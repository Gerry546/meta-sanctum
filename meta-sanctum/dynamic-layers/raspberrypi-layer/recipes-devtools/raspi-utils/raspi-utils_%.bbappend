inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}/native-tools
    install -m 755 ${B}/dtmerge/dtmerge ${DEPLOYDIR}/native-tools
    install -m 755 ${S}/ovmerge/ovmerge ${DEPLOYDIR}/native-tools
}

addtask deploy after do_install

OECMAKE_TARGET_COMPILE += "ovmerge/all"
OECMAKE_TARGET_INSTALL += "ovmerge/install"

BBCLASSEXTEND = "native"
