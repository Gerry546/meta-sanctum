HOMEASSISTANT_CONFIG_DIR = "${sysconfdir}/homeassistant"

do_install:append () {
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/.cloud
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/.storage
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/backups
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/tts
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/image
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/www
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}/data/homeassistant/themes

    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db-shm
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db-wal
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log.1
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log.fault
    touch ${D}/${HOMEASSISTANT_CONFIG_DIR}/secrets.yaml

    ln -sf -r ${D}/data/homeassistant/.cloud                   ${D}${HOMEASSISTANT_CONFIG_DIR}/.cloud
    ln -sf -r ${D}/data/homeassistant/.storage                 ${D}${HOMEASSISTANT_CONFIG_DIR}/.storage
    ln -sf -r ${D}/data/homeassistant/backups                  ${D}${HOMEASSISTANT_CONFIG_DIR}/backups
    ln -sf -r ${D}/data/homeassistant/image                    ${D}${HOMEASSISTANT_CONFIG_DIR}/image
    ln -sf -r ${D}/data/homeassistant/themes                   ${D}${HOMEASSISTANT_CONFIG_DIR}/themes
    ln -sf -r ${D}/data/homeassistant/tts                      ${D}${HOMEASSISTANT_CONFIG_DIR}/tts
    ln -sf -r ${D}/data/homeassistant/www                      ${D}${HOMEASSISTANT_CONFIG_DIR}/www
    ln -sf -r ${D}/data/homeassistant/home-assistant_v2.db     ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db
    ln -sf -r ${D}/data/homeassistant/home-assistant_v2.db-shm ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db-shm
    ln -sf -r ${D}/data/homeassistant/home-assistant_v2.db-wal ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant_v2.db-wal
    ln -sf -r ${D}/data/homeassistant/home-assistant.log       ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log
    ln -sf -r ${D}/data/homeassistant/home-assistant.log       ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log.1
    ln -sf -r ${D}/data/homeassistant/home-assistant.log       ${D}${HOMEASSISTANT_CONFIG_DIR}/home-assistant.log.fault
    ln -sf -r ${D}/data/homeassistant/secrets.yaml             ${D}${HOMEASSISTANT_CONFIG_DIR}/secrets.yaml

    # chown -R ${HOMEASSISTANT_USER}:${HOMEASSISTANT_USER} ${D}${HOMEASSISTANT_CONFIG_DIR}
}

FILES:${PN} += "/data/homeassistant"
