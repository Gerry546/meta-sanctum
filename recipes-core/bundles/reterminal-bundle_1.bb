SUMMARY = "Debug RAUC bundle"
LICENSE = "Apache-2.0"

inherit bundle

RAUC_BUNDLE_COMPATIBLE = "reterminal"

RAUC_BUNDLE_FORMAT = "verity"

RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_SLOT_rootfs = "reterminal-image"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"
