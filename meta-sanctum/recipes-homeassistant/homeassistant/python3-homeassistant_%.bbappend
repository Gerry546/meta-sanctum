HOMEASSISTANT_CONFIG_DIR = "/data/homeassistant"

do_install:append () {
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant
}

FILES:${PN} += "/data/homeassistant"
