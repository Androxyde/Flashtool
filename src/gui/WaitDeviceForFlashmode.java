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
import flashsystem.X10flash;
import gui.tools.SearchJob;
import gui.tools.WidgetsTool;
import org.eclipse.swt.widgets.Label;

public class WaitDeviceForFlashmode extends Dialog {

	protected Object result = new String("OK");
	protected Shell shlWaitForFlashmode;
	protected SearchJob job;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public WaitDeviceForFlashmode(Shell parent, int style) {
		super(parent, style);
		setText("Wait for Flashmode");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(final X10flash flash) {
		createContents();
		WidgetsTool.setSize(shlWaitForFlashmode);
		shlWaitForFlashmode.open();
		shlWaitForFlashmode.layout();
		shlWaitForFlashmode.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
					job.stopSearch();
					result = new String("Canceled");
		      }
		    });
		Display display = getParent().getDisplay();
		job = new SearchJob("Search Job");
		job.setFlash(flash);
		job.schedule();
		while (!shlWaitForFlashmode.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			if (job.getState() == Status.OK) {
				shlWaitForFlashmode.dispose();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlWaitForFlashmode = new Shell(getParent(), getStyle());
		shlWaitForFlashmode.setSize(616, 425);
		shlWaitForFlashmode.setText("Wait for Flashmode");
		
		Composite composite = new Composite(shlWaitForFlashmode, SWT.NONE);
		composite.setBounds(10, 10, 200, 348);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 122, 180, 15);
		lblNewLabel.setText("1 - Unplug the device");
		
		Label lblPower = new Label(composite, SWT.NONE);
		lblPower.setBounds(10, 143, 180, 15);
		lblPower.setText("2 - Power off the device");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(10, 164, 180, 15);
		lblNewLabel_1.setText("3 - Press the back button");
		
		Label lblPlug = new Label(composite, SWT.NONE);
		lblPlug.setBounds(10, 185, 180, 15);
		lblPlug.setText("4 - Plug the USB cable");
		
		Composite composite_1 = new Composite(shlWaitForFlashmode, SWT.NONE);
		composite_1.setBounds(216, 10, 384, 348);
		
		final GifCLabel lbl = new GifCLabel(composite_1, SWT.CENTER);
		lbl.setText("");
		lbl.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/flashmode.gif"));
		lbl.setBounds(10, 35, 350, 339);
		
		Button btnCancel = new Button(shlWaitForFlashmode, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				job.stopSearch();
				result = new String("Canceled");
				shlWaitForFlashmode.dispose();
			}
		});
		btnCancel.setBounds(538, 364, 68, 28);
		btnCancel.setText("Cancel");
	}
}
