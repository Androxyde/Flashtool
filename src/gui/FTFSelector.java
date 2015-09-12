package gui;

import flashsystem.Bundle;
import flashsystem.Category;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.system.DeviceEntry;
import org.system.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

import gui.models.ContentContentProvider;
import gui.models.ContentLabelProvider;
import gui.models.Firmware;
import gui.models.FirmwareContentProvider;
import gui.models.FirmwareLabelProvider;
import gui.models.FirmwaresModel;
import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Combo;

public class FTFSelector extends Dialog {

	protected Bundle result;
	protected Shell shlFirmwareSelector;
	//private Properties hasCmd25 = new Properties();
	//private String filename="";
	//private Button btnOK=null;
	private TableViewer tableFirmwareViewer;
	private TableViewer tableContentViewer;
	private Table tableFirmware;
	private Table tableContent;
	private Text sourceFolder;
	private Composite compositeContent;
	private Composite compositeMisc;
	private Button btnCancel;
	private Label lblMisc;
	private Label lblWipe;
	private Composite compositeFirmware;
	private Composite compositeExclude;
	private Composite compositeWipe;
	private ScrolledComposite scrolledCompositeExclude;
	private boolean simulate = false;
	private Button btnCheckCmd25;
	private Button btnFlash;
	private Label lblContent;
	private Combo comboUSBBuffer;
	private int maxbuffer = 0;
	private FormData fd_compositeContent;
	private Text textDevice;
	private Label lblNewLabel_1;
	private FormData fd_compositeFirmware;
	private FirmwaresModel firms=null;
	private Label lblFirmware;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FTFSelector(Shell parent, int style) {
		super(parent, style);
		setText("Firmware selector");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String pathname, String ftfname) {
		createContents(pathname, ftfname);
				
		
		Button btnCheckSimulate = new Button(shlFirmwareSelector, SWT.CHECK);
		btnCheckSimulate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulate = btnCheckSimulate.getSelection();
			}
		});
		FormData fd_btnCheckSimulate = new FormData();
		fd_btnCheckSimulate.left = new FormAttachment(0, 10);
		fd_btnCheckSimulate.top = new FormAttachment(btnCancel, 4, SWT.TOP);
		btnCheckSimulate.setLayoutData(fd_btnCheckSimulate);
		btnCheckSimulate.setText("Simulate");
		
		btnCheckCmd25 = new Button(shlFirmwareSelector, SWT.CHECK);
		FormData fd_btnCheckButton = new FormData();
		fd_btnCheckButton.top = new FormAttachment(btnCancel, 4, SWT.TOP);
		fd_btnCheckButton.left = new FormAttachment(btnCheckSimulate, 19);
		btnCheckCmd25.setLayoutData(fd_btnCheckButton);
		btnCheckCmd25.setText("Disable final verification");
		btnCheckCmd25.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckCmd25.getSelection()) {
					WidgetTask.openOKBox(shlFirmwareSelector, "Warning, this option will not be used if a FSC script is found");
				}
				result.setCmd25(btnCheckCmd25.getSelection()?"true":"false");
			}
		});
		if (result!=null)
			btnCheckCmd25.setSelection(result.hasCmd25());
		
		comboUSBBuffer = new Combo(shlFirmwareSelector, SWT.READ_ONLY);
		fd_compositeContent.bottom = new FormAttachment(comboUSBBuffer, -14);
		comboUSBBuffer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				maxbuffer=comboUSBBuffer.getSelectionIndex();
			}
		});
		comboUSBBuffer.setItems(new String[] {"Device maxsize", "512K", "256K", "128K"});
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(btnCancel, 2, SWT.TOP);
		comboUSBBuffer.setLayoutData(fd_combo);
		
		Label lblNewLabel = new Label(shlFirmwareSelector, SWT.NONE);
		fd_combo.left = new FormAttachment(lblNewLabel, 6);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(100, -244);
		fd_lblNewLabel.top = new FormAttachment(btnCancel, 5, SWT.TOP);
		comboUSBBuffer.select(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Max USB Buffer :");
		
		textDevice = new Text(shlFirmwareSelector, SWT.BORDER);
		fd_compositeFirmware.top = new FormAttachment(0, 100);
		textDevice.setToolTipText("Double click to get list of devices");
		textDevice.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				String res = WidgetTask.openDeviceSelector(shlFirmwareSelector);
				if (res.length()>0) {
					DeviceEntry ent = new DeviceEntry(res);
					textDevice.setText(ent.getName());
					firms.firmwares.setDevice(ent.getId());
					tableFirmwareViewer.refresh();
					tableContentViewer.refresh();
					tableFirmware.select(0);
				    if (tableFirmware.getSelection().length>0) {
				    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
				    	Firmware firm = (Firmware)sel.getFirstElement();
				    	result = firm.getBundle();
				    };
				}
			}
		});
		textDevice.setEditable(false);
		FormData fd_textDevice = new FormData();
		fd_textDevice.bottom = new FormAttachment(compositeFirmware, -6);
		fd_textDevice.top = new FormAttachment(lblFirmware, 1);
		textDevice.setLayoutData(fd_textDevice);
		
		lblNewLabel_1 = new Label(shlFirmwareSelector, SWT.NONE);
		fd_textDevice.left = new FormAttachment(lblNewLabel_1, 6);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.bottom = new FormAttachment(compositeFirmware, -6);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("Device filter :");
		
		Button btnNewButton_1 = new Button(shlFirmwareSelector, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textDevice.setText("");
				firms.firmwares.setDevice("");
				tableFirmwareViewer.refresh();
				tableContentViewer.refresh();
				tableFirmware.select(0);
			    if (tableFirmware.getSelection().length>0) {
			    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
			    	Firmware firm = (Firmware)sel.getFirstElement();
			    	result = firm.getBundle();
			    };
			}
		});
		fd_textDevice.right = new FormAttachment(btnNewButton_1, -6);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.bottom = new FormAttachment(compositeFirmware, -6);
		fd_btnNewButton_1.right = new FormAttachment(compositeContent, -6);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("Clear filter");
		WidgetsTool.setSize(shlFirmwareSelector);
		shlFirmwareSelector.open();
		shlFirmwareSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlFirmwareSelector.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (result!=null) {
			result.setSimulate(simulate);
			result.setMaxBuffer(maxbuffer);
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents(String pathname, String ftfname) {
		shlFirmwareSelector = new Shell(getParent(), getStyle());
		shlFirmwareSelector.setSize(708, 484);
		shlFirmwareSelector.setText("Firmware Selector");
		shlFirmwareSelector.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
					result = null;
					shlFirmwareSelector.dispose();
		      }
		    });
		shlFirmwareSelector.setLayout(new FormLayout());
		btnCancel = new Button(shlFirmwareSelector, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -11);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlFirmwareSelector.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		compositeFirmware = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeFirmware = new FormData();
		fd_compositeFirmware.left = new FormAttachment(0, 10);
		compositeFirmware.setLayoutData(fd_compositeFirmware);
		compositeFirmware.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableFirmwareViewer = new TableViewer(compositeFirmware,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		tableFirmwareViewer.setContentProvider(new FirmwareContentProvider());
		tableFirmwareViewer.setLabelProvider(new FirmwareLabelProvider());

		
		tableFirmware = tableFirmwareViewer.getTable();
		TableColumn[] columns = new TableColumn[4];
		columns[0] = new TableColumn(tableFirmware, SWT.NONE);
		columns[0].setText("Filename");
		columns[1] = new TableColumn(tableFirmware, SWT.NONE);
		columns[1].setText("Device");
		columns[2] = new TableColumn(tableFirmware, SWT.NONE);
		columns[2].setText("Version");
		columns[3] = new TableColumn(tableFirmware, SWT.NONE);
		columns[3].setText("Branding");
	    for (int i = 0, n = tableFirmware.getColumnCount(); i < n; i++) {
	    	tableFirmware.getColumn(i).pack();
	    	if (i==0) {
	    		tableFirmware.getColumn(i).setWidth(0);tableFirmware.getColumn(i).setResizable(false);
	    	}
	    	if (i==1)
	    		tableFirmware.getColumn(i).setWidth(60);
		    if (i==2)
		    	tableFirmware.getColumn(i).setWidth(100);
		    if (i==3)
		    	tableFirmware.getColumn(i).setWidth(180);
	    }
		tableFirmware.setHeaderVisible(true);
		tableFirmware.setLinesVisible(true);
		tableFirmware.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        shlFirmwareSelector.dispose();
		      }
		    });
		tableFirmware.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
		    	  Firmware firm = (Firmware)sel.getFirstElement();
		    	  tableContentViewer.setInput(firm);
		    	  tableContentViewer.refresh();
		    	  result = firm.getBundle();
		    	  btnCheckCmd25.setSelection(result.hasCmd25());
		    	  updateCheckBoxes();
		      }
		    });
		
		compositeContent = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeFirmware.bottom = new FormAttachment(compositeContent, 0, SWT.BOTTOM);
		fd_compositeFirmware.right = new FormAttachment(compositeContent, -6);
		fd_compositeContent = new FormData();
		fd_compositeContent.left = new FormAttachment(0, 347);
		compositeContent.setLayoutData(fd_compositeContent);

		tableContentViewer = new TableViewer(compositeContent, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		tableContentViewer.setContentProvider(new ContentContentProvider());
		tableContentViewer.setLabelProvider(new ContentLabelProvider());
		
		tableContent = tableContentViewer.getTable();
		tableContent.setEnabled(false);
		tableContent.setBounds(0, 0, 158, 335);
		TableColumn[] columnsContent = new TableColumn[1];
		columnsContent[0] = new TableColumn(tableContent, SWT.NONE);
		columnsContent[0].setText("Filename");
	    for (int i = 0, n = tableContent.getColumnCount(); i < n; i++) {
	    	tableContent.getColumn(i).pack();
	    	tableContent.getColumn(i).setWidth(153);
	      }
		tableContent.setHeaderVisible(true);
		tableContent.setLinesVisible(true);
		
		lblFirmware = new Label(shlFirmwareSelector, SWT.NONE);
		FormData fd_lblFirmware = new FormData();
		fd_lblFirmware.right = new FormAttachment(0, 109);
		fd_lblFirmware.top = new FormAttachment(0, 53);
		fd_lblFirmware.left = new FormAttachment(0, 10);
		lblFirmware.setLayoutData(fd_lblFirmware);
		lblFirmware.setText("Firmware :");
		
		lblContent = new Label(shlFirmwareSelector, SWT.NONE);
		fd_compositeContent.top = new FormAttachment(lblContent, 5);
		FormData fd_lblContent = new FormData();
		fd_lblContent.left = new FormAttachment(lblFirmware, 238);
		fd_lblContent.top = new FormAttachment(lblFirmware, 0, SWT.TOP);
		lblContent.setLayoutData(fd_lblContent);
		lblContent.setText("Content :");

		lblWipe = new Label(shlFirmwareSelector, SWT.NONE);
		fd_lblContent.right = new FormAttachment(lblWipe, -83);
		FormData fd_lblWipe = new FormData();
		fd_lblWipe.left = new FormAttachment(0, 511);
		fd_lblWipe.right = new FormAttachment(100, -11);
		fd_lblWipe.bottom = new FormAttachment(lblFirmware, 0, SWT.BOTTOM);
		lblWipe.setLayoutData(fd_lblWipe);
		lblWipe.setText("Wipe :");

		lblMisc = new Label(shlFirmwareSelector, SWT.NONE);
		fd_compositeContent.right = new FormAttachment(lblMisc, -6);
		FormData fd_lblMisc = new FormData();
		fd_lblMisc.right = new FormAttachment(100, -11);
		fd_lblMisc.left = new FormAttachment(0, 511);
		lblMisc.setLayoutData(fd_lblMisc);
		lblMisc.setText("MiscTA Exclude : ");

		ScrolledComposite scrolledCompositeMisc = new ScrolledComposite(shlFirmwareSelector, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_lblMisc.bottom = new FormAttachment(scrolledCompositeMisc, -2);
		FormData fd_compositeMisc = new FormData();
		fd_compositeMisc.right = new FormAttachment(100, -10);
		fd_compositeMisc.left = new FormAttachment(compositeContent, 6);
		fd_compositeMisc.bottom = new FormAttachment(0, 408);
		fd_compositeMisc.top = new FormAttachment(0, 323);
		scrolledCompositeMisc.setLayoutData(fd_compositeMisc);

		compositeMisc = new Composite(scrolledCompositeMisc, SWT.NONE);
		compositeMisc.setLayoutData(new FormData());
		scrolledCompositeMisc.setContent(compositeMisc);
		GridLayout layoutMisc = new GridLayout();
        layoutMisc.numColumns = 1;
        compositeMisc.setLayout(layoutMisc);
        compositeMisc.setSize(compositeMisc.computeSize(SWT.DEFAULT, SWT.DEFAULT));				
		
		Composite compositeSearchBar = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_lblWipe.top = new FormAttachment(compositeSearchBar, 6);
		compositeSearchBar.setLayout(new GridLayout(3, false));
		FormData fd_compositeSearchBar = new FormData();
		fd_compositeSearchBar.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		fd_compositeSearchBar.left = new FormAttachment(0, 10);
		fd_compositeSearchBar.bottom = new FormAttachment(lblFirmware, -6);
		fd_compositeSearchBar.top = new FormAttachment(0, 10);
		compositeSearchBar.setLayoutData(fd_compositeSearchBar);
		
				Label lblSourceFolder = new Label(compositeSearchBar, SWT.NONE);
				GridData gd_lblSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_lblSourceFolder.widthHint = 93;
				lblSourceFolder.setLayoutData(gd_lblSourceFolder);
				lblSourceFolder.setText("Source folder :");
				
				sourceFolder = new Text(compositeSearchBar, SWT.BORDER);
				GridData gd_sourceFolder = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
				gd_sourceFolder.widthHint = 513;
				sourceFolder.setLayoutData(gd_sourceFolder);
				sourceFolder.setEditable(false);
				if (pathname.length()==0) {
					sourceFolder.setText(OS.getFolderFirmwares());
				}
				else sourceFolder.setText(pathname);

				Button btnNewButton = new Button(compositeSearchBar, SWT.NONE);
				GridData gd_btnNewButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
				gd_btnNewButton.widthHint = 46;
				btnNewButton.setLayoutData(gd_btnNewButton);
				btnNewButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						DirectoryDialog dlg = new DirectoryDialog(shlFirmwareSelector);

				        // Set the initial filter path according
				        // to anything they've selected or typed in
				        dlg.setFilterPath(sourceFolder.getText());

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
				        	if (!sourceFolder.getText().equals(dir)) {
				        		sourceFolder.setText(dir);
				        		updateTables();
				        	}
				        }
					}
				});
				btnNewButton.setText("...");
				ScrolledComposite scrolledCompositeWipe = new ScrolledComposite(shlFirmwareSelector, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
				FormData fd_scrolledCompositeWipe = new FormData();
				fd_scrolledCompositeWipe.right = new FormAttachment(btnCancel, 1, SWT.RIGHT);
				fd_scrolledCompositeWipe.left = new FormAttachment(compositeContent, 6);
				fd_scrolledCompositeWipe.bottom = new FormAttachment(lblWipe, 103, SWT.BOTTOM);
				fd_scrolledCompositeWipe.top = new FormAttachment(lblWipe, 5);
				scrolledCompositeWipe.setLayoutData(fd_scrolledCompositeWipe);
				
				compositeWipe = new Composite(scrolledCompositeWipe, SWT.NONE);
				scrolledCompositeWipe.setContent(compositeWipe);
				GridLayout layoutWipe = new GridLayout();
		        layoutWipe.numColumns = 1;
		        compositeWipe.setLayout(layoutWipe);
		        compositeWipe.setSize(compositeWipe.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				scrolledCompositeExclude = new ScrolledComposite(shlFirmwareSelector, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
				FormData fd_scrolledComposite = new FormData();
				fd_scrolledComposite.left = new FormAttachment(compositeContent, 6);
				fd_scrolledComposite.right = new FormAttachment(100, -10);
				fd_scrolledComposite.bottom = new FormAttachment(lblMisc, -6);
				scrolledCompositeExclude.setLayoutData(fd_scrolledComposite);
				
				compositeExclude = new Composite(scrolledCompositeExclude, SWT.NONE);
				scrolledCompositeExclude.setContent(compositeExclude);
				GridLayout layoutExclude = new GridLayout();
		        layoutExclude.numColumns = 1;
		        compositeExclude.setLayout(layoutExclude);
		        compositeExclude.setSize(compositeExclude.computeSize(SWT.DEFAULT, SWT.DEFAULT));				
				Label lblExclude = new Label(shlFirmwareSelector, SWT.NONE);
				fd_scrolledComposite.top = new FormAttachment(lblExclude, 6);
				FormData fd_lblExclude = new FormData();
				fd_lblExclude.top = new FormAttachment(scrolledCompositeWipe, 6);
				fd_lblExclude.right = new FormAttachment(100, -11);
				fd_lblExclude.left = new FormAttachment(compositeContent, 6);
				lblExclude.setLayoutData(fd_lblExclude);
				lblExclude.setText("Exclude :");
				updateTables();
	}

	public void updateTables() {
		
		firms = new FirmwaresModel(sourceFolder.getText());
		tableFirmwareViewer.setInput(firms.firmwares);
		tableFirmwareViewer.refresh();
		tableContentViewer.setInput(firms.getFirstFirmware());
		tableContentViewer.refresh();
		tableFirmware.select(0);
	    if (tableFirmware.getSelection().length>0) {
	    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
	    	Firmware firm = (Firmware)sel.getFirstElement();
	    	result = firm.getBundle();
	    }
		btnFlash = new Button(shlFirmwareSelector, SWT.NONE);
		btnFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlFirmwareSelector.dispose();
			}
		});
		FormData fd_btnFlash = new FormData();
		fd_btnFlash.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnFlash.right = new FormAttachment(btnCancel, -6);
		btnFlash.setLayoutData(fd_btnFlash);
		btnFlash.setText("Flash");
	    updateCheckBoxes();
	}
	
	public void updateCheckBoxes() {
		Control[] ctl = compositeWipe.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		ctl = compositeExclude.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		ctl = compositeMisc.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		compositeMisc.setSize(compositeMisc.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeMisc.layout();
		compositeWipe.setSize(compositeWipe.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeWipe.layout();
		compositeExclude.setSize(compositeExclude.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeExclude.layout();
		if (!tableFirmwareViewer.getSelection().isEmpty()) {
			IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
	    	Firmware firm = (Firmware)sel.getFirstElement();
			
			Iterator<Category> exclude = result.getMeta().getExclude().iterator();
	    	while (exclude.hasNext()) {
				Category categ = exclude.next();
				if (!(categ.isSin() || categ.isBootDelivery())) continue; 
				Button btnExclude = new Button(compositeExclude, SWT.CHECK);
				btnExclude.setText(categ.getId());
				btnExclude.setSelection(!categ.isEnabled());
				compositeExclude.setSize(compositeExclude.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				compositeExclude.layout();
				btnExclude.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
				    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
				    	Firmware firm = (Firmware)sel.getFirstElement();
						Button b = (Button)e.widget;
				    	if (b.getSelection()) firm.disableCateg(b.getText());
				    	else firm.enableCateg(b.getText());
						tableContentViewer.setInput(firm);
						tableContentViewer.refresh();
					}
				});
	    	}

	    	Iterator<Category> wipe = result.getMeta().getWipe().iterator();
	    	while (wipe.hasNext()) {
				Category categ = wipe.next();
				Button btnWipe = new Button(compositeWipe, SWT.CHECK);
				btnWipe.setText(categ.getId());
				btnWipe.setSelection(categ.isEnabled());
				compositeWipe.setSize(compositeWipe.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				compositeWipe.layout();
				btnWipe.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button)e.widget;
				    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
				    	Firmware firm = (Firmware)sel.getFirstElement();
				    	if (b.getSelection()) firm.enableCateg(b.getText());
				    	else firm.disableCateg(b.getText());
						tableContentViewer.setInput(firm);
						tableContentViewer.refresh();
					}
				});
	    	}
	    	
			Iterator<Category> miscta = result.getMeta().getExclude().iterator();
	    	while (miscta.hasNext()) {
				Category categ = miscta.next();
				if (!categ.isTa()) continue; 
				Button btnMisc = new Button(compositeMisc, SWT.CHECK);
				btnMisc.setText(categ.getId());
				btnMisc.setSelection(!categ.isEnabled());
				compositeMisc.setSize(compositeMisc.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				compositeMisc.layout();
				btnMisc.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
				    	IStructuredSelection sel = (IStructuredSelection) tableFirmwareViewer.getSelection();
				    	Firmware firm = (Firmware)sel.getFirstElement();
						Button b = (Button)e.widget;
				    	if (b.getSelection()) firm.disableCateg(b.getText());
				    	else firm.enableCateg(b.getText());
						tableContentViewer.setInput(firm);
						tableContentViewer.refresh();
					}
				});
	    	}
		}
	}
}