package gui;

import java.io.File;
import java.util.Iterator;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class Decrypt extends Dialog {

	protected Shell shlDecruptWizard;
	private Text txtSourceFolder;
	ListViewer listViewerFiles;
	ListViewer listViewerConvert;
	Vector files = new Vector();
	Vector convert = new Vector();
	Vector result = null;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Decrypt(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Vector open() {
		createContents();
		shlDecruptWizard.open();
		shlDecruptWizard.layout();
		Display display = getParent().getDisplay();
		while (!shlDecruptWizard.isDisposed()) {
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
		shlDecruptWizard = new Shell(getParent(), getStyle());
		shlDecruptWizard.setSize(539, 302);
		shlDecruptWizard.setText("Decrypt Wizard");
		shlDecruptWizard.setLayout(new FormLayout());
		
		listViewerFiles = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List list = listViewerFiles.getList();
		FormData fd_list = new FormData();
		fd_list.bottom = new FormAttachment(0, 229);
		fd_list.right = new FormAttachment(0, 223);
		fd_list.top = new FormAttachment(0, 71);
		fd_list.left = new FormAttachment(0, 10);
		list.setLayoutData(fd_list);

	    listViewerFiles.setContentProvider(new IStructuredContentProvider() {
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
	    listViewerFiles.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((File)element).getName();
	        }
	      });

		Label lblAvailableFiles = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lblAvailableFiles = new FormData();
		fd_lblAvailableFiles.right = new FormAttachment(0, 115);
		fd_lblAvailableFiles.top = new FormAttachment(0, 51);
		fd_lblAvailableFiles.left = new FormAttachment(0, 10);
		lblAvailableFiles.setLayoutData(fd_lblAvailableFiles);
		lblAvailableFiles.setText("Available files :");
		
		listViewerConvert = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL);
		List list_1 = listViewerConvert.getList();
		FormData fd_list_1 = new FormData();
		fd_list_1.bottom = new FormAttachment(list, 0, SWT.BOTTOM);
		fd_list_1.top = new FormAttachment(list, 0, SWT.TOP);
		fd_list_1.right = new FormAttachment(100,-10);
		fd_list_1.left = new FormAttachment(0, 282);
		list_1.setLayoutData(fd_list_1);

		listViewerConvert.setContentProvider(new IStructuredContentProvider() {
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
	    
		listViewerConvert.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((File)element).getName();
	        }
	      });
		
		Label lblNewLabel_1 = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(0, 51);
		fd_lblNewLabel_1.left = new FormAttachment(0, 282);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("Files to convert :");
		
		Button btnCancel = new Button(shlDecruptWizard, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100,-10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDecruptWizard.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		Button btnConvert = new Button(shlDecruptWizard, SWT.NONE);
		
		FormData fd_btnConvert = new FormData();
		fd_btnConvert.right = new FormAttachment(btnCancel, -6);
		fd_btnConvert.bottom = new FormAttachment(100,-10);
		btnConvert.setLayoutData(fd_btnConvert);
		btnConvert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (convert.size()>0) result=convert;
				shlDecruptWizard.dispose();
			}
		});
		btnConvert.setText("Convert");
		
		Composite composite = new Composite(shlDecruptWizard, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(lblAvailableFiles, -6);
		fd_composite.left = new FormAttachment(0,10);
		fd_composite.right = new FormAttachment(100,-10);
		fd_composite.top = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		Label lblSourceFolder = new Label(composite, SWT.NONE);
		GridData gd_lblSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSourceFolder.widthHint = 106;
		lblSourceFolder.setLayoutData(gd_lblSourceFolder);
		lblSourceFolder.setText("Source Folder : ");
		txtSourceFolder = new Text(composite, SWT.BORDER);
		GridData gd_txtSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtSourceFolder.widthHint = 338;
		txtSourceFolder.setLayoutData(gd_txtSourceFolder);
		
		Button btnSourceFolder = new Button(composite, SWT.NONE);
		GridData gd_btnSourceFolder = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnSourceFolder.widthHint = 85;
		btnSourceFolder.setLayoutData(gd_btnSourceFolder);
		btnSourceFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlDecruptWizard);

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
		        		convert = new Vector();
		    			File srcdir = new File(txtSourceFolder.getText());
		    			File[] chld = srcdir.listFiles();
		    			for(int i = 0; i < chld.length; i++) {
		    				if (chld[i].getName().toUpperCase().startsWith("FILE"))
		    					files.add(chld[i]);
		    			}
		    			listViewerFiles.setInput(files);
		    			listViewerConvert.setInput(convert);
		        	}
		        }
			}
		});
		btnSourceFolder.setText("...");
		btnSourceFolder.setFont(new Font(Display.getCurrent(),"Arial",11,SWT.NONE));
		
		
		Composite composite_1 = new Composite(shlDecruptWizard, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(100, -78);
		fd_composite_1.top = new FormAttachment(composite, 61);
		fd_composite_1.left = new FormAttachment(list, 5);
		fd_composite_1.right = new FormAttachment(list_1, -6);
		composite_1.setLayoutData(fd_composite_1);
		
		Button btnNewButton_1 = new Button(composite_1, SWT.NONE);
		GridData gd_btnNewButton_1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton_1.heightHint = 26;
		gd_btnNewButton_1.widthHint = 40;
		btnNewButton_1.setLayoutData(gd_btnNewButton_1);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerFiles.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					File f = (File)i.next();
					files.remove(f);
					convert.add(f);
					listViewerFiles.refresh();
					listViewerConvert.refresh();
				}
			}
		});
		btnNewButton_1.setText("->");
		new Label(composite_1, SWT.NONE);
		
		Button btnNewButton_2 = new Button(composite_1, SWT.NONE);
		GridData gd_btnNewButton_2 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton_2.heightHint = 26;
		gd_btnNewButton_2.widthHint = 40;
		btnNewButton_2.setLayoutData(gd_btnNewButton_2);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerConvert.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					File f = (File)i.next();
					convert.remove(f);
					files.add(f);
					listViewerFiles.refresh();
					listViewerConvert.refresh();
				}
			}
		});
		btnNewButton_2.setText("<-");
	}

}
