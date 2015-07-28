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
		shlWaitForFlashmode.setSize(656, 520);
		shlWaitForFlashmode.setText("Wait for Flashmode");
		
		Composite composite = new Composite(shlWaitForFlashmode, SWT.NONE);
		composite.setBounds(10, 5, 304, 433);
		
		// 2012 Line Of Xperias Text
		Label lblNewLabel00 = new Label(composite, SWT.NONE);
		lblNewLabel00.setBounds(20, 39, 248, 15);
		lblNewLabel00.setText("2012");

		Label lblNewLabel01 = new Label(composite, SWT.NONE);
		lblNewLabel01.setBounds(20, 61, 274, 15);
		lblNewLabel01.setText("1.  Unplug the device");
		
		Label lblNewLabel02 = new Label(composite, SWT.NONE);
		lblNewLabel02.setBounds(20, 82, 274, 15);
		lblNewLabel02.setText("2.  Power off the device");
		
		Label lblNewLabel03 = new Label(composite, SWT.NONE);
		lblNewLabel03.setBounds(20, 104, 274, 15);
		lblNewLabel03.setText("3.  Press the volume DOWN button");

		Label lblNewLabel05 = new Label(composite, SWT.NONE);
		lblNewLabel05.setBounds(20, 126, 274, 15);
		lblNewLabel05.setText("4.  Plug the USB cable");
		
		
		// Xperia 2011 Lines of Xperia Text
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(20, 260, 274, 15);
		lblNewLabel.setText("2011");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(20, 282, 274, 15);
		lblNewLabel_1.setText("1.  Unplug the device");
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setBounds(20, 304, 274, 15);
		lblNewLabel_2.setText("2.  Power off the device");
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setBounds(20, 326, 274, 15);
		lblNewLabel_3.setText("3.  Press the BACK button-");
		
		Label lblNewLabel_4 = new Label(composite, SWT.NONE);
		lblNewLabel_4.setBounds(20, 348, 274, 23);
		lblNewLabel_4.setText("-Volume down for Xperia Ray");
		
		Label lblNewLabel_5 = new Label(composite, SWT.NONE);
		lblNewLabel_5.setBounds(20, 369, 274, 23);
		lblNewLabel_5.setText("4.  Plug the USB cable");
		
		Composite composite_1 = new Composite(shlWaitForFlashmode, SWT.NONE);
		composite_1.setBounds(320, 0, 364, 221);
		
		final GifCLabel lbl = new GifCLabel(composite_1, SWT.CENTER);
		lbl.setText("");
		lbl.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/flashmode2012.gif"));
		lbl.setBounds(10, -44, 350, 339);

		Composite composite_2 = new Composite(shlWaitForFlashmode, SWT.NONE);
		composite_2.setBounds(320, 167, 364, 271);
		
		final GifCLabel lbl2 = new GifCLabel(composite_2, SWT.CENTER);
		lbl2.setText("");
		lbl2.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/flashmode2011.gif"));
		lbl2.setBounds(8, 5, 350, 339);
		
		
		Button btnCancel = new Button(shlWaitForFlashmode, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				job.stopSearch();
				result = new String("Canceled");
				shlWaitForFlashmode.dispose();
			}
		});
		btnCancel.setBounds(568, 450, 68, 26);
		btnCancel.setText("Cancel");
	}
}
