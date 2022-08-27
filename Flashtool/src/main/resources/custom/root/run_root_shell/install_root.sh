#!/system/bin/sh
#
# DooMing Script: Part 2
# Part of Easy Rooting Toolkit by DooMLoRD@XDA
#

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
echo "--- Rooting!"


echo "--- Finding RIC service (specific for new Xperia devices)"
export RIC=$($BIN/ps|$BUSYBOX grep bin/ric|$BUSYBOX awk '{print $9}')

if $BUSYBOX test -n "$RIC"
then
   echo "--- Remounting fs rw"
   $BUSYBOX mount -o remount,rw / && $BUSYBOX mount -o remount,rw /system && $BUSYBOX pkill -f $RIC && $BIN/chmod 644 $RIC
else
   $BUSYBOX mount -o remount,rw /system
fi

echo "--- Installing busybox"
$BIN/dd if=/data/local/tmp/busybox of=/system/xbin/busybox
set_perm 0 2000 0755 /system/xbin/busybox
ch_con /system/xbin/busybox
$XBIN/busybox --install -s /system/xbin

$XBIN/chattr -i /system/xbin/su
$XBIN/chattr -i /system/xbin/daemonsu
$XBIN/chattr -i /system/bin/.ext/.su
$XBIN/chattr -i /system/etc/install-recovery.sh

$XBIN/rm -f /system/bin/su
$XBIN/rm -f /system/xbin/su
$XBIN/rm -f /system/xbin/daemonsu
$XBIN/rm -rf /system/bin/.ext
$XBIN/rm -f /system/etc/install-recovery.sh
$XBIN/rm -f /system/etc/init.d/99SuperSUDaemon
$XBIN/rm -f /system/etc/.installed_su_daemon
$XBIN/rm -f /system/app/Superuser.apk
$XBIN/rm -f /system/app/Superuser.odex
$XBIN/rm -f /system/app/SuperUser.apk
$XBIN/rm -f /system/app/SuperUser.odex
$XBIN/rm -f /system/app/superuser.apk
$XBIN/rm -f /system/app/superuser.odex
$XBIN/rm -f /system/app/Supersu.apk
$XBIN/rm -f /system/app/Supersu.odex
$XBIN/rm -f /system/app/SuperSU.apk
$XBIN/rm -f /system/app/SuperSU.odex
$XBIN/rm -f /system/app/supersu.apk
$XBIN/rm -f /system/app/supersu.odex
$XBIN/rm -f /data/dalvik-cache/*com.noshufou.android.su*
$XBIN/rm -f /data/dalvik-cache/*com.koushikdutta.superuser*
$XBIN/rm -f /data/dalvik-cache/*com.mgyun.shua.su*
$XBIN/rm -f /data/dalvik-cache/*Superuser.apk*
$XBIN/rm -f /data/dalvik-cache/*SuperUser.apk*
$XBIN/rm -f /data/dalvik-cache/*superuser.apk*
$XBIN/rm -f /data/dalvik-cache/*eu.chainfire.supersu*
$XBIN/rm -f /data/dalvik-cache/*Supersu.apk*
$XBIN/rm -f /data/dalvik-cache/*SuperSU.apk*
$XBIN/rm -f /data/dalvik-cache/*supersu.apk*
$XBIN/rm -f /data/dalvik-cache/*.oat
$XBIN/rm -f /data/app/com.noshufou.android.su-*
$XBIN/rm -f /data/app/com.koushikdutta.superuser-*
$XBIN/rm -f /data/app/com.mgyun.shua.su-*
$XBIN/rm -f /data/app/eu.chainfire.supersu-*

mkdir $BIN/.ext
$XBIN/cp /data/local/tmp/su /system/xbin/daemonsu
$XBIN/cp /data/local/tmp/su /system/xbin/su
$XBIN/cp /data/local/tmp/su /system/bin/.ext/.su
$XBIN/cp /data/local/tmp/Superuser.apk /system/app/Superuser.apk
$XBIN/cp /data/local/tmp/install-recovery.sh /system/etc/install-recovery.sh
$XBIN/cp /data/local/tmp/99SuperSUDaemon /system/etc/init.d/99SuperSUDaemon
$XBIN/echo 1 > /system/etc/.installed_su_daemon

set_perm 0 0 0777 /system/bin/.ext
set_perm 0 0 06755 /system/bin/.ext/.su
set_perm 0 0 06755 /system/xbin/su
set_perm 0 0 06755 /system/xbin/daemonsu
set_perm 0 0 0755 /system/etc/install-recovery.sh
set_perm 0 0 0755 /system/etc/init.d/99SuperSUDaemon
set_perm 0 0 0644 /system/etc/.installed_su_daemon

ch_con /system/bin/.ext/.su
ch_con /system/xbin/su
ch_con /system/xbin/daemonsu
ch_con /system/etc/install-recovery.sh
ch_con /system/etc/init.d/99SuperSUDaemon
ch_con /system/etc/.installed_su_daemon
ch_con /system/app/Superuser.apk

echo "--- DONE!"
exit