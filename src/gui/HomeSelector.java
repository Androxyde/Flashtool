package gui;

import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.system.GlobalConfig;
import org.system.OS;

public class HomeSelector extends Dialog {

	protected Object result;
	protected Shell shlHomeSelector;
	private Text sourceFolder;
	private static Logger logger = Logger.getLogger(HomeSelector.class);
	private Button btnAccept;
	private boolean cancelable = true;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public HomeSelector(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(boolean pcancelable) {
		cancelable = pcancelable;
		createContents();
		WidgetsTool.setSize(shlHomeSelector);
		
		Button btnCancel = new Button(shlHomeSelector, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cancelable) {
					result = "";
					shlHomeSelector.dispose();
				}
		    	else {
		    		WidgetTask.openOKBox(shlHomeSelector, "You must choose a user home folder");
		    	}
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(btnAccept, 0, SWT.BOTTOM);
		fd_btnCancel.right = new FormAttachment(btnAccept, -6);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		shlHomeSelector.open();
		shlHomeSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlHomeSelector.isDisposed()) {
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
		shlHomeSelector = new Shell(getParent(), getStyle());
		shlHomeSelector.setSize(538, 128);
		shlHomeSelector.setText("User Home Selector");
		shlHomeSelector.setLayout(new FormLayout());
		shlHomeSelector.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  if (cancelable) {
		    		  result = "";
		    	  	  event.doit = true;
		    	  }
		    	  else {
		    		  WidgetTask.openOKBox(shlHomeSelector, "You must choose a user home folder");
		    		  event.doit = false;
		    	  }
		      }
		    });		
		Composite composite = new Composite(shlHomeSelector, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -9);
		composite.setLayoutData(fd_composite);
		
		Label lblHomeFolder = new Label(composite, SWT.NONE);
		GridData gd_lblHomeFolder = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblHomeFolder.widthHint = 62;
		lblHomeFolder.setLayoutData(gd_lblHomeFolder);
		lblHomeFolder.setText("Folder :");
		
		sourceFolder = new Text(composite, SWT.BORDER);
		sourceFolder.setEditable(false);
		sourceFolder.setText(GlobalConfig.getProperty("user.flashtool"));
		GridData gd_sourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_sourceFolder.widthHint = 385;
		sourceFolder.setLayoutData(gd_sourceFolder);
		
		Button btnFolderChoose = new Button(composite, SWT.NONE);
		btnFolderChoose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlHomeSelector);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(sourceFolder.getText());

		        // Change the title bar text
		        dlg.setText("Home User Folder Chooser");
		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	if (!sourceFolder.getText().equals(dir)) {
		        		try {
		        			sourceFolder.setText(dir);
		        		}
		        		catch (Exception ex) {
		        			ex.printStackTrace();
		        		}
		        	}
		        }
			}
		});
		GridData gd_btnFolderChoose = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnFolderChoose.widthHint = 34;
		btnFolderChoose.setLayoutData(gd_btnFolderChoose);
		btnFolderChoose.setText("...");
		btnFolderChoose.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		
		btnAccept = new Button(shlHomeSelector, SWT.NONE);
		btnAccept.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=sourceFolder.getText();
				if (!((String)result).startsWith(OS.getWorkDir()+File.separator))
					shlHomeSelector.dispose();
				else
					WidgetTask.openOKBox(shlHomeSelector, "User home folder must be out of Flashtool application folder");
			}
		});
		FormData fd_btnAccept = new FormData();
		fd_btnAccept.bottom = new FormAttachment(100, -10);
		fd_btnAccept.right = new FormAttachment(100, -10);
		btnAccept.setLayoutData(fd_btnAccept);
		btnAccept.setText("Accept");

	}
}