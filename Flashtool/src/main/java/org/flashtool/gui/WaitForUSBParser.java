package org.flashtool.gui;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.flashtool.gui.tools.DevicesSyncJob;
import org.flashtool.gui.tools.USBParseJob;
import org.flashtool.gui.tools.WidgetTask;
import org.flashtool.parsers.simpleusblog.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitForUSBParser extends Dialog {

	protected Shell shlWaiForDevicesSync;
	protected boolean canClose = false;
	protected Dialog mydial;
	protected Session sess;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaitForUSBParser(Shell parent, int style) {
		super(parent, style);
		setText("Parsing USB log");
		mydial = this;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String file, String folder) {
		createContents(file,folder);
		
		Label lblNewLabel = new Label(shlWaiForDevicesSync, SWT.NONE);
		lblNewLabel.setBounds(10, 32, 323, 20);
		lblNewLabel.setText("Please wait until the end of process");
		shlWaiForDevicesSync.open();
		shlWaiForDevicesSync.layout();
		Display display = getParent().getDisplay();
		while (!shlWaiForDevicesSync.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return sess;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(String file,String folder) {
		shlWaiForDevicesSync = new Shell(getParent(), getStyle());
		shlWaiForDevicesSync.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  if (canClose) {
		    		  sess = null;
		    	  	event.doit = true;
		    	  }
		    	  else {
		    		  WidgetTask.openOKBox(shlWaiForDevicesSync, "Wait for end of process");
		    		  event.doit = false;
		    	  }
		      }
		    });
		shlWaiForDevicesSync.setSize(365, 130);
		shlWaiForDevicesSync.setText("Parsing USB log");
		USBParseJob pj = new USBParseJob("USB log parser");
		pj.setFilename(file);
		pj.setSinDir(folder);

		pj.addJobChangeListener(new IJobChangeListener() {
			public void aboutToRun(IJobChangeEvent event) {
			}

			public void awake(IJobChangeEvent event) {
			}

			public void done(IJobChangeEvent event) {
				sess = pj.getSession();
				canClose=true;
				Display.getDefault().asyncExec(
						new Runnable() {
							public void run() {
								shlWaiForDevicesSync.dispose();
							}
						}
				);				
				
			}

			public void running(IJobChangeEvent event) {
			}

			public void scheduled(IJobChangeEvent event) {
			}

			public void sleeping(IJobChangeEvent event) {
			}
		});
		pj.schedule();

	}
}
