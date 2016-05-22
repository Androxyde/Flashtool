package gui;

import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorContentProvider;
import gui.models.VectorLabelProvider;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.system.DeviceEntry;
import org.system.Devices;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DeviceSelector extends Dialog {

	protected Object result;
	protected Shell shlDeviceSelector;
	private Table tableDevices;
	private TableViewer tableViewer;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DeviceSelector(Shell parent, int style) {
		super(parent, style);
		setText("Device Selector");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		fillTable();
		shlDeviceSelector.open();
		shlDeviceSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlDeviceSelector.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	/**
	 * Open the dialog.
	 * @return the result
	 */

	public Object open(Properties p) {
		createContents();
		fillTable(p);
		shlDeviceSelector.open();
		shlDeviceSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlDeviceSelector.isDisposed()) {
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
		shlDeviceSelector = new Shell(getParent(), getStyle());
		shlDeviceSelector.setSize(289, 434);
		shlDeviceSelector.setText("Device Selector");
		shlDeviceSelector.setLayout(new FormLayout());
		
		Button btnCancel = new Button(shlDeviceSelector, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlDeviceSelector.dispose();
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Composite compositeTable = new Composite(shlDeviceSelector, SWT.NONE);
		compositeTable.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeTable = new FormData();
		fd_compositeTable.bottom = new FormAttachment(btnCancel, -6);
		fd_compositeTable.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_compositeTable.top = new FormAttachment(0, 10);
		fd_compositeTable.left = new FormAttachment(0, 10);
		compositeTable.setLayoutData(fd_compositeTable);
		
		tableViewer = new TableViewer(compositeTable,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		tableViewer.setContentProvider(new VectorContentProvider());
		tableViewer.setLabelProvider(new VectorLabelProvider());

		tableDevices = tableViewer.getTable();
		TableColumn[] columns = new TableColumn[2];
		columns[0] = new TableColumn(tableDevices, SWT.NONE);
		columns[0].setText("Id");
		columns[1] = new TableColumn(tableDevices, SWT.NONE);
		columns[1].setText("Name");
		tableDevices.setHeaderVisible(true);
		tableDevices.setLinesVisible(true);
		tableDevices.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        TableItem[] selection = tableDevices.getSelection();
		        String string = selection[0].getText(0);
		        result = string;
		        shlDeviceSelector.dispose();
		      }
		    });
		TableSorter sort = new TableSorter(tableViewer);
	}

	public void fillTable() {
		Vector result = new Vector();
		Enumeration<Object> e = Devices.listDevices(false);
	    while (e.hasMoreElements()) {
	    	DeviceEntry entry = Devices.getDevice((String)e.nextElement());
	    	TableLine line = new TableLine();
	    	line.add(entry.getId());
	    	line.add(entry.getName());
	    	result.add(line);
	    }
	    tableViewer.setInput(result);
	    tableViewer.getTable().setSortColumn(tableDevices.getColumn(0));
	    for (int nbcols=0;nbcols<tableDevices.getColumnCount();nbcols++)
	    	tableDevices.getColumn(nbcols).pack();
	    tableDevices.setSortColumn(tableDevices.getColumn(0));
	    tableDevices.setSortDirection(SWT.UP);
	    tableViewer.refresh();
	}

	public void fillTable(Properties p) {
		Vector result = new Vector();
		Enumeration<Object> e = p.keys();
	    while (e.hasMoreElements()) {
	    	TableLine line = new TableLine();
	    	String key = (String)e.nextElement();
	    	line.add(key);
	    	line.add(p.getProperty(key));
	    	result.add(line);
	    }
	    tableViewer.setInput(result);
	    for (int nbcols=0;nbcols<tableDevices.getColumnCount();nbcols++)
	    	tableDevices.getColumn(nbcols).pack();
	    tableDevices.setSortColumn(tableDevices.getColumn(0));
	    tableDevices.setSortDirection(SWT.UP);
	    tableViewer.refresh();
	}

}
