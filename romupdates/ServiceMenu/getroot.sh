#!/system/bin/sh
/system/bin/cat /system/bin/sh > /dev/sh
/system/bin/mount -o suid,remount /dev /dev
/system/bin/chown root.root /dev/sh
/system/bin/chmod 6755 /dev/sh

