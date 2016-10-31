# How to build on Linux from source

## Requirements

Download [Java SE Runtime Environment](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) 
for Linux x86 and Linux x64 platform in ".tar.gz" format.

```
cd ./jre
wget http://download.oracle.com/otn-pub/java/jdk/8u101-b13/jre-8u101-linux-i586.tar.gz
tar xf jre-8u101-linux-i586.tar.gz
mv jre1.8.0_101 linjre32
wget http://download.oracle.com/otn-pub/java/jdk/8u101-b13/jre-8u101-linux-x64.tar.gz
tar xf jre-8u101-linux-x64.tar.gz
mv jre1.8.0_101 linjre64
tar cf linjre.tar linjre32 linjre64
cd ..
```

Install p7zip:

```ShellSession
apt-get install p7zip
```

Add bin folder:

```ShellSession
mkdir ./bin
```

## Build

```ShellSession
ant -buildfile ant/deploy-release.xml
ant -buildfile ant/setup-linux.xml binaries
```

Now you can run newly released FlashTool from `Deploy/FlashTool`:

```ShellSession
./../Deploy/FlashTool/FlashTool
```
