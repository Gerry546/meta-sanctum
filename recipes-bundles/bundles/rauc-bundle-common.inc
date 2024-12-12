LICENSE = "Apache-2.0"

inherit bundle

RAUC_BUNDLE_FORMAT = "verity"

RAUC_SLOT_rootfs = "reterminal-image"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"

RAUC_SLOT_rootfs[adaptive] = "block-hash-index"
RAUC_SLOT_rootfs[file] = "reterminal-image.ext4"

BUNDLE_LINK_NAME = "${BUNDLE_BASENAME}"