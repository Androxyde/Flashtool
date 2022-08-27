package org.flashtool.gui;

import java.io.File;
import java.util.Vector;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.flashtool.gui.tools.DeviceApps;
import org.flashtool.gui.tools.WidgetTask;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class ProfileSave extends Dialog {

	protected Shell shlProfileSave;
	private Text txtProfileName;
	private Button btnsave;
	String result = null;
	DeviceApps _apps = null;

	public ProfileSave(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open(DeviceApps apps) {
		_apps=apps;
		createContents();
		shlProfileSave.open();
		shlProfileSave.layout();
		Display display = getParent().getDisplay();
		while (!shlProfileSave.isDisposed()) {
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
		shlProfileSave = new Shell(getParent(), getStyle());
		shlProfileSave.setSize(421, 123);
		shlProfileSave.setText("Profile Name");
		shlProfileSave.setLayout(new FormLayout());
		
		Button btnCancel = new Button(shlProfileSave, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result="";
				shlProfileSave.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		btnsave = new Button(shlProfileSave, SWT.NONE);
		btnsave.setEnabled(false);
		FormData fd_btnsave = new FormData();
		fd_btnsave.bottom = new FormAttachment(100,-10);
		fd_btnsave.right = new FormAttachment(btnCancel, -6);
		btnsave.setLayoutData(fd_btnsave);
		btnsave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
				if (_apps.getProfiles().contains(txtProfileName.getText().toLowerCase()))
					throw new Exception("This profile already exists.");
				if (txtProfileName.getText().toLowerCase().contains(" "))
					throw new Exception("Name cannot contain spaces.");
					_apps.saveProfile(txtProfileName.getText().toLowerCase());
					_apps.setProfile(txtProfileName.getText().toLowerCase());
					shlProfileSave.dispose();
				} catch (Exception ex) {
					WidgetTask.openOKBox(shlProfileSave, ex.getMessage());
				}
			}
		});
		btnsave.setText("Save");
		
		Composite composite = new Composite(shlProfileSave, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		
		Label lblName = new Label(composite, SWT.NONE);
		GridData gd_lblName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblName.widthHint = 92;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Profile name :");
		txtProfileName = new Text(composite, SWT.BORDER);
		txtProfileName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtProfileName.getText().length()>0) {
					btnsave.setEnabled(true);
				}
				else
					btnsave.setEnabled(false);
			}
		});
		GridData gd_txtProfileName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtProfileName.widthHint = 270;
		txtProfileName.setLayoutData(gd_txtProfileName);
	}

}
