package gui;

import gui.tools.WidgetTask;
import gui.tools.FastBootToolBoxJob;
import gui.tools.WidgetsTool;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FastbootToolbox extends Dialog {

	protected Object result;
	protected Shell shlFastbootToolbox;
	private static Logger logger = Logger.getLogger(FastbootToolbox.class);

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FastbootToolbox(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlFastbootToolbox);
		shlFastbootToolbox.open();
		shlFastbootToolbox.layout();
		Display display = getParent().getDisplay();
		while (!shlFastbootToolbox.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlFastbootToolbox = new Shell(getParent(), getStyle());
		shlFastbootToolbox.setSize(673, 244);
		shlFastbootToolbox.setText("Fastboot Toolbox");
		shlFastbootToolbox.setLayout(new GridLayout(3, false));
		new Label(shlFastbootToolbox, SWT.NONE);
		new Label(shlFastbootToolbox, SWT.NONE);
		new Label(shlFastbootToolbox, SWT.NONE);
		
		Label lblVersion = new Label(shlFastbootToolbox, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version 1.0");
		
		Button btnCheckStatus = new Button(shlFastbootToolbox, SWT.NONE);
		btnCheckStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCheckDeviceStatus();
			}
		});
		btnCheckStatus.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnCheckStatus.setText("Check Current Device Status");
		
		Label lblByDooMLoRD = new Label(shlFastbootToolbox, SWT.NONE);
		lblByDooMLoRD.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblByDooMLoRD.setText("By DooMLoRD");
		
		Button btnrRebootFBAdb = new Button(shlFastbootToolbox, SWT.NONE);
		btnrRebootFBAdb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRebootFastbootViaAdb();
			}
		});
		btnrRebootFBAdb.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnrRebootFBAdb.setText("Reboot into fastboot mode (via ADB)");
		new Label(shlFastbootToolbox, SWT.NONE);
		
		Button btnRebootFBFB = new Button(shlFastbootToolbox, SWT.NONE);
		btnRebootFBFB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doRebootBackIntoFastbootMode();
			}
			else {
				logger.info("Failed");
			}
		   }
		});
		btnRebootFBFB.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnRebootFBFB.setText("Reboot into fastboot mode (via Fastboot)");
		
		Button btnHotboot = new Button(shlFastbootToolbox, SWT.NONE);
		btnHotboot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				FileDialog dlg = new FileDialog(shlFastbootToolbox);
		        dlg.setFilterExtensions(new String[]{"*.sin","*.elf","*.img"});
		        dlg.setText("Kernel Chooser");
		        String dir = dlg.open();
		        if (dir!=null)
		        	doHotBoot(dir);
			} else {
				logger.info("Failed");
			}
		   }
		});
		btnHotboot.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnHotboot.setText("Select kernel to HotBoot");
		
		Button btnFlashSystem = new Button(shlFastbootToolbox, SWT.NONE);
		btnFlashSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				FileDialog dlg = new FileDialog(shlFastbootToolbox);
		        dlg.setFilterExtensions(new String[]{"*.sin","*.img","*.ext4","*.yaffs2"});
		        dlg.setText("System Chooser");
		        String dir = dlg.open();
		        if (dir!=null)
		        	doFlashSystem(dir);
			}
			else {
				logger.info("Failed");
			}
		   }
		});

		btnFlashSystem.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnFlashSystem.setText("Select system to Flash");
		
		Button btnFlashKernel = new Button(shlFastbootToolbox, SWT.NONE);
		btnFlashKernel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				FileDialog dlg = new FileDialog(shlFastbootToolbox);
		        dlg.setFilterExtensions(new String[]{"*.sin","*.elf","*.img"});
		        dlg.setText("Kernel Chooser");
		        String dir = dlg.open();
		        if (dir!=null)
		        	doFlashKernel(dir);
			}
			else {
				logger.info("Failed");
			}
		   }
		});
		btnFlashKernel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnFlashKernel.setText("Select kernel to Flash");
		
		Button btnGetVerInfo = new Button(shlFastbootToolbox, SWT.NONE);
		btnGetVerInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doGetFastbootVerInfo();
			} else {
				logger.info("Filed");
			}
		   }
		});
		btnGetVerInfo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnGetVerInfo.setText("Get Ver Info");
		new Label(shlFastbootToolbox, SWT.NONE);
		
		Button btnGetDeviceInfo = new Button(shlFastbootToolbox, SWT.NONE);
		btnGetDeviceInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doGetConnectedDeviceInfo();
			} else {
				logger.info("Failed");
			}
		   }
		});
		btnGetDeviceInfo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnGetDeviceInfo.setText("Get Device Info");
		new Label(shlFastbootToolbox, SWT.NONE);
		
		Button btnReboot = new Button(shlFastbootToolbox, SWT.NONE);
		btnReboot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			logger.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doFastbootReboot();
			}
			else {
				logger.info("failed");
			}
		   }
		});
		btnReboot.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnReboot.setText("Reboot device into system");
		new Label(shlFastbootToolbox, SWT.NONE);
		new Label(shlFastbootToolbox, SWT.NONE);
		new Label(shlFastbootToolbox, SWT.NONE);
		
		Button btnClose = new Button(shlFastbootToolbox, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlFastbootToolbox.dispose();
			}
		});
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnClose.setText("Close");

	}

	public void doRebootFastbootViaAdb() {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Reboot fastboot via ADB");
		job.setAction("doRebootFastbootViaAdb");
		job.schedule();
	}
	
	public void doCheckDeviceStatus(){
		FastBootToolBoxJob job = new FastBootToolBoxJob("Check Device Status");
		job.setAction("doCheckDeviceStatus");
		job.schedule();
	}

	public void doGetConnectedDeviceInfo() {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Get Device Infos");
		job.setAction("doGetConnectedDeviceInfo");
		job.schedule();
	}

	public void doGetFastbootVerInfo() {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Get Device Vers Infos");
		job.setAction("doGetFastbootVerInfo");
		job.schedule();
	}
	
	public void doRebootBackIntoFastbootMode() {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Reboot device into fastboot");
		job.setAction("doRebootBackIntoFastbootMode");
		job.schedule();
	}

	public void doFastbootReboot() {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Reboot device");
		job.setAction("doFastbootReboot");
		job.schedule();
	}

	public void doHotBoot(String kernel) {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Hotboot device");
		job.setAction("doHotbootKernel");
		job.setImage(kernel);
		job.schedule();
	}

	public void doFlashKernel(String kernel) {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Flash kernel to device");
		job.setAction("doFlashKernel");
		job.setImage(kernel);
		job.schedule();
	}

	public void doFlashSystem(String system) {
		FastBootToolBoxJob job = new FastBootToolBoxJob("Flash system to device");
		job.setAction("doFlashSystem");
		job.setImage(system);
		job.schedule();
	}

}
