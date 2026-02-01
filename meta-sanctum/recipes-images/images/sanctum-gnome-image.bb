SUMMARY = "Image for a sanctum project with GNOME desktop environment"
LICENSE = "Apache-2.0"

inherit core-image

IMAGE_INSTALL:append = "\
    openssh \
"

#############################
# GNOME Configuration
#############################
# The following snippet adds GNOME desktop environment and GDM display manager to the image
# This is the minimal set of packages to get a working GNOME desktop and will drop you to a graphical login prompt
IMAGE_INSTALL:append = "\
    gnome-shell \
    gdm \
    xkeyboard-config \
"
SYSTEMD_DEFAULT_TARGET = "graphical.target"

#############################
# User Configuration
#############################
# The following snippet adds sudo and configures a 'serviceuser' with sudo privileges
IMAGE_INSTALL:append = "\
    sudo \
"
# This password is generated with `openssl passwd -6 password`, 
# where -6 stands for SHA-512 hashing alorithgm
# The resulting string is in format $<ALGORITHM_ID>$<SALT>$<PASSWORD_HASH>,
# the dollar signs have been escaped
# This'll allow user to login with the least secure password there is, "password" (without quotes)
PASSWD = "\$6\$vRcGS0O8nEeug1zJ\$YnRLFm/w1y/JtgGOQRTfm57c1.QVSZfbJEHzzLUAFmwcf6N72tDQ7xlsmhEF.3JdVL9iz75DVnmmtxVnNIFvp0"
inherit extrausers
EXTRA_USERS_PARAMS:append = "\
    useradd -u 1200 -d /home/serviceuser -s /bin/sh -p '${PASSWD}' serviceuser; \
    usermod -a -G sudo serviceuser; \
    usermod -L -e 1 root; \
    "

enable_sudo_group() {
    # This magic looking sed will uncomment the following line from sudoers:
    #   %sudo   ALL=(ALL:ALL) ALL
    sed -i 's/^#\s*\(%sudo\s*ALL=(ALL:ALL)\s*ALL\)/\1/'  ${IMAGE_ROOTFS}/etc/sudoers
}
ROOTFS_POSTPROCESS_COMMAND += "enable_sudo_group;"

#############################
# QEMU Configuration
#############################
QB_MEM = "-m 16384"
QB_SMP = "-smp 12"
