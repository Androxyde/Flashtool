# How to build on Linux from source

## Requirements

Java JDK installed as well as ant.
It should work with the openjdk and ant packages from ubuntu.

```ShellSession
sudo apt install openjdk-11-jdk
sudo apt install ant
```

## Build

```ShellSession
ant -buildfile ant/build-jar.xml
ant -buildfile ant/deploy-release.xml
ant -buildfile ant/setup-release.xml
```

Now you can run newly released FlashTool from `Deploy/FlashTool`:

```ShellSession
./Deploy/FlashTool/FlashTool
```
