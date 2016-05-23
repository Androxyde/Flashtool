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
import gui.tools.WidgetTask;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridData;

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
		shlWaitForFastbootmode.setSize(500, 600);
		shlWaitForFastbootmode.setText("Wait for Fastboot Mode");
		shlWaitForFastbootmode.setLayout(new FormLayout());
		
		Composite composite2012 = new Composite(shlWaitForFastbootmode, SWT.NONE);
		FormData fd_composite2012 = new FormData();
		fd_composite2012.left = new FormAttachment(0, 10);
		fd_composite2012.top = new FormAttachment(0, 77);
		fd_composite2012.bottom = new FormAttachment(0, 202);
		composite2012.setLayoutData(fd_composite2012);
		composite2012.setLayout(new GridLayout(1, false));
		
		// 2012 Line Of Xperias Text
		Label lblNewLabel00 = new Label(composite2012, SWT.NONE);
		lblNewLabel00.setText("2012");

		Label lblNewLabel01 = new Label(composite2012, SWT.NONE);
		lblNewLabel01.setText("1.  Unplug the device");
		
		Label lblNewLabel02 = new Label(composite2012, SWT.NONE);
		lblNewLabel02.setText("2.  Power off the device");
		
		Label lblNewLabel03 = new Label(composite2012, SWT.NONE);
		lblNewLabel03.setText("3.  Press the volume UP button");

		Label lblNewLabel05 = new Label(composite2012, SWT.NONE);
		lblNewLabel05.setText("4.  Plug the USB cable");
		
		Label lblNewLabel_4 = new Label(composite2012, SWT.NONE);
		lblNewLabel_4.setText("-Volume UP for Xperia Ray");
		
		Composite composite2012GIF = new Composite(shlWaitForFastbootmode, SWT.NONE);
		fd_composite2012.right = new FormAttachment(composite2012GIF, -6);
		FormData fd_composite2012GIF = new FormData();
		fd_composite2012GIF.left = new FormAttachment(0, 206);
		fd_composite2012GIF.right = new FormAttachment(100, -10);
		fd_composite2012GIF.top = new FormAttachment(0, 10);
		composite2012GIF.setLayoutData(fd_composite2012GIF);
		composite2012GIF.setLayout(new FormLayout());
		
		final GifCLabel lbl = new GifCLabel(composite2012GIF, SWT.CENTER);
		FormData fd_lbl = new FormData();
		fd_lbl.top = new FormAttachment(0);
		fd_lbl.left = new FormAttachment(0);
		fd_lbl.right = new FormAttachment(100);
		fd_lbl.bottom = new FormAttachment(0, 240);
		lbl.setLayoutData(fd_lbl);
		lbl.setText("");
		lbl.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/fastbootmode2012.gif"));
		lbl.setLayout(new FormLayout());

		Composite composite2011GIF = new Composite(shlWaitForFastbootmode, SWT.NONE);
		fd_composite2012GIF.bottom = new FormAttachment(100, -321);
		FormData fd_composite2011GIF = new FormData();
		fd_composite2011GIF.right = new FormAttachment(100, -10);
		fd_composite2011GIF.top = new FormAttachment(composite2012GIF, 6);
		composite2011GIF.setLayoutData(fd_composite2011GIF);
		composite2011GIF.setLayout(new FormLayout());
		
		final GifCLabel lbl2 = new GifCLabel(composite2011GIF, SWT.CENTER);
		FormData fd_lbl2 = new FormData();
		fd_lbl2.top = new FormAttachment(0);
		fd_lbl2.left = new FormAttachment(0);
		fd_lbl2.right = new FormAttachment(100);
		fd_lbl2.bottom = new FormAttachment(0, 271);
		lbl2.setLayoutData(fd_lbl2);
		lbl2.setText("");
		lbl2.setGifImage(this.getClass().getResourceAsStream("/gui/ressources/fastbootmode2011.gif"));
		lbl2.setLayout(new FormLayout());
		
		
		Button btnCancel = new Button(shlWaitForFastbootmode, SWT.NONE);
		fd_composite2011GIF.bottom = new FormAttachment(btnCancel, -9);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100,-10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				job.stopSearch();
				result = new String("Canceled");
				shlWaitForFastbootmode.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		Composite composite2011 = new Composite(shlWaitForFastbootmode, SWT.NONE);
		fd_composite2011GIF.left = new FormAttachment(0, 206);
		FormData fd_composite2011 = new FormData();
		fd_composite2011.bottom = new FormAttachment(100, -141);
		fd_composite2011.top = new FormAttachment(composite2012, 123);
		fd_composite2011.right = new FormAttachment(composite2011GIF, -6);
		fd_composite2011.left = new FormAttachment(0, 10);
		composite2011.setLayoutData(fd_composite2011);
		composite2011.setLayout(new GridLayout(1, false));
		
		
		// Xperia 2011 Lines of Xperia Text
		Label lblNewLabel = new Label(composite2011, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 186;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("2011");
		
		Label lblNewLabel_1 = new Label(composite2011, SWT.NONE);
		lblNewLabel_1.setText("1.  Unplug the device");
		
		Label lblNewLabel_2 = new Label(composite2011, SWT.NONE);
		lblNewLabel_2.setText("2.  Power off the device");
		
		Label lblNewLabel_3 = new Label(composite2011, SWT.NONE);
		lblNewLabel_3.setText("3.  Press the menu button-");
		
		Label lblNewLabel_5 = new Label(composite2011, SWT.NONE);
		lblNewLabel_5.setText("4.  Plug the USB cable");
	}

}
