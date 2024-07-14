FILESEXTRAPATHS:prepend:qemuarm64-a72 := "${THISDIR}/files:"

# Add a mount point for a shared data partition
dirs755:append:qemuarm64-a72 = " /data"
