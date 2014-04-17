package gui;

import gui.models.CustIdItem;
import gui.models.ModelUpdater;
import gui.models.Models;
import gui.models.PropertiesFileContentProvider;
import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorLabelProvider;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import gui.tools.WidgetsTool;

public class CustIdManager extends Dialog {

	protected Object result;
	protected Shell shlDeviceUpdateChecker;
	protected CTabFolder tabFolder;
	protected Label lblInfo;
	protected Models models;
	protected Button btnApply;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CustIdManager(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	
	public Object open(Models m) {
		models=m;
		createContents();
		WidgetsTool.setSize(shlDeviceUpdateChecker);
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
		shlDeviceUpdateChecker.setSize(450, 336);
		shlDeviceUpdateChecker.setText("cdfID Manager");
		
		tabFolder = new CTabFolder(shlDeviceUpdateChecker, SWT.BORDER);
		tabFolder.setBounds(11, 10, 423, 256);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));				
		
		Button btnNewButton = new Button(shlDeviceUpdateChecker, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDeviceUpdateChecker.dispose();
			}
		});
		btnNewButton.setBounds(359, 272, 75, 25);
		btnNewButton.setText("Close");
		
		lblInfo = new Label(shlDeviceUpdateChecker, SWT.NONE);
		lblInfo.setBounds(11, 244, 342, 15);
		btnApply = new Button(shlDeviceUpdateChecker, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator i = models.keySet().iterator();
				while (i.hasNext()) {
					ModelUpdater mu = (ModelUpdater)models.get(i.next());
					if (mu.isModified())
						mu.save();
					btnApply.setEnabled(false);
				}
			}
		});
		btnApply.setBounds(279, 272, 75, 25);
		btnApply.setText("Apply");
		btnApply.setEnabled(false);
		parseMap();
	}

	public void parseMap() {
		Iterator keys = models.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String)keys.next();
			ModelUpdater m = (ModelUpdater)models.get(key);
			addTab(m);
		}
	}
	
	public void addTab(final ModelUpdater m) {
		models.put(m.getModel(), m);
		final TableViewer tableViewer = new TableViewer(tabFolder,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						tableViewer.setContentProvider(new PropertiesFileContentProvider());
						tableViewer.setLabelProvider(new VectorLabelProvider());
						// Create the popup menu
						  MenuManager menuMgr = new MenuManager();
						  Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
						  menuMgr.addMenuListener(new IMenuListener() {
						    @Override
						    public void menuAboutToShow(IMenuManager manager) {
						    	manager.add(new Action("Add") {
						            public void run() {
						            	System.out.println(m.getModel());
										AddCustId add = new AddCustId(shlDeviceUpdateChecker,SWT.PRIMARY_MODAL | SWT.SHEET);
										CustIdItem item = (CustIdItem)add.open(m);
										if (item != null) {
											m.AddCustId(item);
											btnApply.setEnabled(true);
							            	tableViewer.refresh();
										}
						            }
						        });						    		
						    	if (!tableViewer.getSelection().isEmpty()) {
							    	manager.add(new Action("Edit") {
							            public void run() {
											AddCustId add = new AddCustId(shlDeviceUpdateChecker,SWT.PRIMARY_MODAL | SWT.SHEET);
											TableLine line = (TableLine)tableViewer.getTable().getSelection()[0].getData();
											CustIdItem i = new CustIdItem(m.getModel(),line);
											m.RemoveCustId(line.getValueOf(0));
											CustIdItem item = (CustIdItem)add.open(m,i);
											if (item != null) {
												m.AddCustId(item);
												btnApply.setEnabled(true);
								            	tableViewer.refresh();
											}
							            }
							        });
							    	manager.add(new Action("Delete") {
							            public void run() {
							            	m.RemoveCustId(((TableLine)tableViewer.getTable().getSelection()[0].getData()).getValueOf(0));
							            	btnApply.setEnabled(true);
							            	tableViewer.refresh();
							            }
							        });
						    	}
						    }
						  });

						menuMgr.setRemoveAllWhenShown(true);
						tableViewer.getControl().setMenu(menu);
						Table tableDevice = tableViewer.getTable();
						TableColumn[] columns = new TableColumn[2];
						columns[0] = new TableColumn(tableDevice, SWT.NONE);
						columns[0].setText("Id");
						columns[1] = new TableColumn(tableDevice, SWT.NONE);
						columns[1].setText("Name");
						tableDevice.setHeaderVisible(true);
						tableDevice.setLinesVisible(true);
						TableSorter sort = new TableSorter(tableViewer);
						tableDevice.setSortColumn(tableDevice.getColumn(0));
						tableDevice.setSortDirection(SWT.UP);
						tableViewer.setInput(m.getCustIds());
						for (int i = 0, n = tableViewer.getTable().getColumnCount(); i < n; i++) {
							tableViewer.getTable().getColumn(i).pack();
						}
						tableViewer.getTable().pack();
						tableViewer.refresh();
						final CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
						tabItem.setText(m.getModel());
						tabItem.setControl(tableViewer.getTable());
						tabFolder.setSelection(tabItem);
					}
				}
		);
	}
}