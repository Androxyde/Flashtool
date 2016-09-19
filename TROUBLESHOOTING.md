# Troubleshooting

## Could not open X display with sudo

```ShellSession
** (java:32672): WARNING **: Could not open X display
No protocol specified
Unable to init server: Could not connect: Connection refused
Exception in thread "main" org.eclipse.swt.SWTError: No more handles [gtk_init_check() failed]
```

Try to add access:

```ShellSession
xhost local:root
```
