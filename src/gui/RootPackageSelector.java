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

public class RootPackageSelector extends Dialog {

	protected Object result;
	protected Shell shell;
	Button btnSuperuser;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RootPackageSelector(Shell parent, int style) {
		super(parent, style);
		setText("Root Package chooser");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
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
		shell.setSize(243, 137);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Button btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shell.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		Button btnOK = new Button(shell, SWT.NONE);
		FormData fd_btnOK = new FormData();
		fd_btnOK.right = new FormAttachment(btnCancel, -10);
		fd_btnOK.bottom = new FormAttachment(100, -10);
		btnOK.setLayoutData(fd_btnOK);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnSuperuser.getSelection()) result="Superuser";
				else result="Supersu";
				shell.dispose();
			}
		});
		btnOK.setText("Ok");
		
		Composite composite = new Composite(shell, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.top = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new GridLayout(1, false));
		
		btnSuperuser = new Button(composite, SWT.RADIO);
		btnSuperuser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnSuperuser.setText("Superuser");
		btnSuperuser.setSelection(true);
		
		Button btnSuperSU = new Button(composite, SWT.RADIO);
		btnSuperSU.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnSuperSU.setText("SuperSU");

	}
}
