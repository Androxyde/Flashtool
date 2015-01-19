package gui;

import gui.tools.WidgetsTool;

import java.io.File;
import java.util.Vector;

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
import org.system.Devices;
import org.system.OS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;

public class BusyboxSelector extends Dialog {

	protected Object result;
	protected Shell shlBusyboxSelector;
	private Button btnCancel;
	private List listBusybox;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BusyboxSelector(Shell parent, int style) {
		super(parent, style);
		setText("Busybox Selector");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlBusyboxSelector);
		shlBusyboxSelector.open();
		shlBusyboxSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlBusyboxSelector.isDisposed()) {
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
		shlBusyboxSelector = new Shell(getParent(), getStyle());
		shlBusyboxSelector.setSize(168, 434);
		shlBusyboxSelector.setText("Busybox Selector");
		shlBusyboxSelector.setLayout(new FormLayout());
		
		btnCancel = new Button(shlBusyboxSelector, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlBusyboxSelector.dispose();
			}
		});
		btnCancel.setText("Cancel");
		ListViewer listBusyboxViewer = new ListViewer(shlBusyboxSelector, SWT.BORDER | SWT.V_SCROLL);
		listBusybox = listBusyboxViewer.getList();
		fd_btnCancel.top = new FormAttachment(listBusybox, 6);
		FormData fd_listBusybox = new FormData();
		fd_listBusybox.bottom = new FormAttachment(100, -41);
		fd_listBusybox.top = new FormAttachment(0, 10);
		fd_listBusybox.right = new FormAttachment(100, -10);
		fd_listBusybox.left = new FormAttachment(0, 10);
		listBusybox.setLayoutData(fd_listBusybox);
		listBusybox.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        int selected = listBusybox.getSelectionIndex();
		        String string = listBusybox.getItem(selected);
		        result = string;
		        shlBusyboxSelector.dispose();
		      }
		    });

		listBusyboxViewer.setContentProvider(new IStructuredContentProvider() {
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

		listBusyboxViewer.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((File)element).getName();
	        }
		});
		Vector<File> folders = new Vector();
		File srcdir = new File(OS.getFolderDevices()+File.separator+"busybox");
		File[] chld = srcdir.listFiles();
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].isDirectory())
				folders.add(chld[i]);
		}
		listBusyboxViewer.setInput(folders);

	}
}
