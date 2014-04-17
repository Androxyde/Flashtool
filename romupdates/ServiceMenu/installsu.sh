#!/dev/sh

# remount rw
/system/bin/stop ric
/system/bin/mount -o remount,rw /system

# su
/system/bin/dd if=/data/local/tmp/su of=/system/xbin/su
/system/bin/chown root.root /system/xbin/su
/system/bin/chmod 06755 /system/xbin/su

# Superuser
/system/bin/dd if=/data/local/tmp/Superuser.apk of=/system/app/Superuser.apk
/system/bin/chown root.root /system/app/Superuser.apk
/system/bin/chmod 644 /system/app/Superuser.apk

# busybox
/system/bin/dd if=/data/local/tmp/busybox of=/system/xbin/busybox
/system/bin/chown root.root /system/xbin/busybox
/system/bin/chmod 755 /system/xbin/busybox
/system/xbin/busybox --install -s /system/xbin

# antiric
/system/bin/mv /system/etc/install-recovery.sh /system/etc/install-recovery.sh.orig
/system/bin/dd if=/data/local/tmp/install-recovery.sh of=/system/etc/install-recovery.sh
/system/bin/chmod 755 /system/etc/install-recovery.sh

# remount ro
/system/bin/mount -o remount,ro /system
/system/bin/start ric