package gui;

import gui.tools.WidgetsTool;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class BootModeSelector extends Dialog {

	protected Object result;
	protected Shell shell;
	Button btnFlashmode;
	private Button btnFastboot;
	private Button btnCancel;
	private Composite composite;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BootModeSelector(Shell parent, int style) {
		super(parent, style);
		setText("Bootmode chooser");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shell);
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 87);
		composite.setLayoutData(fd_composite);
		
		Composite compositeSelector = new Composite(shell, SWT.NONE);
		fd_composite.top = new FormAttachment(0, 75);
		
		Button btnOK = new Button(composite, SWT.NONE);
		GridData gd_btnOK = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnOK.widthHint = 52;
		btnOK.setLayoutData(gd_btnOK);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnFlashmode.getSelection()) result="flashmode";
				else result="fastboot";
				shell.dispose();
			}
		});
		btnOK.setText("Ok");
		new Label(composite, SWT.NONE);
		
		btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnCancel.widthHint = 58;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shell.dispose();
			}
		});
		btnCancel.setText("Cancel");
		FormData fd_compositeSelector = new FormData();
		fd_compositeSelector.top = new FormAttachment(0, 10);
		fd_compositeSelector.left = new FormAttachment(0, 10);
		compositeSelector.setLayoutData(fd_compositeSelector);
		compositeSelector.setLayout(new GridLayout(1, false));
		
		btnFlashmode = new Button(compositeSelector, SWT.RADIO);
		btnFlashmode.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnFlashmode.setText("Flashmode");
		btnFlashmode.setSelection(true);
		
		btnFastboot = new Button(compositeSelector, SWT.RADIO);
		btnFastboot.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnFastboot.setText("Fastboot mode");
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
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
		shell = new Shell(getParent(), getStyle());
		shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  result = "";
		    	  event.doit = true;
		      }
		    });
		shell.setSize(249, 148);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

	}
}
