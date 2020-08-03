package gui;

import gui.tools.WidgetTask;
import gui.tools.XperiFirmJob;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class WaitForXperiFirm extends Dialog {

	protected Object result;
	protected Shell shlWaiForXperiFirm;
	protected boolean canClose = false;
	protected Dialog mydial;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaitForXperiFirm(Shell parent, int style) {
		super(parent, style);
		setText("XperiFirm running");
		mydial = this;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		Label lblNewLabel = new Label(shlWaiForXperiFirm, SWT.NONE);
		lblNewLabel.setBounds(10, 32, 323, 18);
		lblNewLabel.setText("Please wait until the end of process");
		shlWaiForXperiFirm.open();
		shlWaiForXperiFirm.layout();
		Display display = getParent().getDisplay();
		while (!shlWaiForXperiFirm.isDisposed()) {
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
		shlWaiForXperiFirm = new Shell(getParent(), getStyle());
		shlWaiForXperiFirm.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  if (canClose) {
		    		  result = "";
		    	  	event.doit = true;
		    	  }
		    	  else {
		    		  WidgetTask.openOKBox(shlWaiForXperiFirm, "Wait for end of process");
		    		  event.doit = false;
		    	  }
		      }
		    });
		shlWaiForXperiFirm.setSize(365, 130);
		shlWaiForXperiFirm.setText("Running XperiFirm");
		XperiFirmJob xj = new XperiFirmJob("XperiFirm");
		xj.setShell(shlWaiForXperiFirm);
		xj.addJobChangeListener(new IJobChangeListener() {
			public void aboutToRun(IJobChangeEvent event) {
			}

			public void awake(IJobChangeEvent event) {
			}

			public void done(IJobChangeEvent event) {
				canClose=true;
				Display.getDefault().asyncExec(
						new Runnable() {
							public void run() {
								shlWaiForXperiFirm.dispose();
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
		xj.schedule();

	}
}
