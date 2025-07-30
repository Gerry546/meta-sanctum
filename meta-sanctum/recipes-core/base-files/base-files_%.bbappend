FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Add a mount point for a shared data partition
dirs755:append:reterminal = " /data"
dirs755:append:qemuarm64-a72 = " /data"
# Add a mount point for the grubenv
dirs755:append:qemux86-64n = " /grubenv"
