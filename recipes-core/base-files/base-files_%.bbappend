FILESEXTRAPATHS:prepend:qemuall := "${THISDIR}/files:"

# Add a mount point for a shared data partition
dirs755:append:qemuall = " /data"
# Add a mount point for the grubenv
dirs755:append:qemux86-64 = "/grubenv"
