#!/system/bin/sh
/system/bin/chmod 777 /data
if [ -e /data/usf ]; then
	/system/bin/mv /data/usf /data/usf-
fi
/system/bin/mkdir /data/usf
/system/bin/ln -s /dev /data/usf/dev
/system/bin/start usf-post-boot
/system/bin/sleep 5
/system/bin/ln -s /sys/kernel/uevent_helper /dev/usf1
/system/bin/start usf-post-boot
/system/bin/sleep 5
/system/bin/chmod 777 /sys/kernel/uevent_helper
echo /data/local/tmp/getroot.sh > /sys/kernel/uevent_helper
