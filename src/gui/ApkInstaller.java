package gui;

import gui.tools.WidgetsTool;

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

public class ApkInstaller extends Dialog {

	protected Shell shlApkInstaller;
	private Text txtSourceFolder;
	private Button btnInstall;
	ListViewer listViewerApk;
	Vector files = new Vector();
	String result = null;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ApkInstaller(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		WidgetsTool.setSize(shlApkInstaller);
		shlApkInstaller.open();
		shlApkInstaller.layout();
		Display display = getParent().getDisplay();
		while (!shlApkInstaller.isDisposed()) {
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
		shlApkInstaller = new Shell(getParent(), getStyle());
		shlApkInstaller.setSize(539, 312);
		shlApkInstaller.setText("Apk Installer");
		shlApkInstaller.setLayout(new FormLayout());
		
		listViewerApk = new ListViewer(shlApkInstaller, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List listApk = listViewerApk.getList();
		listApk.addSelectionListener(new SelectionListener () {
			  @Override
			  public void widgetSelected(SelectionEvent e) {
			    List l = (List) e.widget;
			    l.deselectAll();
			  }
			  @Override
			  public void widgetDefaultSelected(SelectionEvent e) {}
			});
		FormData fd_listApk = new FormData();
		
		fd_listApk.left = new FormAttachment(0, 10);
		fd_listApk.right = new FormAttachment(100, -10);
		listApk.setLayoutData(fd_listApk);

	    listViewerApk.setContentProvider(new IStructuredContentProvider() {
	        public Object[] getElements(Object inputElement) {
	          Vector v = (Vector)inputElement;
	          return v.toArray();
	        }
	        
	        public void dispose() {
	        }
	   
	        public void inputChanged(
	          Viewer viewer,
	          Object oldInput,
	          Object newInput) {
	        }
	      });
	    listViewerApk.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((File)element).getName();
	        }
	      });

		Label lblAvailableFiles = new Label(shlApkInstaller, SWT.NONE);
		FormData fd_lblAvailableFiles = new FormData();
		
		fd_lblAvailableFiles.left = new FormAttachment(0, 10);
		lblAvailableFiles.setLayoutData(fd_lblAvailableFiles);
		lblAvailableFiles.setText("Available files :");
		
		Button btnCancel = new Button(shlApkInstaller, SWT.NONE);
		
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result="";
				shlApkInstaller.dispose();
			}
		});
		btnCancel.setText("Cancel");
		fd_listApk.right = new FormAttachment(100, -10);
		fd_listApk.top = new FormAttachment(lblAvailableFiles, 6);
		fd_listApk.bottom = new FormAttachment(btnCancel, -6,SWT.TOP);
		
		btnInstall = new Button(shlApkInstaller, SWT.NONE);
		btnInstall.setEnabled(false);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		FormData fd_btnInstall = new FormData();
		fd_btnInstall.bottom = new FormAttachment(100, -10);
		fd_btnInstall.right = new FormAttachment(btnCancel, -6);
		btnInstall.setLayoutData(fd_btnInstall);
		btnInstall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (files.size()>0) result=txtSourceFolder.getText();
				shlApkInstaller.dispose();
			}
		});
		btnInstall.setText("Install");
		
		Composite compositeFolder = new Composite(shlApkInstaller, SWT.NONE);
		compositeFolder.setLayout(new GridLayout(3, false));
		FormData fd_compositeFolder = new FormData();
		fd_compositeFolder.right = new FormAttachment(100, -10);
		fd_compositeFolder.left = new FormAttachment(0, 10);
		fd_compositeFolder.top = new FormAttachment(0, 10);
		compositeFolder.setLayoutData(fd_compositeFolder);
		fd_lblAvailableFiles.top = new FormAttachment(compositeFolder, 10,SWT.BOTTOM);
		
		Label lblSourceFolder = new Label(compositeFolder, SWT.NONE);
		GridData gd_lblSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSourceFolder.widthHint = 92;
		lblSourceFolder.setLayoutData(gd_lblSourceFolder);
		lblSourceFolder.setText("Source Folder : ");
		txtSourceFolder = new Text(compositeFolder, SWT.BORDER);
		GridData gd_txtSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtSourceFolder.widthHint = 359;
		txtSourceFolder.setLayoutData(gd_txtSourceFolder);
		
		Button btnSourceFolder = new Button(compositeFolder, SWT.NONE);
		GridData gd_btnSourceFolder = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_btnSourceFolder.widthHint = 85;
		btnSourceFolder.setLayoutData(gd_btnSourceFolder);
		btnSourceFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlApkInstaller);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(txtSourceFolder.getText());

		        // Change the title bar text
		        dlg.setText("Directory chooser");

		        // Customizable message displayed in the dialog
		        dlg.setMessage("Select a directory");

		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	if (!txtSourceFolder.getText().equals(dir)) {
		        		txtSourceFolder.setText(dir);
		        		files = new Vector();
		    			File srcdir = new File(txtSourceFolder.getText());
		    			File[] chld = srcdir.listFiles();
		    			for(int i = 0; i < chld.length; i++) {
		    				if (chld[i].getName().toUpperCase().endsWith("APK"))
		    					files.add(chld[i]);
		    			}
		    			btnInstall.setEnabled(files.size()>0);
		    			listViewerApk.setInput(files);
		        	}
		        }
			}
		});
		btnSourceFolder.setText("...");
		btnSourceFolder.setFont(new Font(Display.getCurrent(),"Arial",11,SWT.NONE));
	}

}
