package gui;

import gui.tools.WidgetsTool;

import java.io.File;
import java.util.HashSet;
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
import org.system.OS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;

public class VariantSelector extends Dialog {

	protected Object result;
	protected Shell shlVariantSelector;
	private Button btnCancel;
	HashSet<String> currentVariant;
	private List listVariant;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public VariantSelector(Shell parent, int style) {
		super(parent, style);
		setText("Variant Selector");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(HashSet<String> variantlist) {
		if (variantlist.size()==0) return null;
		currentVariant = variantlist;
		createContents();
		WidgetsTool.setSize(shlVariantSelector);
		shlVariantSelector.open();
		shlVariantSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlVariantSelector.isDisposed()) {
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
		shlVariantSelector = new Shell(getParent(), getStyle());
		shlVariantSelector.setSize(168, 434);
		shlVariantSelector.setText("Variant Selector");
		shlVariantSelector.setLayout(new FormLayout());
		
		btnCancel = new Button(shlVariantSelector, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlVariantSelector.dispose();
			}
		});
		btnCancel.setText("Cancel");
		ListViewer listVariantViewer = new ListViewer(shlVariantSelector, SWT.BORDER | SWT.V_SCROLL);
		listVariant = listVariantViewer.getList();
		fd_btnCancel.top = new FormAttachment(listVariant, 6);
		FormData fd_listVariant = new FormData();
		fd_listVariant.bottom = new FormAttachment(100, -41);
		fd_listVariant.top = new FormAttachment(0, 10);
		fd_listVariant.right = new FormAttachment(100, -10);
		fd_listVariant.left = new FormAttachment(0, 10);
		listVariant.setLayoutData(fd_listVariant);
		listVariant.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        int selected = listVariant.getSelectionIndex();
		        String string = listVariant.getItem(selected);
		        result = string;
		        shlVariantSelector.dispose();
		      }
		    });

		listVariantViewer.setContentProvider(new IStructuredContentProvider() {
	        public Object[] getElements(Object inputElement) {
	          HashSet<String> s = (HashSet<String>)inputElement;
	          return s.toArray();
	        }
	        
	        public void dispose() {
	        }
	   
	        public void inputChanged(
	          Viewer viewer,
	          Object oldInput,
	          Object newInput) {
	        }
	    });

		listVariantViewer.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return (String)element;
	        }
		});
		
		listVariantViewer.setInput(currentVariant);

	}
}
