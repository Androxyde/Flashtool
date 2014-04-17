package gui;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.system.db.*;

public class DBEditor extends Dialog {

	protected Object result = new String("Cancel");
	protected Shell shlDBEdit;
	ListViewer listViewerServices;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DBEditor(Shell parent, int style) {
		super(parent, style);
		setText("Import FTF from Sony Database");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlDBEdit.open();
		shlDBEdit.layout();
		Display display = getParent().getDisplay();
		while (!shlDBEdit.isDisposed()) {
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
		shlDBEdit = new Shell(getParent(), getStyle());
		shlDBEdit.setSize(450, 300);
		shlDBEdit.setText(getText());
		listViewerServices = new ListViewer(shlDBEdit, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List list = listViewerServices.getList();
		FormData fd_list = new FormData();
		fd_list.bottom = new FormAttachment(0, 229);
		fd_list.right = new FormAttachment(0, 223);
		fd_list.top = new FormAttachment(0, 71);
		fd_list.left = new FormAttachment(0, 10);
		list.setLayoutData(fd_list);
		
	    listViewerServices.setContentProvider(new IStructuredContentProvider() {
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
	    listViewerServices.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((File)element).getName();
	        }
	      });

	}

}
