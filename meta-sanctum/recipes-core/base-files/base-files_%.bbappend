FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Add a mount point for a shared data partition
dirs755:append:reterminal = " /data"
# Add a mount point for the grubenv
dirs755:append:reterminal-qemux86-64 = " /grubenv"
