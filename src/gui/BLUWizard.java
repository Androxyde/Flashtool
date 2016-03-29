package gui;

import flashsystem.X10flash;
import gui.tools.BLUnlockJob;
import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;
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

public class BLUWizard extends Dialog {

	protected Object result;
	protected Shell shlBootloaderUnlockWizard;
	private Text textIMEI;
	private Text textULCODE;
	private Button btnGetUnlock;
	private Button btnUnlock;
	private X10flash _flash;
	private String _action;
	private String _serial;
	static final Logger logger = LogManager.getLogger(BLUWizard.class);

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BLUWizard(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String serial, String imei, String ulcode,X10flash flash, String action) {
		_action = action;
		_flash = flash;
		_serial = serial;
		createContents();
		textIMEI.setText(imei);
		textULCODE.setText(ulcode);
		if (ulcode.length()>0) {
			btnUnlock.setEnabled(true);
			if (_action.equals("R")) {
				btnUnlock.setText("Relock");
			}
			btnGetUnlock.setEnabled(false);
			textULCODE.setEditable(false);
		}
		WidgetsTool.setSize(shlBootloaderUnlockWizard);
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
		shlBootloaderUnlockWizard.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  result = "";
		    	  event.doit = true;
		      }
		    });
		shlBootloaderUnlockWizard.setSize(350, 220);
		if (_action.equals("R"))
			shlBootloaderUnlockWizard.setText("BootLoader Relock Wizard");
		else
			shlBootloaderUnlockWizard.setText("BootLoader Unlock Wizard");
		
		Label lblImei = new Label(shlBootloaderUnlockWizard, SWT.NONE);
		lblImei.setBounds(10, 10, 55, 15);
		lblImei.setText("IMEI : ");
		
		textIMEI = new Text(shlBootloaderUnlockWizard, SWT.BORDER);
		textIMEI.setEditable(false);
		textIMEI.setBounds(106, 7, 164, 21);
		
		btnGetUnlock = new Button(shlBootloaderUnlockWizard, SWT.NONE);
		btnGetUnlock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://unlockbootloader.sonymobile.com/");
			}
		});
		btnGetUnlock.setBounds(127, 34, 118, 25);
		btnGetUnlock.setText("Get Unlock Code");
		
		Label lblUnlockCode = new Label(shlBootloaderUnlockWizard, SWT.NONE);
		lblUnlockCode.setBounds(10, 68, 85, 15);
		lblUnlockCode.setText("Unlock Code :");
		
		textULCODE = new Text(shlBootloaderUnlockWizard, SWT.BORDER);
		textULCODE.setBounds(106, 65, 164, 21);
		
		btnUnlock = new Button(shlBootloaderUnlockWizard, SWT.NONE);
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
		btnUnlock.setBounds(144, 92, 75, 25);
		btnUnlock.setText("Unlock");
		
		Button btnNewButton_2 = new Button(shlBootloaderUnlockWizard, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlBootloaderUnlockWizard.dispose();
			}
		});
		btnNewButton_2.setBounds(195, 123, 75, 25);
		btnNewButton_2.setText("Close");

	}

	public void showErrorMessageBox(String message) {
		MessageBox mb = new MessageBox(shlBootloaderUnlockWizard,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Errorr");
		mb.setMessage(message);
		int result = mb.open();
	}

}
