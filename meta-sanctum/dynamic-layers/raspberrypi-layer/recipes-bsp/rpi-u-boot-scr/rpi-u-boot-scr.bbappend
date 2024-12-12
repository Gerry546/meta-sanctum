FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:remove = "file://boot.cmd.in"

SRC_URI += "file://boot.cmd.in;subdir=${S}"

