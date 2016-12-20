package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import flashsystem.Bundle;
import flashsystem.Category;
import gui.models.ContentContentProvider;
import gui.models.ContentLabelProvider;
import gui.models.Firmware;
import gui.models.MyTreeContentProvider;
import gui.models.MyTreeLabelProvider;
import gui.models.TreeDeviceCustomizationRelease;
import gui.models.TreeDevices;
import gui.tools.WidgetTask;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.system.DeviceEntry;
import org.system.GlobalConfig;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Table;

import java.util.Iterator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FTFSelector extends Dialog {

	protected Bundle result;
	protected Shell shlFirmwareSelector;
	private boolean simulate = false;
	private int maxbuffer = 0;
	private Text textSourceFolder;
	private Button btnSourcefolder;
	private Composite compositeSearch;
	private Composite compositeSettings;
	private Button btnCheckSimulate;
	private Button btnCheckFinal;
	private Label lblUSBBuffer;
	private Combo comboUSBBuffer;
	private Text textFilter;
	private Label lblFirmwares;
	private Table tableContent;
	private TableViewer tableViewerContent;
	private Button btnCancel;
	private Button btnFlash;
	private TreeDevices firms=null;
	private TreeViewer treeViewerFirmwares;
	private Tree treeFirmwares;
	private Firmware currentfirm=null;
	private Composite compositeWipeContent;
	private Composite compositeWipeTA;
	private Composite compositeExcludeContent;
	private Composite compositeExcludeTA;
	private Button btnFilter;
	private FormData fd_compositeExclude;
	
	public FTFSelector(Shell parent, int style) {
		super(parent, style);
		setText("Firmware selector");
	}

	public Object open(String pathname, String ftfname) {

		createContents();
		
		btnFlash = new Button(shlFirmwareSelector, SWT.NONE);
		FormData fd_btnFlash = new FormData();
		
		fd_btnFlash.bottom = new FormAttachment(100, -10);
		btnFlash.setLayoutData(fd_btnFlash);
		btnFlash.setText("Flash");
		
		btnCancel = new Button(shlFirmwareSelector, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnFlash.right = new FormAttachment(btnCancel, -6);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		feedContent(pathname, ftfname);
		setProviders();
		setListeners();
		updateContent(true);

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

	private void createContents() {
		
		// Creation du Shell
		shlFirmwareSelector = new Shell(getParent(), getStyle());
		shlFirmwareSelector.setSize(714, 497);
		shlFirmwareSelector.setText("Firmware Selector");
		shlFirmwareSelector.setLayout(new FormLayout());
		
		// Search bar
		compositeSearch = new Composite(shlFirmwareSelector, SWT.NONE);
		FormData fd_compositeSearch = new FormData();
		fd_compositeSearch.left = new FormAttachment(0, 10);
		fd_compositeSearch.right = new FormAttachment(100, -10);
		fd_compositeSearch.top = new FormAttachment(0, 10);
		fd_compositeSearch.bottom = new FormAttachment(0, 45);
		compositeSearch.setLayoutData(fd_compositeSearch);
		compositeSearch.setLayout(new GridLayout(3, false));
		
		Label lblSourceFolder = new Label(compositeSearch, SWT.NONE);
		lblSourceFolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSourceFolder.setText("Source folder : ");
		
		textSourceFolder = new Text(compositeSearch, SWT.BORDER);
		textSourceFolder.setEditable(false);
		GridData gd_textSourceFolder = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_textSourceFolder.widthHint = 517;
		textSourceFolder.setLayoutData(gd_textSourceFolder);
		
		btnSourcefolder = new Button(compositeSearch, SWT.NONE);
		btnSourcefolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		btnSourcefolder.setText(" ... ");
		
		// Settings bar
		compositeSettings = new Composite(shlFirmwareSelector, SWT.NONE);
		FormData fd_compositeSettings = new FormData();
		fd_compositeSettings.right = new FormAttachment(100, -197);
		fd_compositeSettings.top = new FormAttachment(0, 422);
		fd_compositeSettings.left = new FormAttachment(0, 10);
		compositeSettings.setLayoutData(fd_compositeSettings);
		compositeSettings.setLayout(new GridLayout(6, false));
		
		
		btnCheckSimulate = new Button(compositeSettings, SWT.CHECK);
		btnCheckSimulate.setText("Simulate");
		new Label(compositeSettings, SWT.NONE);
		
		btnCheckFinal = new Button(compositeSettings, SWT.CHECK);
		btnCheckFinal.setText("Disable final verification");
		new Label(compositeSettings, SWT.NONE);
		
		lblUSBBuffer = new Label(compositeSettings, SWT.NONE);
		lblUSBBuffer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUSBBuffer.setText("USB buffer");
		
		comboUSBBuffer = new Combo(compositeSettings, SWT.READ_ONLY);
		comboUSBBuffer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Composite compositeFirmwares = new Composite(shlFirmwareSelector, SWT.NONE);
		compositeFirmwares.setLayout(new GridLayout(1, false));
		FormData fd_compositeFirmwares = new FormData();
		fd_compositeFirmwares.bottom = new FormAttachment(compositeSettings, -6);
		fd_compositeFirmwares.left = new FormAttachment(0, 10);
		compositeFirmwares.setLayoutData(fd_compositeFirmwares);
		
		Composite compositeFilter = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeFirmwares.top = new FormAttachment(compositeFilter, 6);
		
		lblFirmwares = new Label(compositeFirmwares, SWT.NONE);
		lblFirmwares.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblFirmwares.setText("Firmwares");
		
		treeViewerFirmwares = new TreeViewer(compositeFirmwares, SWT.BORDER);
		treeFirmwares = treeViewerFirmwares.getTree();
		GridData gd_treeFirmwares = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_treeFirmwares.widthHint = 118;
		treeFirmwares.setLayoutData(gd_treeFirmwares);
		compositeFilter.setLayout(new GridLayout(3, false));
		FormData fd_compositeFilter = new FormData();
		fd_compositeFilter.top = new FormAttachment(compositeSearch, 6);
		fd_compositeFilter.right = new FormAttachment(100, -182);
		fd_compositeFilter.left = new FormAttachment(0, 182);
		compositeFilter.setLayoutData(fd_compositeFilter);
		
		Label lblFilter = new Label(compositeFilter, SWT.NONE);
		lblFilter.setText("Device filter : ");
		
		textFilter = new Text(compositeFilter, SWT.BORDER);
		textFilter.setEditable(false);
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnFilter = new Button(compositeFilter, SWT.NONE);
		btnFilter.setText("Clear filter");
		
		Composite compositeContent = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeFirmwares.right = new FormAttachment(compositeContent, -6);
		compositeContent.setLayout(new GridLayout(1, false));
		FormData fd_compositeContent = new FormData();
		fd_compositeContent.bottom = new FormAttachment(compositeSettings, -6);
		fd_compositeContent.top = new FormAttachment(compositeFilter, 6);
		fd_compositeContent.left = new FormAttachment(0, 225);
		compositeContent.setLayoutData(fd_compositeContent);
		
		Label lblContent = new Label(compositeContent, SWT.NONE);
		lblContent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblContent.setText("Content");
		
		tableViewerContent = new TableViewer(compositeContent, SWT.BORDER | SWT.FULL_SELECTION);
		tableContent = tableViewerContent.getTable();
		tableContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositeWipe = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeContent.right = new FormAttachment(compositeWipe, -6);
		compositeWipe.setLayout(new GridLayout(1, false));
		FormData fd_compositeWipe = new FormData();
		fd_compositeWipe.bottom = new FormAttachment(compositeSettings, -6);
		fd_compositeWipe.top = new FormAttachment(compositeFilter, 6);
		fd_compositeWipe.left = new FormAttachment(0, 401);
		compositeWipe.setLayoutData(fd_compositeWipe);
		
		Label lblWipe = new Label(compositeWipe, SWT.NONE);
		lblWipe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblWipe.setText("Wipe");
		
		Composite compositeExclude = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeWipe.right = new FormAttachment(compositeExclude, -6);
		
		Label lblNewLabel = new Label(compositeWipe, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Sin");
		
		ScrolledComposite scrolledCompositeWipe = new ScrolledComposite(compositeWipe, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledCompositeWipe = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_scrolledCompositeWipe.heightHint = 100;
		scrolledCompositeWipe.setLayoutData(gd_scrolledCompositeWipe);
		
		compositeWipeContent = new Composite(scrolledCompositeWipe, SWT.NONE);
		compositeWipeContent.setLayout(new GridLayout(1, false));
		compositeWipeContent.setSize(compositeWipe.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		scrolledCompositeWipe.setContent(compositeWipeContent);
		
		Label lblWipeMiscTA = new Label(compositeWipe, SWT.NONE);
		lblWipeMiscTA.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblWipeMiscTA.setText("Misc TA");
		
		ScrolledComposite scrolledCompositeWipeTA = new ScrolledComposite(compositeWipe, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledCompositeWipeTA = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_scrolledCompositeWipeTA.heightHint = 80;
		scrolledCompositeWipeTA.setLayoutData(gd_scrolledCompositeWipeTA);
		
		compositeWipeTA = new Composite(scrolledCompositeWipeTA, SWT.NONE);
		compositeWipeTA.setLayout(new GridLayout(1, false));
		scrolledCompositeWipeTA.setContent(compositeWipeTA);
		compositeExclude.setLayout(new GridLayout(1, false));
		fd_compositeExclude = new FormData();
		fd_compositeExclude.top = new FormAttachment(compositeFilter, 6);
		fd_compositeExclude.left = new FormAttachment(0, 543);
		fd_compositeExclude.right = new FormAttachment(100, -10);
		fd_compositeExclude.bottom = new FormAttachment(compositeSettings, -6);
		compositeExclude.setLayoutData(fd_compositeExclude);
		
		Label lblExclude = new Label(compositeExclude, SWT.NONE);
		lblExclude.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblExclude.setText("Exclude");
		
		Label lblNewLabel_1 = new Label(compositeExclude, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Sin");
		
		ScrolledComposite scrolledCompositeExclude = new ScrolledComposite(compositeExclude, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledCompositeExclude = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_scrolledCompositeExclude.heightHint = 100;
		scrolledCompositeExclude.setLayoutData(gd_scrolledCompositeExclude);
		
		compositeExcludeContent = new Composite(scrolledCompositeExclude, SWT.NONE);
		GridLayout layoutExclude = new GridLayout();
        layoutExclude.numColumns = 1;
        compositeExcludeContent.setLayout(layoutExclude);
        compositeExcludeContent.setSize(compositeExcludeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));				
		compositeExcludeContent.setSize(compositeExcludeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledCompositeExclude.setContent(compositeExcludeContent);
		
		Label lblNewLabel_2 = new Label(compositeExclude, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Misc TA");
		
		ScrolledComposite scrolledCompositeExcludeTA = new ScrolledComposite(compositeExclude, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledCompositeExcludeTA = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_scrolledCompositeExcludeTA.heightHint = 80;
		scrolledCompositeExcludeTA.setLayoutData(gd_scrolledCompositeExcludeTA);
		
		compositeExcludeTA = new Composite(scrolledCompositeExcludeTA, SWT.NONE);
		compositeExcludeTA.setLayout(new GridLayout(1, false));
		scrolledCompositeExcludeTA.setContent(compositeExcludeTA);

	}

	public void feedContent(String pathname, String ftfname) {
		textSourceFolder.setText(pathname);
		comboUSBBuffer.setItems(new String[] {"Device maxsize", "512K", "256K", "128K", "64K", "32K"});
		comboUSBBuffer.select(1);
		maxbuffer=comboUSBBuffer.getSelectionIndex();
	}
	
	public void updateContent(boolean folderchange) {
		if (folderchange) {
			firms = new TreeDevices(textSourceFolder.getText());
			textFilter.setText("");
			treeViewerFirmwares.setInput(firms);
			btnFlash.setEnabled(false);
			btnCheckFinal.setEnabled(false);
			btnCheckFinal.setSelection(false);
		}
		treeViewerFirmwares.refresh();
		tableViewerContent.setInput(currentfirm);
		tableViewerContent.refresh();
		updateCheckBoxes();
	}

	public void updateCheckBoxes() {
		Control[] ctl = compositeWipeContent.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		ctl = compositeExcludeContent.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		ctl = compositeWipeTA.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}
		ctl = compositeExcludeTA.getChildren();
		for (int i = 0;i<ctl.length;i++) {
			ctl[i].dispose();
		}

		compositeWipeContent.setSize(compositeWipeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeWipeContent.layout();
		compositeWipeTA.setSize(compositeWipeTA.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeWipeTA.layout();

		compositeExcludeContent.setSize(compositeExcludeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeExcludeContent.layout();
		compositeExcludeTA.setSize(compositeExcludeTA.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeExcludeTA.layout();

		if (currentfirm!=null) {
	
		    Iterator<Category> wipe = result.getMeta().getWipe().iterator();
		    while (wipe.hasNext()) {
				Category categ = wipe.next();
				Button btnWipe = null;
				if (categ.isTa()) {
					btnWipe = new Button(compositeWipeTA, SWT.CHECK);
				}
				else {
					btnWipe = new Button(compositeWipeContent, SWT.CHECK);
				}
				btnWipe.setText(categ.getId());
				btnWipe.setSelection(categ.isEnabled());
				if (categ.getId().equals("SIMLOCK") && GlobalConfig.getProperty("devfeatures").equals("no"))
					btnWipe.setEnabled(false);
				if (categ.isTa()) {
					compositeWipeTA.setSize(compositeWipeTA.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					compositeWipeTA.layout();
				}
				else {
					compositeWipeContent.setSize(compositeWipeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					compositeWipeContent.layout();
				}
				btnWipe.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button)e.widget;
					    if (b.getSelection()) {
					    	if (b.getText().equals("SIMLOCK")) WidgetTask.openOKBox(shlFirmwareSelector, "Including simlock can lead to loss of network");
					    	currentfirm.enableCateg(b.getText());
					    }
					    else currentfirm.disableCateg(b.getText());
					    tableViewerContent.setInput(currentfirm);
						tableViewerContent.refresh();
					}
				});
		    }
		    
			Iterator<Category> exclude = result.getMeta().getExclude().iterator();
		    while (exclude.hasNext()) {
				Category categ = exclude.next();
				//if (!(categ.isSin() || categ.isBootDelivery())) continue; 
				Button btnExclude = null;
				if (categ.isTa()) {
					btnExclude = new Button(compositeExcludeTA, SWT.CHECK);
				}
				else {
					btnExclude = new Button(compositeExcludeContent, SWT.CHECK);
				}
				btnExclude.setText(categ.getId());
				btnExclude.setSelection(!categ.isEnabled());
				compositeExcludeContent.setSize(compositeExcludeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				compositeExcludeContent.layout();
				if (categ.isTa()) {
					compositeExcludeTA.setSize(compositeExcludeTA.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					compositeExcludeTA.layout();
				}
				else {
					compositeExcludeContent.setSize(compositeExcludeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					compositeExcludeContent.layout();
				}
				btnExclude.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button b = (Button)e.widget;
					    if (b.getSelection()) currentfirm.disableCateg(b.getText());
					    else {
					    	if (b.getText().equals("SIMLOCK")) WidgetTask.openOKBox(shlFirmwareSelector, "Including simlock can lead to loss of network");
					    	currentfirm.enableCateg(b.getText());
					    }
					    tableViewerContent.setInput(currentfirm);
					    tableViewerContent.refresh();
					}
				});
		    }

		}
		tableViewerContent.setInput(currentfirm);
		tableViewerContent.refresh();
	}

	public void setProviders() {
		treeViewerFirmwares.setContentProvider(new MyTreeContentProvider());
		treeViewerFirmwares.setLabelProvider(new MyTreeLabelProvider());
		tableViewerContent.setContentProvider(new ContentContentProvider());
		tableViewerContent.setLabelProvider(new ContentLabelProvider());

	}
	public void setListeners() {
		
		shlFirmwareSelector.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
					result = null;
					shlFirmwareSelector.dispose();
		      }
		    });
		
		btnSourcefolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlFirmwareSelector);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(textSourceFolder.getText());

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
		        	if (!textSourceFolder.getText().equals(dir)) {
		        		textSourceFolder.setText(dir);
		        		updateContent(true);
		        	}
		        }
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlFirmwareSelector.dispose();
			}
		});

		btnFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlFirmwareSelector.dispose();
			}
		});
		
		treeFirmwares.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  if (treeFirmwares.getSelectionCount()==0) {
		    		  currentfirm=null;
		    		  result=null;
		    		  btnFlash.setEnabled(false);
		    		  btnCheckFinal.setSelection(false);
		    		  btnCheckFinal.setEnabled(false);
		    	  }
		    	  else {
			    	  TreeItem selection = treeFirmwares.getSelection()[0];
			    	  if (selection.getData() instanceof TreeDeviceCustomizationRelease) {
			    		  TreeDeviceCustomizationRelease rel = (TreeDeviceCustomizationRelease)selection.getData();
			    		  currentfirm=rel.getFirmware();
			    		  result=currentfirm.getBundle();
			    		  btnFlash.setEnabled(true);
			    		  btnCheckFinal.setEnabled(true);
			    		  btnCheckFinal.setSelection(result.hasCmd25());
			    	  }
			    	  else {
			    		  btnFlash.setEnabled(false);
			    		  btnCheckFinal.setEnabled(false);
			    		  btnCheckFinal.setSelection(false);
			    		  currentfirm=null;
			    		  result=null;
			    	  }
		    	  }
	    		  if (tableViewerContent.getInput()!=currentfirm)
	    			  updateContent(false);

		      }
		    });

		textFilter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				String res = WidgetTask.openDeviceSelector(shlFirmwareSelector);
				if (res.length()>0) {
					DeviceEntry ent = new DeviceEntry(res);
					textFilter.setText(ent.getName());
					firms.setDeviceFilter(ent.getId());
					updateContent(false);
				}
			}
		});

		comboUSBBuffer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				maxbuffer=comboUSBBuffer.getSelectionIndex();
			}
		});

		btnCheckSimulate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				simulate = btnCheckSimulate.getSelection();
			}
		});

		btnFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (textFilter.getText().length()>0) {
					textFilter.setText("");
					firms.setDeviceFilter("");
					updateContent(false);
				}
			}
		});

		btnCheckFinal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckFinal.getSelection()) {
					WidgetTask.openOKBox(shlFirmwareSelector, "Warning, this option will not be used if a FSC script is found");
				}
				if (result!=null)
					result.setCmd25(btnCheckFinal.getSelection()?"true":"false");
			}
		});

	}
}
