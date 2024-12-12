SUMMARY = "HomeAssistant Configuration for Reterminal"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "gitsm://github.com/Gerry546/Reterminal-Homeassistant.git;branch=main;protocol=https"
# SRCREV = "c41d7873aaa1ac39d87d5357044474637e7ac794"
SRCREV = "${AUTOREV}"

inherit useradd

S = "${WORKDIR}/git"

HOMEASSISTANT_CONFIG_DIR ?= "${localstatedir}/lib/homeassistant"
HOMEASSISTANT_USER ?= "homeassistant"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "homeassistant"
USERADD_PARAM:${PN} = "\
    --system --home ${HOMEASSISTANT_CONFIG_DIR} \
    --no-create-home --shell /bin/false \
    --groups homeassistant,dialout --gid homeassistant ${HOMEASSISTANT_USER} \
"

do_compile[noexec] = "1"

do_install() {
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}${HOMEASSISTANT_CONFIG_DIR}

    cp -R --no-dereference --preserve=mode,links -v  ${S}/common ${D}${HOMEASSISTANT_CONFIG_DIR}/common
    install -m 0755 ${S}/configuration.yaml ${D}${HOMEASSISTANT_CONFIG_DIR}/
    install -m 0755 ${S}/automations.yaml ${D}${HOMEASSISTANT_CONFIG_DIR}/
    install -m 0755 ${S}/scenes.yaml ${D}${HOMEASSISTANT_CONFIG_DIR}/
    install -m 0755 ${S}/scripts.yaml ${D}${HOMEASSISTANT_CONFIG_DIR}/
}

FILES:${PN} += "\
    ${HOMEASSISTANT_CONFIG_DIR}/common \
    ${HOMEASSISTANT_CONFIG_DIR}/configuration.yaml \
    ${HOMEASSISTANT_CONFIG_DIR}/automations.yaml \
    ${HOMEASSISTANT_CONFIG_DIR}/scenes.yaml \
    ${HOMEASSISTANT_CONFIG_DIR}/scripts.yaml \
"
