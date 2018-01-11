package gui;

import gui.tools.BLUnlockJob;
import gui.tools.WidgetTask;
import gui.tools.WriteTAJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.system.ULCodeFile;
import org.ta.parsers.TAUnit;

import flashsystem.Flasher;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class BLUWizard extends Dialog {

	protected Object result;
	protected Shell shlBootloaderUnlockWizard;
	private Text textIMEI;
	private Text textULCODE;
	private Button btnGetUnlock;
	private Button btnUnlock;
	private Flasher _flash;
	private String _action;
	private String _serial;
	static final Logger logger = LogManager.getLogger(BLUWizard.class);
	private Composite composite;
	private Label lblUnlockCode;
	private Label lblImei;
	private Button btnCancel;
	private Composite composite_1;
	private FormData fd_btnUnlock;
	private FormData fd_btnGetUnlock;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BLUWizard(Shell parent, int style) {
		super(parent, style);
		setText("Device bootloader unlock");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String serial, String imei, String ulcode,Flasher flash, String action) {
		_action = action;
		_flash = flash;
		_serial = serial;
		createContents();
		if (ulcode.length()>0) {
			btnUnlock.setEnabled(true);
			if (_action.equals("R")) {
				btnUnlock.setText("Relock");
			}
			btnGetUnlock.setEnabled(false);
			textULCODE.setEditable(false);
		}
		textIMEI.setText(imei);
		textULCODE.setText(ulcode);
		
		shlBootloaderUnlockWizard.open();
		shlBootloaderUnlockWizard.layout();
		Display display = getParent().getDisplay();
		while (!shlBootloaderUnlockWizard.isDisposed()) {
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
		shlBootloaderUnlockWizard = new Shell(getParent(), getStyle());
		shlBootloaderUnlockWizard.setText("Device bootloader unlock");
		shlBootloaderUnlockWizard.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  result = "";
		    	  event.doit = true;
		      }
		    });
		shlBootloaderUnlockWizard.setSize(326, 162);
		if (_action.equals("R"))
			shlBootloaderUnlockWizard.setText("BootLoader Relock Wizard");
		else
			shlBootloaderUnlockWizard.setText("BootLoader Unlock Wizard");
		shlBootloaderUnlockWizard.setLayout(new FormLayout());
		
		btnGetUnlock = new Button(shlBootloaderUnlockWizard, SWT.NONE);
		fd_btnGetUnlock = new FormData();
		fd_btnGetUnlock.left = new FormAttachment(0, 10);
		fd_btnGetUnlock.bottom = new FormAttachment(100, -10);
		btnGetUnlock.setLayoutData(fd_btnGetUnlock);
		btnGetUnlock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://unlockbootloader.sonymobile.com/");
			}
		});
		btnGetUnlock.setText("Get Unlock Code");
		
		btnUnlock = new Button(shlBootloaderUnlockWizard, SWT.NONE);
		fd_btnUnlock = new FormData();
		fd_btnUnlock.top = new FormAttachment(btnGetUnlock, 0, SWT.TOP);
		fd_btnUnlock.left = new FormAttachment(btnGetUnlock, 2);
		fd_btnUnlock.right = new FormAttachment(100, -128);
		btnUnlock.setLayoutData(fd_btnUnlock);
		btnUnlock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (textULCODE.getText().length()==0) {
					showErrorMessageBox("Your must enter an unlock code");
					return;
				}
				if (_flash==null) {
					BLUnlockJob bj = new BLUnlockJob("Unlock Job");
					final String ulcode = textULCODE.getText();
					bj.setULCode(ulcode);
					bj.addJobChangeListener(new IJobChangeListener() {
						public void aboutToRun(IJobChangeEvent event) {}
						public void awake(IJobChangeEvent event) {}
						public void running(IJobChangeEvent event) {}
						public void scheduled(IJobChangeEvent event) {}
						public void sleeping(IJobChangeEvent event) {}

						public void done(IJobChangeEvent event) {
							BLUnlockJob res = (BLUnlockJob) event.getJob();
							WidgetTask.setEnabled(btnUnlock,!res.unlockSuccess());
							if (res.unlockSuccess()) {
								try {
									ULCodeFile uc = new ULCodeFile(_serial);
									uc.setCode(ulcode);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

					});
					bj.schedule();
					btnUnlock.setEnabled(false);
				}
				else {
					if (_action.equals("R")) {
						TAUnit ta = new TAUnit(2226, null);
						logger.info("Relocking device");
						WriteTAJob tj = new WriteTAJob("Write TA");
						tj.addJobChangeListener(new IJobChangeListener() {
							public void aboutToRun(IJobChangeEvent event) {}
							public void awake(IJobChangeEvent event) {}
							public void running(IJobChangeEvent event) {}
							public void scheduled(IJobChangeEvent event) {}
							public void sleeping(IJobChangeEvent event) {}
							public void done(IJobChangeEvent event) {
								logger.info("Relock finished");
								WriteTAJob res = (WriteTAJob) event.getJob();
								WidgetTask.setEnabled(btnUnlock,!res.writeSuccess());
							}
						});
						tj.setFlash(_flash);
						tj.setTA(ta);
						tj.schedule();
					}
					else {
						TAUnit ta = new TAUnit(2226, textULCODE.getText().getBytes());
						logger.info("Unlocking device");
						WriteTAJob tj = new WriteTAJob("Write TA");
						tj.addJobChangeListener(new IJobChangeListener() {
							public void aboutToRun(IJobChangeEvent event) {}
							public void awake(IJobChangeEvent event) {}
							public void running(IJobChangeEvent event) {}
							public void scheduled(IJobChangeEvent event) {}
							public void sleeping(IJobChangeEvent event) {}
							public void done(IJobChangeEvent event) {
								logger.info("Unlock finished");
								WriteTAJob res = (WriteTAJob) event.getJob();
								WidgetTask.setEnabled(btnUnlock,!res.writeSuccess());
							}
						});
						tj.setFlash(_flash);
						tj.setTA(ta);
						tj.schedule();
					}
				}
			}
		});
		btnUnlock.setText("Unlock");
		
		btnCancel = new Button(shlBootloaderUnlockWizard, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlBootloaderUnlockWizard.dispose();
			}
		});
		btnCancel.setText("Cancel");
		composite = new Composite(shlBootloaderUnlockWizard, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		lblImei = new Label(composite, SWT.NONE);
		GridData gd_lblImei = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblImei.widthHint = 85;
		lblImei.setLayoutData(gd_lblImei);
		lblImei.setText("IMEI : ");

		textIMEI = new Text(composite, SWT.BORDER);
		textIMEI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textIMEI.setEditable(false);

		
		composite_1 = new Composite(shlBootloaderUnlockWizard, SWT.NONE);
		fd_composite.left = new FormAttachment(composite_1, 0, SWT.LEFT);
		composite_1.setLayout(new GridLayout(2, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.top = new FormAttachment(composite, 6);
		fd_composite_1.left = new FormAttachment(btnGetUnlock, 0, SWT.LEFT);
		fd_composite_1.right = new FormAttachment(100, -10);
		composite_1.setLayoutData(fd_composite_1);
		
		lblUnlockCode = new Label(composite_1, SWT.NONE);
		GridData gd_lblUnlockCode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblUnlockCode.widthHint = 85;
		lblUnlockCode.setLayoutData(gd_lblUnlockCode);
		lblUnlockCode.setText("Unlock Code :");
		
		textULCODE = new Text(composite_1, SWT.BORDER);
		textULCODE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	public void showErrorMessageBox(String message) {
		MessageBox mb = new MessageBox(shlBootloaderUnlockWizard,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Errorr");
		mb.setMessage(message);
		int result = mb.open();
	}

}
