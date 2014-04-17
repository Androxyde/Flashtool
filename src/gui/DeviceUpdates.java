package gui;

import gui.models.ModelUpdater;
import gui.models.Models;
import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorContentProvider;
import gui.models.VectorLabelProvider;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.system.DeviceEntry;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class DeviceUpdates extends Dialog {

	protected Object result;
	protected Shell shlDeviceUpdateChecker;
	protected CTabFolder tabFolder;
	protected DeviceEntry _entry;
	protected Label lblInfo;
	protected CTabItem tabItem;
	private Table tableDevice;
	private TableViewer tableViewer;
	protected Models models;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DeviceUpdates(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(DeviceEntry entry) {
		_entry = entry;
		createContents();
		shlDeviceUpdateChecker.open();
		shlDeviceUpdateChecker.layout();
		Display display = getParent().getDisplay();
		while (!shlDeviceUpdateChecker.isDisposed()) {
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
		shlDeviceUpdateChecker = new Shell(getParent(), getStyle());
		shlDeviceUpdateChecker.setSize(450, 300);
		shlDeviceUpdateChecker.setText("Device Update Checker");
		
		tabFolder = new CTabFolder(shlDeviceUpdateChecker, SWT.BORDER);
		tabFolder.setBounds(11, 10, 423, 223);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));				
		
		Button btnNewButton = new Button(shlDeviceUpdateChecker, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDeviceUpdateChecker.dispose();
			}
		});
		btnNewButton.setBounds(359, 239, 75, 25);
		btnNewButton.setText("Close");
		
		lblInfo = new Label(shlDeviceUpdateChecker, SWT.NONE);
		lblInfo.setBounds(11, 244, 342, 15);

		fillMap();
		FillJob fj = new FillJob("Update Search");
		fj.schedule();
	}

	public void fillMap() {
		models = _entry.getUpdatableModels(false);
	}

	public void addTab(final String tabtitle) {
		final Vector<TableLine> result = new Vector<TableLine>();
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						tabItem = new CTabItem(tabFolder, SWT.NONE);
						tabItem.setText(tabtitle.length()>0?tabtitle:_entry.getId());
						tableViewer = new TableViewer(tabFolder,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
						tableViewer.setContentProvider(new VectorContentProvider());
						tableViewer.setLabelProvider(new VectorLabelProvider());

						tableDevice = tableViewer.getTable();
						TableColumn[] columns = new TableColumn[2];
						columns[0] = new TableColumn(tableDevice, SWT.NONE);
						columns[0].setText("Id");
						columns[1] = new TableColumn(tableDevice, SWT.NONE);
						columns[1].setText("Version");
						tableDevice.setHeaderVisible(true);
						tableDevice.setLinesVisible(true);
						TableSorter sort = new TableSorter(tableViewer);
						tableDevice.setSortColumn(tableDevice.getColumn(0));
						tableDevice.setSortDirection(SWT.UP);
						tableViewer.setInput(result);
					}
				}
		);
		
		ModelUpdater mu = (ModelUpdater)models.get(tabtitle);
		try {
		mu.checkUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator vlist = mu.getVersions().keySet().iterator();
		while (vlist.hasNext()) {
			String name = (String)vlist.next();
			TableLine line1 = new TableLine();
			line1.add(name);
			line1.add(mu.getVersions().getProperty(name));
			result.add(line1);
			Display.getDefault().asyncExec(
					new Runnable() {
						public void run() {
							tableViewer.refresh();
						}
					}
			);				
		}
		
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						tableViewer.setInput(result);
						for (int i = 0, n = tableDevice.getColumnCount(); i < n; i++) {
							  tableDevice.getColumn(i).pack();
						}
						tableDevice.pack();
						tableViewer.refresh();
						tabItem.setControl(tableDevice);
					}
				}
		);
	}
	
	public void fillTab() {
		Iterator imodels = models.keySet().iterator();
		while (imodels.hasNext()) {
			addTab((String)imodels.next());
		}
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						tabFolder.redraw();
						tabFolder.setSelection(0);
					}
				}
		);
	}

	class FillJob extends Job {

		boolean canceled = false;

		public FillJob(String name) {
			super(name);
		}
		
		public void stopSearch() {
			canceled=true;
		}
		
	    protected IStatus run(IProgressMonitor monitor) {
			    while (!canceled) {
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									lblInfo.setText("Searching for updates. Please wait");
								}
							}
					);
					fillTab();
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									lblInfo.setText("");
								}
							}
					);
					return Status.OK_STATUS;
			    }
			    return Status.CANCEL_STATUS;
	    }
	}
}