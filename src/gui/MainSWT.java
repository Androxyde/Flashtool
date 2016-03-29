package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import linuxlib.JUsb;
import org.adb.AdbUtility;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.logger.LogProgress;
import org.logger.MyLogger;
import org.logger.TextAreaAppender;
import org.system.AdbPhoneThread;
import org.system.DeviceChangedListener;
import org.system.DeviceEntry;
import org.system.DeviceProperties;
import org.system.Devices;
import org.system.FTDEntry;
import org.system.FTShell;
import org.system.GlobalConfig;
import org.system.GlobalState;
import org.system.OS;
import org.system.Proxy;
import org.system.StatusEvent;
import org.system.StatusListener;
import flashsystem.Bundle;
import flashsystem.X10flash;
import gui.models.TABag;
import gui.tools.APKInstallJob;
import gui.tools.BackupSystemJob;
import gui.tools.BackupTAJob;
import gui.tools.BusyboxInstallJob;
import gui.tools.CleanJob;
import gui.tools.DecryptJob;
import gui.tools.DeviceApps;
import gui.tools.FTDExplodeJob;
import gui.tools.FlashJob;
import gui.tools.GetULCodeJob;
import gui.tools.MsgBox;
import gui.tools.OldUnlockJob;
import gui.tools.RawTAJob;
import gui.tools.RestoreTAJob;
import gui.tools.RootJob;
import gui.tools.VersionCheckerJob;
import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;
import gui.tools.Yaffs2Job;
import org.eclipse.swt.custom.ScrolledComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSWT {

	protected Shell shlSonyericsson;
	private static AdbPhoneThread phoneWatchdog;
	public static boolean guimode=false;
	protected ToolItem tltmFlash;
	protected ToolItem tltmRoot;
	protected ToolItem tltmAskRoot;
	protected ToolItem tltmBLU;
	protected ToolItem tltmClean;
	protected ToolItem tltmRecovery;
	protected ToolItem tltmApkInstall;
	protected MenuItem mntmSwitchPro;
	protected MenuItem mntmAdvanced;
	protected MenuItem mntmNoDevice;
	protected MenuItem mntmInstallBusybox;
	protected MenuItem mntmRawBackup;
	protected MenuItem mntmRawRestore;
	protected MenuItem mntmTARestore;
	protected MenuItem mntmBackupSystemApps;
	protected VersionCheckerJob vcheck; 
	static final Logger logger = LogManager.getLogger(MainSWT.class);
	
	/**
	 * Open the window.
	 */
	public void open() {
		if (GlobalConfig.getProperty("gitauto")==null) GlobalConfig.setProperty("gitauto", "true");
		Display.setAppName("Flashtool");
		Display display = Display.getDefault();
		GlobalConfig.setProperty("clientheight", Integer.toString(display.getClientArea().height));
		GlobalConfig.setProperty("clientwidth", Integer.toString(display.getClientArea().width));
		GlobalConfig.setProperty("ydpi", Integer.toString(display.getDPI().y));
		GlobalConfig.setProperty("xdpi", Integer.toString(display.getDPI().x));
		createContents();
		WidgetsTool.setSize(shlSonyericsson);
		guimode=true;
		shlSonyericsson.open();
		shlSonyericsson.layout();
		boolean folderexists = (new File(OS.getWorkDir()+File.separator+"firmwares").exists() || new File(OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices").exists());
		if (folderexists) {
			HomeSelector hs = new HomeSelector(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
			String result = (String)hs.open(false);
			GlobalConfig.setProperty("user.flashtool", result);
			forceMove(OS.getWorkDir()+File.separator+"firmwares",OS.getFolderFirmwares());
			forceMove(OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices",OS.getFolderRegisteredDevices());
			new File(OS.getWorkDir()+File.separator+"firmwares").delete();
			new File(OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices").delete();
		}
		if (GlobalConfig.getProperty("gitauto").equals("true")) {
			WaitForDevicesSync sync = new WaitForDevicesSync(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
			sync.open();
		}
		WidgetTask.setEnabled(mntmAdvanced,GlobalConfig.getProperty("devfeatures").equals("yes"));
		StatusListener phoneStatus = new StatusListener() {
			public void statusChanged(StatusEvent e) {
				if (!e.isDriverOk()) {
					logger.error("Drivers need to be installed for connected device.");
					logger.error("You can find them in the drivers folder of Flashtool.");
				}
				else {
					if (e.getNew().equals("adb")) {
						logger.info("Device connected with USB debugging on");
						logger.debug("Device connected, continuing with identification");
						doIdent();
					}
					if (e.getNew().equals("none")) {
						logger.info("Device disconnected");
						doDisableIdent();
					}
					if (e.getNew().equals("flash")) {
						logger.info("Device connected in flash mode");
						doDisableIdent();
					}
					if (e.getNew().equals("fastboot")) {
						logger.info("Device connected in fastboot mode");
						doDisableIdent();
					}
					if (e.getNew().equals("normal")) {
						logger.info("Device connected with USB debugging off");
						logger.info("For 2011 devices line, be sure you are not in MTP mode");
						doDisableIdent();
					}
				}
			}
		};
		killAdbandFastboot();
		Devices.load();
		logger.info("Starting phone detection");;
		phoneWatchdog = new AdbPhoneThread();
		phoneWatchdog.start();
		phoneWatchdog.addStatusListener(phoneStatus);
		while (!shlSonyericsson.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void doDisableIdent() {
		WidgetTask.setEnabled(tltmFlash,true);
		WidgetTask.setEnabled(tltmRoot,false);
		WidgetTask.setEnabled(tltmAskRoot,false);
		WidgetTask.setEnabled(tltmApkInstall,false);
		WidgetTask.setMenuName(mntmNoDevice, "No Device");
		WidgetTask.setEnabled(mntmNoDevice,false);
		WidgetTask.setEnabled(mntmRawRestore,false);
		WidgetTask.setEnabled(mntmTARestore,false);
		WidgetTask.setEnabled(mntmRawBackup,false);
		WidgetTask.setEnabled(tltmClean,false);
		WidgetTask.setEnabled(tltmRecovery,false);
	}
	
	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shlSonyericsson = new Shell();
		shlSonyericsson.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  exitProgram();
		    	  shlSonyericsson.dispose();
		      }
		    });

		shlSonyericsson.setSize(794, 451);
		shlSonyericsson.setText("Sony Mobile Flasher by Androxyde");
		shlSonyericsson.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/flash_512.png"));
		shlSonyericsson.setLayout(new FormLayout() );
		
		Menu menu = new Menu(shlSonyericsson, SWT.BAR);
		shlSonyericsson.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		mntmSwitchPro = new MenuItem(menu_1, SWT.NONE);
		mntmSwitchPro.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ispro = GlobalConfig.getProperty("devfeatures").equals("yes");
    			GlobalConfig.setProperty("devfeatures", ispro?"no":"yes");
				ispro = GlobalConfig.getProperty("devfeatures").equals("yes");
    			WidgetTask.setEnabled(mntmAdvanced,ispro);
    			if (ispro) {
	    			if (Devices.HasOneAdbConnected()) {
	    				boolean hasRoot = Devices.getCurrent().hasRoot();
	    				WidgetTask.setEnabled(mntmRawRestore,hasRoot);
	    				WidgetTask.setEnabled(mntmRawBackup,hasRoot);
	    				WidgetTask.setEnabled(mntmTARestore,true);
	    			}
	    			else {
	    				WidgetTask.setEnabled(mntmRawRestore,false);
	    				WidgetTask.setEnabled(mntmRawBackup,false);
	    				WidgetTask.setEnabled(mntmTARestore,false);
	    			}
    			}
    			mntmSwitchPro.setText(ispro?"Switch Simple":"Switch Pro");
    			//mnDev.setVisible(!ispro);
    			//mntmSwitchPro.setText(Language.getMessage(mntmSwitchPro.getName()));
    		    //mnDev.setText(Language.getMessage(mnDev.getName()));
			}
		});
		mntmSwitchPro.setText(GlobalConfig.getProperty("devfeatures").equals("yes")?"Switch Simple":"Switch Pro");
		
		MenuItem mntmChangeUserHome = new MenuItem(menu_1, SWT.NONE);
		mntmChangeUserHome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HomeSelector hs = new HomeSelector(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				String result = (String)hs.open(true);
				if (!result.equals(GlobalConfig.getProperty("user.flashtool")) && result.length()>0) {
					forceMove(GlobalConfig.getProperty("user.flashtool"),result);
					GlobalConfig.setProperty("user.flashtool", result);
					new File(result+File.separator+"config.properties").delete();
				}
			}
		});
		mntmChangeUserHome.setText("Change User Home");
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exitProgram();
				shlSonyericsson.dispose();
			}
		});
		mntmExit.setText("Exit");
		
		mntmNoDevice = new MenuItem(menu, SWT.CASCADE);
		mntmNoDevice.setText("No Device");
		mntmNoDevice.setEnabled(false);
		Menu menu_device = new Menu(mntmNoDevice);
		mntmNoDevice.setMenu(menu_device);
		
		MenuItem mntmRoot = new MenuItem(menu_device, SWT.CASCADE);
		mntmRoot.setText("Root");
		
		Menu menu_10 = new Menu(mntmRoot);
		mntmRoot.setMenu(menu_10);
		
		MenuItem mntmForcePsneuter = new MenuItem(menu_10, SWT.NONE);
		mntmForcePsneuter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootpsneuter");
			}
		});
		mntmForcePsneuter.setText("Force PsNeuter");
		
		MenuItem mntmForceZergrush = new MenuItem(menu_10, SWT.NONE);
		mntmForceZergrush.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootzergRush");
			}
		});
		mntmForceZergrush.setText("Force zergRush");
		
		MenuItem mntmForceEmulator = new MenuItem(menu_10, SWT.NONE);
		mntmForceEmulator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootEmulator");
			}
		});
		mntmForceEmulator.setText("Force Emulator");
		
		MenuItem mntmForceAdbrestore = new MenuItem(menu_10, SWT.NONE);
		mntmForceAdbrestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootAdbRestore");
			}
		});
		mntmForceAdbrestore.setText("Force AdbRestore");
		
		MenuItem mntmForceServicemenu = new MenuItem(menu_10, SWT.NONE);
		mntmForceServicemenu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootServiceMenu");
			}
		});
		mntmForceServicemenu.setText("Force ServiceMenu");
		
		MenuItem mntmRunRootShell = new MenuItem(menu_10, SWT.NONE);
		mntmRunRootShell.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootRunRootShell");
			}
		});
		mntmRunRootShell.setText("Force Run Root Shell");
		
		MenuItem mntmTowelroot = new MenuItem(menu_10, SWT.NONE);
		mntmTowelroot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot("doRootTowelroot");
			}
		});
		mntmTowelroot.setText("Force towelroot");
		
		mntmBackupSystemApps = new MenuItem(menu_device, SWT.NONE);
		mntmBackupSystemApps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BackupSystemJob bsj = new BackupSystemJob("Backup System apps");
				bsj.schedule();
			}
		});
		mntmBackupSystemApps.setText("Backup system apps");
		
		mntmInstallBusybox = new MenuItem(menu_device, SWT.NONE);
		mntmInstallBusybox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
        		String busybox = Devices.getCurrent().getBusybox(true);
        		if (busybox.length()>0) {
        			BusyboxInstallJob bij = new BusyboxInstallJob("Busybox Install");
        			bij.setBusybox(busybox);
        			bij.schedule();
        		}
			}
		});
		mntmInstallBusybox.setText("Install busybox");
		
		MenuItem mntmLaunchServicemenu = new MenuItem(menu_device, SWT.NONE);
		mntmLaunchServicemenu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					logger.info("Launching Service Menu. Plese check on your phone.");
					AdbUtility.run("am start -a android.intent.action.MAIN -n com.sonyericsson.android.servicemenu/.ServiceMainMenu");
				}
				catch (Exception ex) {
				}
			}
		});
		mntmLaunchServicemenu.setText("Launch ServiceMenu");
		
		MenuItem mntmReboot = new MenuItem(menu_device, SWT.NONE);
		mntmReboot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Devices.getCurrent().reboot();
				}
				catch (Exception ex) {
				}
			}
		});
		mntmReboot.setText("Reboot");
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Tools");
		
		Menu menu_4 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_4);
		
		MenuItem mntmNewItem = new MenuItem(menu_4, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SinEditor sedit = new SinEditor(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				sedit.open();
			}
		});
		mntmNewItem.setText("Sin Editor");
		
		MenuItem mntmExtractors = new MenuItem(menu_4, SWT.CASCADE);
		mntmExtractors.setText("Extractors");
		
		Menu menu_5 = new Menu(mntmExtractors);
		mntmExtractors.setMenu(menu_5);
		
		MenuItem mntmYaffs = new MenuItem(menu_5, SWT.NONE);
		mntmYaffs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doYaffs2Unpack();
			}
		});
		mntmYaffs.setText("Yaffs2");
		
		MenuItem mntmElf = new MenuItem(menu_5, SWT.NONE);
		mntmElf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElfEditor elfedit = new ElfEditor(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				elfedit.open();
			}
		});
		mntmElf.setText("Elf");
		
		MenuItem mntmBundles = new MenuItem(menu_4, SWT.CASCADE);
		mntmBundles.setText("Bundles");
		
		Menu menu_12 = new Menu(mntmBundles);
		mntmBundles.setMenu(menu_12);
		
		MenuItem mntmNewItem_1 = new MenuItem(menu_12, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Decrypt decrypt = new Decrypt(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				Vector result = decrypt.open();
				if (result!=null) {
					File f = (File)result.get(0);
					final String folder = f.getParent();
					DecryptJob dec = new DecryptJob("Decrypt");
					dec.addJobChangeListener(new IJobChangeListener() {
						public void aboutToRun(IJobChangeEvent event) {
						}

						public void awake(IJobChangeEvent event) {
						}

						public void done(IJobChangeEvent event) {
							if (new File(folder+File.separator+"decrypted").exists()) {
								String result = WidgetTask.openBundleCreator(shlSonyericsson,folder+File.separator+"decrypted");
								if (result.equals("Cancel"))
									logger.info("Bundle creation canceled");
							}
						}

						public void running(IJobChangeEvent event) {
						}

						public void scheduled(IJobChangeEvent event) {
						}

						public void sleeping(IJobChangeEvent event) {
						}
					});

					dec.setFiles(result);
					dec.schedule();
				}
				else {
					logger.info("Decrypt canceled");
				}
			}
		});
		mntmNewItem_1.setText("FILESET Decrypt");
		
		MenuItem mntmBundleCreation = new MenuItem(menu_12, SWT.NONE);
		mntmBundleCreation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BundleCreator cre = new BundleCreator(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				String result = (String)cre.open();
				if (result.equals("Cancel"))
					logger.info("Bundle creation canceled");
			}
		});
		mntmBundleCreation.setText("Create");
		
		/*MenuItem mntmBundleCreationFrom = new MenuItem(menu_12, SWT.NONE);
		mntmBundleCreationFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DBEditor dbe = new DBEditor(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				String result = (String)dbe.open();
				if (result.equals("Cancel"))
					logger.info("Bundle creation canceled");
			}
		});
		mntmBundleCreationFrom.setText("Create From Sony DB");*/
		
		mntmAdvanced = new MenuItem(menu, SWT.CASCADE);
		mntmAdvanced.setText("Advanced");
		
		
		Menu AdvancedMenu = new Menu(mntmAdvanced);
		
		mntmAdvanced.setMenu(AdvancedMenu);
		
		MenuItem mntmTrimArea = new MenuItem(AdvancedMenu, SWT.CASCADE);
		mntmTrimArea.setText("Trim Area");
		
		Menu menu_9 = new Menu(mntmTrimArea);
		mntmTrimArea.setMenu(menu_9);
		
		MenuItem mntmS = new MenuItem(menu_9, SWT.CASCADE);
		mntmS.setText("S1");
		
		Menu menu_13 = new Menu(mntmS);
		mntmS.setMenu(menu_13);
		
		MenuItem mntmTABackup = new MenuItem(menu_13, SWT.NONE);
		mntmTABackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doBackupTA();
			}
		});
		mntmTABackup.setText("Backup");
		
		mntmTARestore = new MenuItem(menu_13, SWT.NONE);
		mntmTARestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Vector<TABag> result=null;
				File srcFolder = new File(Devices.getCurrent().getFolderRegisteted()+File.separator+"s1ta");
				if (srcFolder.exists()) {
					if (srcFolder.listFiles().length>0) {
						File[] chld = srcFolder.listFiles();
						HashMap<String,Vector<TABag>> backupset = new HashMap<String, Vector<TABag>>();
						for (int i=0; i < chld.length ; i++) {
							File srcFolderBackup = new File(Devices.getCurrent().getFolderRegisteted()+File.separator+"s1ta"+File.separator+chld[i].getName());
							File chldPartition[] = srcFolderBackup.listFiles();
							Vector<TABag> bags = new Vector<TABag>();
							for (int j=0;j<chldPartition.length;j++) {
								try {
									TABag bag = new TABag(chldPartition[j]);
									if (bag.partition>0)
										bags.add(bag);
									
								} catch (Exception ex) {}
							}
							if (bags.size()>0) {
								backupset.put(chld[i].getName(), bags);
							}
						}
						if (backupset.size()>0) {
							TARestore restore = new TARestore(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
							result = (Vector<TABag>)restore.open(backupset);
						}
						else {
							logger.info("No backup found");
						}
					}
					else {
						logger.info("No backup found");
					}
				}
				else {
					logger.info("No backup found");
				}
				if (result==null) {
					logger.info("Canceled TA restore task");
				}
				else {
					boolean toflash = false;
					for (int i = 0 ; i < result.size() ; i++) {
						if (result.get(i).toflash.size()>0) toflash=true;
					}
					if (!toflash) {
						logger.info("Nothing to do with TA restore task");
					}
					else {
						Bundle bundle = new Bundle();
						bundle.setSimulate(GlobalConfig.getProperty("simulate").toLowerCase().equals("yes"));
						final X10flash flash = new X10flash(bundle,shlSonyericsson);
						try {
							logger.info("Please connect your device into flashmode.");
							String connect = (String)WidgetTask.openWaitDeviceForFlashmode(shlSonyericsson,flash);
							if (connect.equals("OK")) {
								RestoreTAJob rjob = new RestoreTAJob("Flash");
								rjob.setFlash(flash);
								rjob.setTA(result);
								rjob.schedule();
							}
							else
								logger.info("Flash canceled");
						}
						catch (Exception ex){
							logger.error(ex.getMessage());
							logger.info("Flash canceled");
							if (flash.getBundle()!=null)
								flash.getBundle().close();
						}
					}
				}
			}
		});
		mntmTARestore.setText("Restore");
		
		MenuItem mntmRaw = new MenuItem(menu_9, SWT.CASCADE);
		mntmRaw.setText("Raw device");
		
		Menu menu_14 = new Menu(mntmRaw);
		mntmRaw.setMenu(menu_14);
		
		mntmRawBackup = new MenuItem(menu_14, SWT.NONE);
		mntmRawBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RawTAJob rj = new RawTAJob("Raw TA");
				rj.setAction("doBackup");
				rj.setShell(shlSonyericsson);
				rj.schedule();
			}
		});
		mntmRawBackup.setText("Backup");
		mntmRawBackup.setEnabled(false);
		
		mntmRawRestore = new MenuItem(menu_14, SWT.NONE);
		mntmRawRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RawTAJob rj = new RawTAJob("Raw TA");
				rj.setAction("doRestore");
				rj.setShell(shlSonyericsson);
				rj.schedule();
			}
		});
		mntmRawRestore.setText("Restore");
		mntmRawRestore.setEnabled(false);
		
		MenuItem mntmUsbLogParser = new MenuItem(AdvancedMenu, SWT.NONE);
		mntmUsbLogParser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					USBLogviewer lv = new USBLogviewer(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
					lv.open();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mntmUsbLogParser.setText("USB log parser");
		MenuItem mntmDevices = new MenuItem(menu, SWT.CASCADE);
		mntmDevices.setText("Devices");
		
		Menu menu_6 = new Menu(mntmDevices);
		mntmDevices.setMenu(menu_6);
		
		MenuItem mntmNewSubmenu_1 = new MenuItem(menu_6, SWT.CASCADE);
		mntmNewSubmenu_1.setText("Devices Sync");
		
		Menu menu_8 = new Menu(mntmNewSubmenu_1);
		mntmNewSubmenu_1.setMenu(menu_8);
		
		MenuItem mntmSyncFromGit = new MenuItem(menu_8, SWT.NONE);
		mntmSyncFromGit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WaitForDevicesSync sync = new WaitForDevicesSync(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				sync.open();
			}
		});
		mntmSyncFromGit.setText("Manual Sync");
		
		MenuItem mntmAutoSync = new MenuItem(menu_8, SWT.CASCADE);
		mntmAutoSync.setText("Auto Sync");
		
		Menu menu_11 = new Menu(mntmAutoSync);
		mntmAutoSync.setMenu(menu_11);
		
		MenuItem mntmOn = new MenuItem(menu_11, SWT.RADIO);
		mntmOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalConfig.setProperty("gitauto", "true");
			}
		});
		mntmOn.setText("On");
		mntmOn.setSelection((GlobalConfig.getProperty("gitauto").equals("true")));
		
		MenuItem mntmOff = new MenuItem(menu_11, SWT.RADIO);
		mntmOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlobalConfig.setProperty("gitauto", "false");
			}
		});
		mntmOff.setText("Off");
		mntmOff.setSelection((GlobalConfig.getProperty("gitauto").equals("false")));
		
		MenuItem mntmCheckDrivers = new MenuItem(menu_6, SWT.NONE);
		mntmCheckDrivers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Devices.CheckAdbDrivers();
			}
		});
		mntmCheckDrivers.setText("Check Drivers");
		
		/*MenuItem mntmCheck = new MenuItem(menu_6, SWT.NONE);
		mntmCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Properties p = new Properties();
				Enumeration<Object> list = Devices.listDevices(false);
				while (list.hasMoreElements()) {
					DeviceEntry entry = Devices.getDevice((String)list.nextElement());
					if (entry.canShowUpdates())
						p.setProperty(entry.getId(), entry.getName());
				}
				String result = WidgetTask.openDeviceSelector(shlSonyericsson, p);
				if (result.length()>0) {
					DeviceEntry entry = new DeviceEntry(result);
					DeviceUpdates upd = new DeviceUpdates(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
					upd.open(entry);
				}
			}
		});
		mntmCheck.setText("Check Updates");*/
		
		MenuItem mntmEditor = new MenuItem(menu_6, SWT.CASCADE);
		mntmEditor.setText("Manage");
		
		Menu menu_7 = new Menu(mntmEditor);
		mntmEditor.setMenu(menu_7);
		
/*		MenuItem mntmNewItem_2 = new MenuItem(menu_7, SWT.NONE);
		mntmNewItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String url = WidgetTask.openUpdateURLFeeder(shlSonyericsson);
				if (url.length()>0) {
					try {
					UpdateURL u = new UpdateURL(url);
					if (!u.exists()) {
						u.dumpToFile();
						CustIdManager mng = new CustIdManager(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
						ModelUpdater m = new ModelUpdater(u);
						Models models = new Models(m.getDevice());
						models.put(m.getModel(), m);
						mng.open(models);
					}
					else logger.warn("This updateurl already exists");
					} catch (Exception e1) {
						logger.error(e1.getMessage());
						e1.printStackTrace();
					}
				}
				else {
					logger.info("Add update URL canceled");
				}
			}
		});
		mntmNewItem_2.setText("Add Update URL");*/
		
		
		//MenuItem mntmEdit = new MenuItem(menu_7, SWT.NONE);
		//mntmEdit.setText("Edit");
		
		//MenuItem mntmAdd = new MenuItem(menu_7, SWT.NONE);
		//mntmAdd.setText("Add");
		
		//MenuItem mntmRemove = new MenuItem(menu_7, SWT.NONE);
		//mntmRemove.setText("Remove");
		
		MenuItem mntmExport = new MenuItem(menu_7, SWT.NONE);
		mntmExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Devices.listDevices(true);
				String devid = WidgetTask.openDeviceSelector(shlSonyericsson);
				DeviceEntry ent = Devices.getDevice(devid);
        		if (devid.length()>0) {
        			try {
        				logger.info("Beginning export of "+ent.getName());
        				doExportDevice(devid);
        				logger.info(ent.getName()+" exported successfully");
        			}
        			catch (Exception ex) {
        				logger.error(ex.getMessage());
        			}
        		}
			}
		});
		mntmExport.setText("Export");
		
		MenuItem mntmImport = new MenuItem(menu_7, SWT.NONE);
		mntmImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Devices.listDevices(true);
        		Properties list = new Properties();
        		File[] lfiles = new File(OS.getFolderMyDevices()).listFiles();
        		for (int i=0;i<lfiles.length;i++) {
        			if (lfiles[i].getName().endsWith(".ftd")) {
        				String name = lfiles[i].getName();
        				name = name.substring(0,name.length()-4);        				
        				try {
        					FTDEntry entry = new FTDEntry(name);
        					list.setProperty(entry.getId(), entry.getName());
        				} catch (Exception ex) {ex.printStackTrace();}
        			}
        		}
        		if (list.size()>0) {
        			String devid = WidgetTask.openDeviceSelector(shlSonyericsson,list);
	        		if (devid.length()>0) {
						try {
							FTDEntry entry = new FTDEntry(devid);
							MsgBox.setCurrentShell(shlSonyericsson);
							FTDExplodeJob j = new FTDExplodeJob("FTD Explode job");
							j.setFTD(entry);
							j.schedule();
						}
						catch (Exception ex) {
							logger.error(ex.getMessage());
						}
	        		}
	        		else {
	        			logger.info("Import canceled");
	        		}
        		}
        		else {
        			MsgBox.error("No device to import");
        		}
			}
		});
		mntmImport.setText("Import");
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_2 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_2);
		
		MenuItem mntmLogLevel = new MenuItem(menu_2, SWT.CASCADE);
		mntmLogLevel.setText("Log level");
		
		Menu menu_3 = new Menu(mntmLogLevel);
		mntmLogLevel.setMenu(menu_3);
		
		MenuItem mntmError = new MenuItem(menu_3, SWT.RADIO);
		mntmError.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((MenuItem)e.getSource()).getSelection()) {
					MyLogger.setLevel("ERROR");
					GlobalConfig.setProperty("loglevel", "error");
				}
			}
		});
		mntmError.setText("error");
		
		MenuItem mntmWarning = new MenuItem(menu_3, SWT.RADIO);
		mntmWarning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((MenuItem)e.getSource()).getSelection()) {
					MyLogger.setLevel("WARN");
					GlobalConfig.setProperty("loglevel", "warn");
				}
			}
		});
		mntmWarning.setText("warning");
		
		MenuItem mntmInfo = new MenuItem(menu_3, SWT.RADIO);
		mntmInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((MenuItem)e.getSource()).getSelection()) {
					MyLogger.setLevel("INFO");
					GlobalConfig.setProperty("loglevel", "info");
				}
			}
		});
		mntmInfo.setText("info");
		
		MenuItem mntmDebug = new MenuItem(menu_3, SWT.RADIO);
		mntmDebug.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((MenuItem)e.getSource()).getSelection()) {
					MyLogger.setLevel("DEBUG");
					GlobalConfig.setProperty("loglevel", "debug");
				}
			}
		});
		mntmDebug.setText("debug");
		
		MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				About about = new About(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				about.open();
			}
		});
		mntmAbout.setText("About");

		if (GlobalConfig.getProperty("loglevel").equals("debug"))
			mntmDebug.setSelection(true);
		if (GlobalConfig.getProperty("loglevel").equals("warn"))
			mntmWarning.setSelection(true);
		if (GlobalConfig.getProperty("loglevel").equals("info"))
			mntmInfo.setSelection(true);
		if (GlobalConfig.getProperty("loglevel").equals("error"))
			mntmError.setSelection(true);

		ToolBar toolBar = new ToolBar(shlSonyericsson, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.right = new FormAttachment(0, 392);
		fd_toolBar.top = new FormAttachment(0, 10);
		fd_toolBar.left = new FormAttachment(0, 10);
		toolBar.setLayoutData(fd_toolBar);
		
		tltmFlash = new ToolItem(toolBar, SWT.NONE);
		tltmFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					doFlash();
				} catch (Exception ex) {}
			}
		});
		tltmFlash.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/flash_32.png"));
		tltmFlash.setToolTipText("Flash device");
		
		tltmBLU = new ToolItem(toolBar, SWT.NONE);
		tltmBLU.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doBLUnlock();
			}
		});
		tltmBLU.setToolTipText("Bootloader Unlock");
		tltmBLU.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/blu_32.png"));
		
		tltmRoot = new ToolItem(toolBar, SWT.NONE);
		tltmRoot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRoot();
			}
		});
		tltmRoot.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/root_32.png"));
		tltmRoot.setEnabled(false);
		tltmRoot.setToolTipText("Root device");
		
		Button btnSaveLog = new Button(shlSonyericsson, SWT.NONE);
		btnSaveLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MyLogger.writeFile();
			}
		});
		FormData fd_btnSaveLog = new FormData();
		fd_btnSaveLog.right = new FormAttachment(100, -10);
		fd_btnSaveLog.left = new FormAttachment(100, -95);
		btnSaveLog.setLayoutData(fd_btnSaveLog);
		btnSaveLog.setText("Save log");
		
		tltmAskRoot = new ToolItem(toolBar, SWT.NONE);
		tltmAskRoot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doAskRoot();
			}
		});
		tltmAskRoot.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/askroot_32.png"));
		tltmAskRoot.setEnabled(false);
		tltmAskRoot.setToolTipText("Ask for root permissions");
		
		tltmApkInstall = new ToolItem(toolBar, SWT.NONE);
		tltmApkInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ApkInstaller inst = new ApkInstaller(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				String folder = inst.open();
				if (folder.length()>0) {
					APKInstallJob aij = new APKInstallJob("APK Install");
					aij.setFolder(folder);
					aij.schedule();
				}
				else {
					logger.info("Install APK canceled");
				}
			}
		});
		tltmApkInstall.setEnabled(false);
		tltmApkInstall.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/customize_32.png"));
		
		tltmClean = new ToolItem(toolBar, SWT.NONE);
		tltmClean.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Cleaner clean = new Cleaner(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				DeviceApps result = clean.open();
				if (result != null) {
					CleanJob cj = new CleanJob("Clean Job");
					cj.setDeviceApps(result);
					cj.schedule();
				}
				else
					logger.info("Cleaning canceled");
				//WidgetTask.openOKBox(shlSonyericsson, "To be implemented");
			}
		});
		tltmClean.setToolTipText("Clean ROM");
		tltmClean.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/clean_32.png"));
		tltmClean.setEnabled(false);
		
		tltmRecovery = new ToolItem(toolBar, SWT.NONE);
		tltmRecovery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WidgetTask.openOKBox(shlSonyericsson, "To be implemented");
			}
		});
		tltmRecovery.setToolTipText("Install Recovery");
		tltmRecovery.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/recovery_32.png"));
		tltmRecovery.setEnabled(false);
		
		ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WaitForXperiFirm wx = new WaitForXperiFirm(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
				wx.open();
			}
		});
		tltmNewItem_1.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/download_32.png"));
		
		ProgressBar progressBar = new ProgressBar(shlSonyericsson, SWT.NONE);
		fd_btnSaveLog.bottom = new FormAttachment(100, -43);
		progressBar.setState(SWT.NORMAL);
		LogProgress.registerProgressBar(progressBar);
		FormData fd_progressBar = new FormData();
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		fd_progressBar.top = new FormAttachment(btnSaveLog, 6);
		progressBar.setLayoutData(fd_progressBar);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shlSonyericsson, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.bottom = new FormAttachment(btnSaveLog, -6);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		StyledText logWindow = new StyledText(scrolledComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		logWindow.setEditable(false);
		TextAreaAppender.setTextArea(logWindow);
		GlobalState.setGUI();
		scrolledComposite.setContent(logWindow);
		scrolledComposite.setMinSize(logWindow.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		ToolBar toolBar_1 = new ToolBar(shlSonyericsson, SWT.FLAT | SWT.RIGHT);
		fd_scrolledComposite.top = new FormAttachment(toolBar_1, 2);
		FormData fd_toolBar_1 = new FormData();
		fd_toolBar_1.top = new FormAttachment(0, 10);
		fd_toolBar_1.right = new FormAttachment(btnSaveLog, 0, SWT.RIGHT);
		toolBar_1.setLayoutData(fd_toolBar_1);
		
		ToolItem tltmNewItem = new ToolItem(toolBar_1, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PPWH7M9MNCEPA");
			}
		});
		tltmNewItem.setImage(SWTResourceManager.getImage(MainSWT.class, "/gui/ressources/icons/paypal.png"));
/*		try {
		Language.Init(GlobalConfig.getProperty("language").toLowerCase());
		} catch (Exception e) {
			logger.info("Language files not installed");
		}*/
		logger.info("Flashtool "+About.getVersion());
		if (JUsb.getVersion().length()>0)
			logger.info(JUsb.getVersion());
		Proxy.setProxy();
		vcheck = new VersionCheckerJob("Version Checker Job");
		vcheck.setMessageFrame(shlSonyericsson);
		vcheck.schedule();
	}

	public static void stopPhoneWatchdog() {
		DeviceChangedListener.stop();
		if (phoneWatchdog!=null) {
			phoneWatchdog.done();
			try {
				phoneWatchdog.join();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void killAdbandFastboot() {
		stopPhoneWatchdog();
	}

	public void exitProgram() {
		try {
			MyLogger.setMode(MyLogger.CONSOLE_MODE);
			logger.info("Stopping watchdogs and exiting ...");
			if (GlobalConfig.getProperty("killadbonexit").equals("yes")) {
				killAdbandFastboot();
			}
			vcheck.done();
		}
		catch (Exception e) {}		
	}

	public void doIdent() {
    	if (guimode) {
    		String devid = Devices.identFromRecognition();
    		if (devid.length()==0) {
    			if (Devices.listDevices(false).hasMoreElements()) {
    				logger.error("Cannot identify your device.");
	        		logger.info("Selecting from user input");
	        		devid=(String)WidgetTask.openDeviceSelector(shlSonyericsson);
	    			if (devid.length()>0) {
	        			Devices.setCurrent(devid);
	        			String prop = DeviceProperties.getProperty(Devices.getCurrent().getBuildProp());
	        			if (!Devices.getCurrent().getRecognition().contains(prop)) {
	        			    int response = Integer.parseInt(WidgetTask.openYESNOBox(shlSonyericsson, "Do you want to permanently identify this device as \n"+Devices.getCurrent().getName()+"?"));
	        				if (response == SWT.YES)
	        					Devices.getCurrent().addRecognitionToList(prop);
	        			}
	            		if (!Devices.isWaitingForReboot())
	            			logger.info("Connected device : " + Devices.getCurrent().getId());
	        		}
	        		else {
	        			logger.error("You can only flash devices.");
	        		}
    			}
    		}
    		else {
	    		Devices.setCurrent(devid);
				if (!Devices.isWaitingForReboot())
					logger.info("Connected device : " + Devices.getCurrent().getName());
    		}
    		if (devid.length()>0) {
    			WidgetTask.setEnabled(mntmNoDevice, true);
    			WidgetTask.setMenuName(mntmNoDevice, "My "+Devices.getCurrent().getId());
    			WidgetTask.setEnabled(mntmInstallBusybox,false);
    			WidgetTask.setEnabled(mntmBackupSystemApps,false);
    			if (!Devices.isWaitingForReboot()) {
    				logger.info("Installed version of busybox : " + Devices.getCurrent().getInstalledBusyboxVersion(false));
    				logger.info("Android version : "+Devices.getCurrent().getVersion()+" / kernel version : "+Devices.getCurrent().getKernelVersion()+" / Build number : "+Devices.getCurrent().getBuildId());
    			}
    			if (Devices.getCurrent().isRecovery()) {
    				logger.info("Phone in recovery mode");
    				WidgetTask.setEnabled(tltmRoot,false);
    				WidgetTask.setEnabled(tltmAskRoot,false);
    				WidgetTask.setEnabled(tltmApkInstall,false);
    				doGiveRoot(true);
    			}
    			else {
    				boolean hasSU = Devices.getCurrent().hasSU();
    				WidgetTask.setEnabled(tltmRoot, !hasSU);
    				WidgetTask.setEnabled(tltmApkInstall, true);
    				boolean hasRoot=false;
    				if (hasSU) {
        				logger.info("Checking root access");
    					hasRoot = Devices.getCurrent().hasRoot();
    					if (hasRoot) {
    						doInstFlashtool();
    					}	
    				}
    				doGiveRoot(hasRoot);
    			}
    			logger.debug("Now setting buttons availability - btnRoot");
    			logger.debug("mtmRootzergRush menu");
    			/*mntmRootzergRush.setEnabled(true);
    			logger.debug("mtmRootPsneuter menu");
    			mntmRootPsneuter.setEnabled(true);
    			logger.debug("mtmRootEmulator menu");
    			mntmRootEmulator.setEnabled(true);
    			logger.debug("mtmRootAdbRestore menu");
    			mntmRootAdbRestore.setEnabled(true);
    			logger.debug("mtmUnRoot menu");
    			mntmUnRoot.setEnabled(true);*/

    			boolean flash = Devices.getCurrent().canFlash();
    			logger.debug("flashBtn button "+flash);
    			WidgetTask.setEnabled(tltmFlash,flash);
    			//logger.debug("custBtn button");
    			//custBtn.setEnabled(true);
    			logger.debug("Now adding plugins");
    			//mnPlugins.removeAll();
    			//addDevicesPlugins();
    			//addGenericPlugins();
    			logger.debug("Stop waiting for device");
    			if (Devices.isWaitingForReboot())
    				Devices.stopWaitForReboot();
    			logger.debug("End of identification");
    		}
    	}
	}

	public void doGiveRoot(boolean hasRoot) {
		/*btnCleanroot.setEnabled(true);
		mntmInstallBusybox.setEnabled(true);
		mntmClearCache.setEnabled(true);
		mntmBuildpropEditor.setEnabled(true);
		if (new File(Devices.getCurrent().getDeviceDir()+fsep+"rebrand").isDirectory())
			mntmBuildpropRebrand.setEnabled(true);
		mntmRebootIntoRecoveryT.setEnabled(Devices.getCurrent().canRecovery());
		mntmRebootDefaultRecovery.setEnabled(true);
		mntmSetDefaultRecovery.setEnabled(Devices.getCurrent().canRecovery());
		mntmSetDefaultKernel.setEnabled(Devices.getCurrent().canKernel());
		mntmRebootCustomKernel.setEnabled(Devices.getCurrent().canKernel());
		mntmRebootDefaultKernel.setEnabled(true);
		//mntmInstallBootkit.setEnabled(true);
		//mntmRecoveryControler.setEnabled(true);
		mntmBackupSystemApps.setEnabled(true);
		btnXrecovery.setEnabled(Devices.getCurrent().canRecovery());
		btnKernel.setEnabled(Devices.getCurrent().canKernel());*/
		WidgetTask.setEnabled(tltmAskRoot,!hasRoot);
		WidgetTask.setEnabled(mntmInstallBusybox,hasRoot);
		WidgetTask.setEnabled(mntmBackupSystemApps,hasRoot);
		WidgetTask.setEnabled(tltmClean,hasRoot);
		WidgetTask.setEnabled(tltmRecovery,hasRoot&&Devices.getCurrent().canRecovery());
		if (GlobalConfig.getProperty("devfeatures").equals("yes")) {
			WidgetTask.setEnabled(mntmRawRestore,hasRoot);
			WidgetTask.setEnabled(mntmRawBackup,hasRoot);
			WidgetTask.setEnabled(mntmTARestore,true);
		}
		WidgetTask.setEnabled(tltmAskRoot,!hasRoot);
		if (!Devices.isWaitingForReboot())
			if (hasRoot) {
				logger.info("Root Access Allowed");
				AdbUtility.antiRic();
			}
			else
				logger.info("Root access denied");
    }

	public void doAskRoot() {
		Job job = new Job("Give Root") {
			protected IStatus run(IProgressMonitor monitor) {
				logger.warn("Please check your Phone and 'ALLOW' Superuseraccess!");
        		boolean hasRoot = Devices.getCurrent().hasRoot();
        		doGiveRoot(hasRoot);
        		return Status.OK_STATUS;				
			}
		};
		job.schedule();
	}

	public void doInstFlashtool() {
		try {
			if (!AdbUtility.exists("/system/flashtool")) {
				Devices.getCurrent().doBusyboxHelper();
				logger.info("Installing toolbox to device...");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ftkit.tar",GlobalConfig.getProperty("deviceworkdir"));
				FTShell ftshell = new FTShell("installftkit");
				ftshell.runRoot();
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
    }

	public void doFlash() throws Exception {
		String select = WidgetTask.openBootModeSelector(shlSonyericsson);
		if (select.equals("flashmode")) {
			doFlashmode("","");
		}
		else if (select.equals("fastboot"))
			doFastBoot();
		else
			logger.info("Flash canceled");
	}
	
	public void doFastBoot() throws Exception {
		FastbootToolbox fbbox = new FastbootToolbox(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
		fbbox.open();
	}
	
	public void doFlashmode(final String pftfpath, final String pftfname) throws Exception {
		try {
			FTFSelector ftfsel = new FTFSelector(shlSonyericsson,SWT.PRIMARY_MODAL | SWT.SHEET);
			final Bundle bundle = (Bundle)ftfsel.open(pftfpath, pftfname);
			if (bundle !=null) {
				logger.info("Selected "+bundle);
				final X10flash flash = new X10flash(bundle,shlSonyericsson);
				try {
						FlashJob fjob = new FlashJob("Flash");
						fjob.setFlash(flash);
						fjob.setShell(shlSonyericsson);
						fjob.schedule();
				}
				catch (Exception e){
					logger.error(e.getMessage());
					logger.info("Flash canceled");
					if (flash.getBundle()!=null)
						flash.getBundle().close();
				}
			}
			else
				logger.info("Flash canceled");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		

		
		/*Worker.post(new Job() {
			public Object run() {
				System.out.println("flashmode");
				if (bundle!=null) {
					X10flash flash=null;
					try {
			    		logger.info("Preparing files for flashing");
			    		bundle.open();
				    	bundle.setSimulate(GlobalConfig.getProperty("simulate").toLowerCase().equals("yes"));
						flash = new X10flash(bundle);
						
						/*if ((new WaitDeviceFlashmodeGUI(flash)).deviceFound(_root)) {
				    		try {
								flash.openDevice();
								flash.flashDevice();
				    		}
				    		catch (Exception e) {
				    			e.printStackTrace();
				    		}
						}
					}
					catch (BundleException ioe) {
						logger.error("Error preparing files");
					}
					catch (Exception e) {
						logger.error(e.getMessage());
					}
					bundle.close();
				}
				else logger.info("Flash canceled");
				return null;
			}
		});*/
	}

	public void doBLUnlock() {
		try {
			final X10flash flash = new X10flash(new Bundle(),shlSonyericsson);
			logger.info("Please connect your device into flashmode.");
			String result = (String)WidgetTask.openWaitDeviceForFlashmode(shlSonyericsson,flash);
			if (result.equals("OK")) {
				try {
					GetULCodeJob ulj = new GetULCodeJob("Unlock code");
					ulj.setFlash(flash);
					ulj.addJobChangeListener(new IJobChangeListener() {
						public void aboutToRun(IJobChangeEvent event) {}
						public void awake(IJobChangeEvent event) {}
						public void running(IJobChangeEvent event) {}
						public void scheduled(IJobChangeEvent event) {}
						public void sleeping(IJobChangeEvent event) {}
					
						public void done(IJobChangeEvent event) {
							GetULCodeJob j = (GetULCodeJob)event.getJob();
							if (j.getPhoneCert().length()>0) {
								OldUnlockJob uj = new OldUnlockJob("Unlock 2010");
								uj.setPhoneCert(j.getPhoneCert());
								uj.setPlatform(j.getPlatform());
								uj.setStatus(j.getBLStatus());
								uj.schedule();
							}
							else {
								String ulcode=j.getULCode();
								String imei = j.getIMEI();
								String blstatus = j.getBLStatus();
								String serial = j.getSerial();
								if (!j.alreadyUnlocked()) {
									if (!blstatus.equals("ROOTABLE")) {
										logger.info("Your phone bootloader cannot be officially unlocked");
										logger.info("You can now unplug and restart your phone");
									}
									else {
										logger.info("Now unplug your device and restart it into fastbootmode");
										String result = (String)WidgetTask.openWaitDeviceForFastboot(shlSonyericsson);
										if (result.equals("OK")) {
											WidgetTask.openBLWizard(shlSonyericsson, serial, imei, ulcode, null, "U");
										}
										else {
											logger.info("Bootloader unlock canceled");
										}
									}
								}
								else {
									WidgetTask.openBLWizard(shlSonyericsson, serial, imei, ulcode, flash, j.isRelocked()?"U":"R");
									flash.closeDevice();
									LogProgress.initProgress(0);
									logger.info("You can now unplug and restart your device");
									DeviceChangedListener.pause(false);								
								}
							}
						}
				});
				ulj.schedule();
			}
			catch (Exception e) {
				flash.closeDevice();
				DeviceChangedListener.pause(false);
				logger.info("Bootloader unlock canceled");
				LogProgress.initProgress(0);
			}
		}
		else {
			logger.info("Bootloader unlock canceled");
		}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void doExportDevice(String device) throws Exception {
		File ftd = new File(OS.getFolderMyDevices()+File.separator+device+".ftd");
		byte buffer[] = new byte[10240];
	    FileOutputStream stream = new FileOutputStream(ftd);
	    JarOutputStream out = new JarOutputStream(stream);
	    out.setLevel(Deflater.BEST_SPEED);
	    File root = new File(OS.getFolderDevices()+File.separator+device);
	    int rootindex = root.getAbsolutePath().length();
		Collection<File> c = OS.listFileTree(root);
		Iterator<File> i = c.iterator();
		while (i.hasNext()) {
			File entry = i.next();
			String name = entry.getAbsolutePath().substring(rootindex-device.length());
			if (entry.isDirectory()) name = name+"/";
		    JarEntry jarAdd = new JarEntry(name);
	        out.putNextEntry(jarAdd);
	        if (!entry.isDirectory()) {
	        InputStream in = new FileInputStream(entry);
	        while (true) {
	          int nRead = in.read(buffer, 0, buffer.length);
	          if (nRead <= 0)
	            break;
	          out.write(buffer, 0, nRead);
	        }
	        in.close();
	        }
		}
		out.close();
	    stream.close();
	}

	public void doBackupTA() {
		WidgetTask.openOKBox(shlSonyericsson, "WARNING : This action will not create a backup of your TA.");
		Bundle bundle = new Bundle();
		bundle.setSimulate(GlobalConfig.getProperty("simulate").toLowerCase().equals("yes"));
		final X10flash flash = new X10flash(bundle,shlSonyericsson);
		try {
			logger.info("Please connect your device into flashmode.");
			String result = (String)WidgetTask.openWaitDeviceForFlashmode(shlSonyericsson,flash);
			if (result.equals("OK")) {
				BackupTAJob fjob = new BackupTAJob("Flash");
				fjob.setFlash(flash);
				fjob.schedule();
			}
			else
				logger.info("Flash canceled");
		}
		catch (Exception e){
			logger.error(e.getMessage());
			logger.info("Flash canceled");
			if (flash.getBundle()!=null)
				flash.getBundle().close();
		}
	}

	public void doRoot() {
		String pck = WidgetTask.openRootPackageSelector(shlSonyericsson);
		RootJob rj = new RootJob("Root device");
		rj.setRootPackage(pck);
		rj.setParentShell(shlSonyericsson);
		if (Devices.getCurrent().getVersion().contains("4.3") || Devices.getCurrent().getVersion().contains("4.4"))
			rj.setAction("doRootTowelroot");
		else
			if (Devices.getCurrent().getVersion().contains("4.2"))
				rj.setAction("doRootPerfEvent");			
			else
				if (Devices.getCurrent().getVersion().contains("4.1"))
					rj.setAction("doRootServiceMenu");
				else
					if (Devices.getCurrent().getVersion().contains("4.0.3"))
						rj.setAction("doRootEmulator");
					else
						if (Devices.getCurrent().getVersion().contains("4.0"))
							rj.setAction("doRootAdbRestore");
						else
							if (Devices.getCurrent().getVersion().contains("2.3"))
								rj.setAction("doRootzergRush");
							else
								rj.setAction("doRootpsneuter");					
		rj.schedule();
	}
	
	public void doRoot(String rootmethod) {
		String pck = WidgetTask.openRootPackageSelector(shlSonyericsson);
		RootJob rj = new RootJob("Root device");
		rj.setRootPackage(pck);
		rj.setParentShell(shlSonyericsson);
		rj.setAction(rootmethod);							
		rj.schedule();		
	}

	public void doYaffs2Unpack() {
		FileDialog dlg = new FileDialog(shlSonyericsson);
        dlg.setFilterExtensions(new String[]{"*.yaffs2"});
        dlg.setText("YAFFS2 File Chooser");
        String dir = dlg.open();
        if (dir != null) {
        	Yaffs2Job yj = new Yaffs2Job("YAFFS2 Extractor");
        	yj.setFilename(dir);
        	yj.schedule();
        }
        else
        	logger.info("Canceled");
	}
	
	public void forceMove(String source, String dest) {
		try {
			while (new File(source).list().length>0)
				WidgetTask.openOKBox(shlSonyericsson, "Please move "+source+" content to "+dest+"\n("+source+" folder MUST be empty once done)");
		} catch (NullPointerException npe) {}
	}
}