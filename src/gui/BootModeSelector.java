package gui;


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
		
		Composite compositeSelector = new Composite(shell, SWT.NONE);
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
		
		Button btnOK = new Button(shell, SWT.NONE);
		FormData fd_btnOK = new FormData();
		btnOK.setLayoutData(fd_btnOK);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnFlashmode.getSelection()) result="flashmode";
				else result="fastboot";
				shell.dispose();
			}
		});
		btnOK.setText("Ok");
		
		btnCancel = new Button(shell, SWT.NONE);
		fd_btnOK.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnOK.right = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shell.dispose();
			}
		});
		btnCancel.setText("Cancel");
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
		shell.setSize(272, 110);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

	}
}
