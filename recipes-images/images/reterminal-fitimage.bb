SUMMARY = "fitImage for booting the reterminal"
DESCRIPTION = "fitImage which has all the components needed to boot simplifying image deployment"
LICENSE = "Apache-2.0"

inherit fitimage

FITIMAGE_IMAGES = "kernel"
FITIMAGE_IMAGE_LINK_NAME = "fitImage"

FITIMAGE_IMAGE_kernel ?= "virtual/kernel"
FITIMAGE_IMAGE_kernel[type] ?= "kernel"

FITIMAGE_IMAGE_fdt ?= "virtual/kernel"
FITIMAGE_IMAGE_fdt[type] ?= "fdt"
