#!/system/bin/sh
#
# DooMing Script: Part 2
# Part of Easy Rooting Toolkit by DooMLoRD@XDA
#

/data/local/tmp/antiric

mount -o remount,rw /system

export BUSYBOX=/data/local/tmp/busybox
export BIN=/system/bin
export XBIN=/system/xbin

set_perm() {
	$BIN/chown $1.$2 $4
	$BIN/chown $1:$2 $4
	$BIN/chmod $3 $4
}

ch_con() {
	$BIN/toolbox chcon u:object_r:system_file:s0 $1
	$BIN/chcon u:object_r:system_file:s0 $1
}

echo ---------------------------------------------------------------
echo   Launching final rooting process...
echo ---------------------------------------------------------------

echo "--- Installing busybox and latest su binaries"
$BIN/dd if=/data/local/tmp/busybox of=/system/xbin/busybox
set_perm 0 2000 0755 /system/xbin/busybox
ch_con /system/xbin/busybox
$XBIN/busybox busybox --install -s /system/xbin

if [ -e /system/xbin/su ]; then
	$XBIN/busybox chattr -i /system/xbin/su
fi

if [ -e /system/xbin/daemonsu ]; then
	$XBIN/busybox chattr -i /system/xbin/daemonsu
fi

if [ -e /system/bin/.ext/.su ]; then
	$XBIN/busybox chattr -i /system/bin/.ext/.su
fi

if [ -e /system/etc/install-recovery.sh ]; then
	$XBIN/busybox chattr -i /system/etc/install-recovery.sh
fi

$XBIN/busybox rm -f /system/bin/su
$XBIN/busybox rm -f /system/xbin/su
$XBIN/busybox rm -f /system/xbin/daemonsu
$XBIN/busybox rm -f /system/xbin/sugote
$XBIN/busybox rm -f /system/xbin/sugote-mksh
$XBIN/busybox rm -rf /system/bin/.ext
$XBIN/busybox rm -f /system/etc/install-recovery.sh
$XBIN/busybox rm -f /system/etc/init.d/99SuperSUDaemon
$XBIN/busybox rm -f /system/etc/.installed_su_daemon
$XBIN/busybox rm -f /system/app/Superuser.apk
$XBIN/busybox rm -f /system/app/Superuser.odex
$XBIN/busybox rm -f /system/app/SuperUser.apk
$XBIN/busybox rm -f /system/app/SuperUser.odex
$XBIN/busybox rm -f /system/app/superuser.apk
$XBIN/busybox rm -f /system/app/superuser.odex
$XBIN/busybox rm -f /system/app/Supersu.apk
$XBIN/busybox rm -f /system/app/Supersu.odex
$XBIN/busybox rm -f /system/app/SuperSU.apk
$XBIN/busybox rm -f /system/app/SuperSU.odex
$XBIN/busybox rm -f /system/app/supersu.apk
$XBIN/busybox rm -f /system/app/supersu.odex
$XBIN/busybox rm -f /data/dalvik-cache/*com.noshufou.android.su*
$XBIN/busybox rm -f /data/dalvik-cache/*com.koushikdutta.superuser*
$XBIN/busybox rm -f /data/dalvik-cache/*com.mgyun.shua.su*
$XBIN/busybox rm -f /data/dalvik-cache/*Superuser.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*SuperUser.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*superuser.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*eu.chainfire.supersu*
$XBIN/busybox rm -f /data/dalvik-cache/*Supersu.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*SuperSU.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*supersu.apk*
$XBIN/busybox rm -f /data/dalvik-cache/*.oat
$XBIN/busybox rm -f /data/app/com.noshufou.android.su-*
$XBIN/busybox rm -f /data/app/com.koushikdutta.superuser-*
$XBIN/busybox rm -f /data/app/com.mgyun.shua.su-*
$XBIN/busybox rm -f /data/app/eu.chainfire.supersu-*

$XBIN/busybox mkdir -p $BIN/.ext
$XBIN/busybox mkdir -p /system/etc/init.d
$XBIN/busybox cp /data/local/tmp/su /system/xbin/daemonsu
$XBIN/busybox cp /data/local/tmp/su /system/xbin/su
$XBIN/busybox cp /data/local/tmp/su /system/xbin/sugote
$XBIN/busybox cp /data/local/tmp/sugote-mksh /system/xbin/sugote-mksh
$XBIN/busybox cp /data/local/tmp/su /system/bin/.ext/.su
$XBIN/busybox cp /data/local/tmp/Superuser.apk /system/app/Superuser.apk
$XBIN/busybox cp /data/local/tmp/install-recovery.sh /system/etc/install-recovery.sh
$XBIN/busybox cp /data/local/tmp/99SuperSUDaemon /system/etc/init.d/99SuperSUDaemon
$XBIN/busybox cp /data/local/tmp/antiric /system/xbin/antiric
$XBIN/busybox cp /data/local/tmp/writekmem /system/xbin/writekmem
$XBIN/busybox cp /data/local/tmp/findricaddr /system/xbin/findricaddr
$XBIN/busybox cp /data/local/tmp/wp_mod.ko /system/lib/modules/wp_mod.ko
$XBIN/busybox echo 1 > /system/etc/.installed_su_daemon

set_perm 0 0 0777 /system/bin/.ext
set_perm 0 0 06755 /system/bin/.ext/.su
set_perm 0 0 06755 /system/xbin/su
set_perm 0 0 0755 /system/xbin/antiric
set_perm 0 0 0755 /system/xbin/writekmem
set_perm 0 0 0755 /system/xbin/findricaddr
set_perm 0 0 0644 /system/lib/modules/wp_mod.ko
set_perm 0 0 06755 /system/xbin/daemonsu
set_perm 0 0 06755 /system/xbin/sugote
set_perm 0 0 0755 /system/etc/install-recovery.sh
set_perm 0 0 0755 /system/etc/init.d/99SuperSUDaemon
set_perm 0 0 0644 /system/etc/.installed_su_daemon
set_perm 0 0 0644 /system/app/Superuser.apk

ch_con /system/bin/.ext/.su
ch_con /system/xbin/su
ch_con /system/xbin/daemonsu
ch_con /system/xbin/sugote
ch_con /system/xbin/sugote-mksh
ch_con /system/etc/install-recovery.sh
ch_con /system/etc/init.d/99SuperSUDaemon
ch_con /system/etc/.installed_su_daemon
ch_con /system/app/Superuser.apk

ANTIRIC=$(cat /system/etc/install-recovery.sh|grep antiric)

if [ -z "$ANTIRIC" ]; then
	echo "/system/xbin/antiric" >> /system/etc/install-recovery.sh
fi

echo "--- DONE!"

exit