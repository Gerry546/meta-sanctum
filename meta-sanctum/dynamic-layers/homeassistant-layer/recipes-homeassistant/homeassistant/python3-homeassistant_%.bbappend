HOMEASSISTANT_CONFIG_DIR = "/data/homeassistant"

do_install:append () {
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant
}

PACKAGECONFIG:sanctum-dev += "integration-tests"

FILES:${PN} += "/data/homeassistant"
