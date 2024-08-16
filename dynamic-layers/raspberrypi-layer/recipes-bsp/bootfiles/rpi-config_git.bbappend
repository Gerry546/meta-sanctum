do_deploy:append() {
    # I2C support
    if [ "${ENABLE_I2C3}" = "1" ]; then
        echo "# Enable I2C3" >>$CONFIG
        echo "dtparam=i2c3=on" >>$CONFIG
        echo "dtoverlay=i2c3,pins_4_5" >>$CONFIG
    fi
    if [ "${ENABLE_RETERMINAL}" = "1" ]; then
        echo "# Enable Reterminal" >>$CONFIG
        echo "dtoverlay=reterminal,tp_rotate=1" >> $CONFIG
    fi
}
