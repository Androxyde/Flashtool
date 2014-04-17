package gui;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import gui.tools.SearchFastbootJob;
import gui.tools.WidgetsTool;
import org.eclipse.swt.widgets.Label;

public class WaitDeviceForFastboot extends Dialog {

	protected Object result;
	protected Shell shlWaitForFastbootmode;
	protected SearchFastbootJob job;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaitDeviceForFastboot(Shell parent, int style) {
		super(parent, style);
		setText("Wait for Fastboot mode");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlWaitForFastbootmode);
		shlWaitForFastbootmode.open();
		shlWaitForFastbootmode.layout();
		shlWaitForFastbootmode.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
					job.stopSearch();
					result = new String("Canceled");
		      }
		    });
		Display display = getParent().getDisplay();
		job = new SearchFastbootJob("Search Fastboot Job");
		job.schedule();
		while (!shlWaitForFastbootmode.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			if (job.getState() == Status.OK) {
				result = new String("OK");
				shlWaitForFastbootmode.dispose();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlWaitForFastbootmode = new Shell(getParent(), getStyle());
		shlWaitForFastbootmode.setSize(616, 429);
		shlWaitForFastbootmode.setText("Wait for Fastboot Mode");
		
		Composite composite = new Composite(shlWaitForFastbootmode, SWT.NONE);
		composite.setBounds(10, 10, 200, 348);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 120, 180, 15);
		lblNewLabel.setText("1 - Unplug the device");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(10, 141, 180, 15);
		lblNewLabel_1.setText("2 - Power off the device");
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setBounds(10, 162, 180, 15);
		lblNewLabel_2.setText("3 - Press the menu button");
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setBounds(10, 183, 180, 15);
		lblNewLabel_3.setText("4 - Plug the USB cable");
		
		Composite composite_1 = new Composite(shlWaitForFastbootmode, SWT.NONE);
		composite_1.setBounds(216, 10, 384, 348);
		
		Button btnCancel = new Button(shlWaitForFastbootmode, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				job.stopSearch();
				result = new String("Canceled");
				shlWaitForFastbootmode.dispose();
			}
		});
		btnCancel.setBounds(532, 364, 68, 23);
		btnCancel.setText("Cancel");
	}

}
