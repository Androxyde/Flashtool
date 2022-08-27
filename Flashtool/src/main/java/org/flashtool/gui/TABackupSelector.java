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
import org.flashtool.flashsystem.S1Command;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class TABackupSelector extends Dialog {

	protected Object result;
	protected Shell shlTABackupSelector;
	private Button btnCancel;
	private List listTA;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TABackupSelector(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlTABackupSelector.open();
		shlTABackupSelector.layout();
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
	private void createContents() {
		shlTABackupSelector = new Shell(getParent(), getStyle());
		shlTABackupSelector.setSize(354, 434);
		shlTABackupSelector.setText("TA Backup Selector");
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
		listTA = listTAViewer.getList();
		FormData fd_listTA = new FormData();
		fd_listTA.bottom = new FormAttachment(btnCancel, -6, SWT.TOP);
		fd_listTA.top = new FormAttachment(0, 10);
		fd_listTA.right = new FormAttachment(100, -10);
		fd_listTA.left = new FormAttachment(0, 10);
		listTA.setLayoutData(fd_listTA);
		listTA.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        int selected = listTA.getSelectionIndex();
		        String string = listTA.getItem(selected);
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

		Vector<String> tabackups = new Vector();
		String serial = Devices.getCurrent().getSerial();
		String folder = OS.getFolderRegisteredDevices()+File.separator+serial+File.separator+"rawta";
		File srcdir = new File(folder);
		File[] chld = srcdir.listFiles();
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().endsWith(".fta")) {
				try {
					JarFile jf = new JarFile(chld[i]);
					Attributes attr = jf.getManifest().getMainAttributes();
					if (attr.getValue("serial").equals(Devices.getCurrent().getSerial())) {
						tabackups.add(attr.getValue("timestamp")+ " : " + attr.getValue("build"));
					}
					else {
						log.info("File skipped : "+chld[i].getName()+". Not for your device");
					}
					jf.close();
				} catch (Exception e) {
					log.error("This file : " + chld[i].getName()+" is corrupted");
				}
				
			}
		}
		listTAViewer.setInput(tabackups);

	}
}
