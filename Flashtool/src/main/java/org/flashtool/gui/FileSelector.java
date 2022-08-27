package org.flashtool.gui;

import java.io.File;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.flashtool.flashsystem.S1Command;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSelector extends Dialog {

	protected Object result;
	protected Shell shlTABackupSelector;
	private Button btnCancel;
	private List listFiles;
	private Label lblNewLabel;
	private Label lblNewLabel_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FileSelector(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(Vector<String> vFiles) {
		createContents(vFiles);
		shlTABackupSelector.open();
		shlTABackupSelector.layout();
		shlTABackupSelector.setSize(390, 434);
		
		lblNewLabel = new Label(shlTABackupSelector, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(listFiles, 0, SWT.LEFT);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Multiple files have been found for this partition");
		
		lblNewLabel_1 = new Label(shlTABackupSelector, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 6);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("Please choose the right one");
		Display display = getParent().getDisplay();
		while (!shlTABackupSelector.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(Vector<String> vFiles) {
		shlTABackupSelector = new Shell(getParent(), getStyle());
		shlTABackupSelector.setText("Partition Image Selector");
		shlTABackupSelector.setLayout(new FormLayout());
		
		btnCancel = new Button(shlTABackupSelector, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shlTABackupSelector.dispose();
			}
		});
		btnCancel.setText("Cancel");
		ListViewer listTAViewer = new ListViewer(shlTABackupSelector, SWT.BORDER | SWT.V_SCROLL);
		listFiles = listTAViewer.getList();
		FormData fd_listTA = new FormData();
		fd_listTA.bottom = new FormAttachment(btnCancel, -6, SWT.TOP);
		fd_listTA.top = new FormAttachment(0, 81);
		fd_listTA.right = new FormAttachment(100, -10);
		fd_listTA.left = new FormAttachment(0, 10);
		listFiles.setLayoutData(fd_listTA);
		listFiles.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        int selected = listFiles.getSelectionIndex();
		        String string = listFiles.getItem(selected);
		        result = string;
		        shlTABackupSelector.dispose();
		      }
		    });

		listTAViewer.setContentProvider(new IStructuredContentProvider() {
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

		listTAViewer.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((String)element);
	        }
		});

		listTAViewer.setInput(vFiles);

	}
}