package org.flashtool.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.flashsystem.S1Command;
import org.flashtool.gui.tools.FastBootToolBoxJob;
import org.flashtool.gui.tools.WidgetTask;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;

@Slf4j
public class FastbootToolbox extends Dialog {

	protected Object result;
	protected Shell shlFastbootToolbox;
	static final Logger logger = LogManager.getLogger(FastbootToolbox.class);

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
		shlFastbootToolbox.setSize(823, 261);
		shlFastbootToolbox.setText("Fastboot Toolbox");
		shlFastbootToolbox.setLayout(new FormLayout());
		
		Button btnClose = new Button(shlFastbootToolbox, SWT.NONE);
		FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(100, -10);
		fd_btnClose.right = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlFastbootToolbox.dispose();
			}
		});
		btnClose.setText("Close");
		
		Composite composite = new Composite(shlFastbootToolbox, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		Label lblVersion = new Label(composite, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version 1.0");
		
		Button btnCheckStatus = new Button(composite, SWT.NONE);
		GridData gd_btnCheckStatus = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnCheckStatus.widthHint = 208;
		btnCheckStatus.setLayoutData(gd_btnCheckStatus);
		btnCheckStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCheckDeviceStatus();
			}
		});
		btnCheckStatus.setText("Check Current Device Status");
		
		Label lblByDooMLoRD = new Label(composite, SWT.NONE);
		lblByDooMLoRD.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblByDooMLoRD.setText("By DooMLoRD");
		
		Button btnrRebootFBAdb = new Button(composite, SWT.NONE);
		GridData gd_btnrRebootFBAdb = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnrRebootFBAdb.widthHint = 273;
		btnrRebootFBAdb.setLayoutData(gd_btnrRebootFBAdb);
		btnrRebootFBAdb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doRebootFastbootViaAdb();
			}
		});
		btnrRebootFBAdb.setText("Reboot into fastboot mode (via ADB)");
		new Label(composite, SWT.NONE);
		
		Button btnRebootFBFB = new Button(composite, SWT.NONE);
		btnRebootFBFB.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnRebootFBFB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doRebootBackIntoFastbootMode();
			}
			else {
				log.info("Failed");
			}
		   }
		});
		btnRebootFBFB.setText("Reboot into fastboot mode (via Fastboot)");
		
		Button btnHotboot = new Button(composite, SWT.NONE);
		btnHotboot.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnHotboot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				FileDialog dlg = new FileDialog(shlFastbootToolbox);
		        dlg.setFilterExtensions(new String[]{"*.sin","*.elf","*.img"});
		        dlg.setText("Kernel Chooser");
		        String dir = dlg.open();
		        if (dir!=null)
		        	doHotBoot(dir);
			} else {
				log.info("Failed");
			}
		   }
		});
		btnHotboot.setText("Select kernel to HotBoot");
		
		Button btnFlashSystem = new Button(composite, SWT.NONE);
		btnFlashSystem.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnFlashSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
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
				log.info("Failed");
			}
		   }
		});
		btnFlashSystem.setText("Select system to Flash");
		
		Button btnFlashKernel = new Button(composite, SWT.NONE);
		btnFlashKernel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnFlashKernel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
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
				log.info("Failed");
			}
		   }
		});
		btnFlashKernel.setText("Select kernel to Flash");
		
		Button btnGetVerInfo = new Button(composite, SWT.NONE);
		btnGetVerInfo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnGetVerInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doGetFastbootVerInfo();
			} else {
				log.info("Filed");
			}
		   }
		});
		btnGetVerInfo.setText("Get Ver Info");
		new Label(composite, SWT.NONE);
		
		Button btnGetDeviceInfo = new Button(composite, SWT.NONE);
		btnGetDeviceInfo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		btnGetDeviceInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doGetConnectedDeviceInfo();
			} else {
				log.info("Failed");
			}
		   }
		});
		btnGetDeviceInfo.setText("Get Device Info");
		new Label(composite, SWT.NONE);
		
		Button btnReboot = new Button(composite, SWT.NONE);
		btnReboot.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnReboot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			log.info("Now plug your device in Fastboot Mode");
			String result = (String)WidgetTask.openWaitDeviceForFastboot(shlFastbootToolbox);
			if (result.equals("OK")) {
				doFastbootReboot();
			}
			else {
				log.info("failed");
			}
		   }
		});
		btnReboot.setText("Reboot device into system");
		new Label(composite, SWT.NONE);

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
