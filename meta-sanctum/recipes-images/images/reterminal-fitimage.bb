SUMMARY = "fitImage for booting the reterminal"
DESCRIPTION = "fitImage which has all the components needed to boot simplifying image deployment"
LICENSE = "Apache-2.0"

inherit fitimage

FITIMAGE_IMAGES = "kernel fdt"
FITIMAGE_IMAGE_LINK_NAME = "fitImage"
ITS_LINK_NAME = "fitImage"

FITIMAGE_IMAGE_kernel ?= "virtual/kernel"
FITIMAGE_IMAGE_kernel[type] ?= "kernel"
FITIMAGE_LOADADDRESS:qemuarm64 = "0x40400000"
FITIMAGE_LOADADDRESS:reterminal = "0x00080000"
FITIMAGE_ENTRYPOINT:qemuarm64 = "0x40400000"
FITIMAGE_ENTRYPOINT:reterminal = "0x00080000"

FITIMAGE_DEVICETREE_NAME:qemuarm64 = "qemu-devicetree.dtb"
FITIMAGE_DEVICETREE_NAME:reterminal = "bcm2711-rpi-cm4.dtb"
FITIMAGE_IMAGE_fdt:qemuarm64 ?= "qemu-devicetree"
FITIMAGE_IMAGE_fdt:reterminal ?= "virtual/kernel"
FITIMAGE_IMAGE_fdt[type] = "fdt"
FITIMAGE_IMAGE_fdt[file] = "${DEPLOY_DIR_IMAGE}/${FITIMAGE_DEVICETREE_NAME}"
FITIMAGE_DTB_LOADADDRESS:qemuarm64 = "0x42000000"
FITIMAGE_DTB_LOADADDRESS:reterminal = "0x02600000"
