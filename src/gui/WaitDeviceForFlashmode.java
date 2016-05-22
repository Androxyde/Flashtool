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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridLayout;

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
		shlWaitForFlashmode.setSize(580, 580);
		shlWaitForFlashmode.setText("Wait for Flashmode");
		shlWaitForFlashmode.setLayout(new FormLayout());
		
		Composite composite = new Composite(shlWaitForFlashmode, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.top = new FormAttachment(0, 74);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(1, false));
		
		// 2012 Line Of Xperias Text
		Label lblNewLabel00 = new Label(composite, SWT.NONE);
		lblNewLabel00.setText("2012");

		Label lblNewLabel01 = new Label(composite, SWT.NONE);
		lblNewLabel01.setText("1.  Unplug the device");
		
		Label lblNewLabel02 = new Label(composite, SWT.NONE);
		lblNewLabel02.setText("2.  Power off the device");
		
		Label lblNewLabel03 = new Label(composite, SWT.NONE);
		lblNewLabel03.setText("3.  Press the volume DOWN button");

		Label lblNewLabel05 = new Label(composite, SWT.NONE);
		lblNewLabel05.setText("4.  Plug the USB cable");
		
		Composite composite2012 = new Composite(shlWaitForFlashmode, SWT.NONE);
		fd_composite.right = new FormAttachment(composite2012, -23);
		FormData fd_composite2012 = new FormData();
		fd_composite2012.top = new FormAttachment(0, 10);
		fd_composite2012.left = new FormAttachment(0, 271);
		composite2012.setLayoutData(fd_composite2012);
		
		final GifCLabel lbl = new GifCLabel(composite2012, SWT.CENTER);
		lbl.setText("");
		lbl.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/flashmode2012.gif"));
		lbl.setBounds(22, 0, 271, 239);

		Composite composite2011 = new Composite(shlWaitForFlashmode, SWT.NONE);
		fd_composite2012.bottom = new FormAttachment(composite2011, -7);
		FormData fd_composite2011 = new FormData();
		fd_composite2011.left = new FormAttachment(composite2012, 0, SWT.LEFT);
		fd_composite2011.right = new FormAttachment(100, -10);
		fd_composite2011.top = new FormAttachment(0, 256);
		composite2011.setLayoutData(fd_composite2011);
		
		final GifCLabel lbl2 = new GifCLabel(composite2011, SWT.CENTER);
		lbl2.setText("");
		lbl2.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/flashmode2011.gif"));
		lbl2.setBounds(23, 0, 270, 254);
		
		
		Button btnCancel = new Button(shlWaitForFlashmode, SWT.NONE);
		fd_composite2011.bottom = new FormAttachment(btnCancel, -6);
		fd_composite2012.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				job.stopSearch();
				result = new String("Canceled");
				shlWaitForFlashmode.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		Composite composite_3 = new Composite(shlWaitForFlashmode, SWT.NONE);
		FormData fd_composite_3 = new FormData();
		fd_composite_3.bottom = new FormAttachment(100, -108);
		fd_composite_3.right = new FormAttachment(composite, 0, SWT.RIGHT);
		fd_composite_3.left = new FormAttachment(0, 10);
		composite_3.setLayoutData(fd_composite_3);
		composite_3.setLayout(new GridLayout(1, false));
		
		
		// Xperia 2011 Lines of Xperia Text
		Label lblNewLabel = new Label(composite_3, SWT.NONE);
		lblNewLabel.setText("2011");
		
		Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
		lblNewLabel_1.setText("1.  Unplug the device");
		
		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setText("2.  Power off the device");
		
		Label lblNewLabel_3 = new Label(composite_3, SWT.NONE);
		lblNewLabel_3.setText("3.  Press the BACK button-");
		
		Label lblNewLabel_4 = new Label(composite_3, SWT.NONE);
		lblNewLabel_4.setText("-Volume down for Xperia Ray");
		
		Label lblNewLabel_5 = new Label(composite_3, SWT.NONE);
		lblNewLabel_5.setText("4.  Plug the USB cable");

	}
}
