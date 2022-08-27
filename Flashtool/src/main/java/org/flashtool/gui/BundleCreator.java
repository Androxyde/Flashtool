package org.flashtool.gui;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.flashtool.flashsystem.Bundle;
import org.flashtool.flashsystem.BundleEntry;
import org.flashtool.flashsystem.BundleMetaData;
import org.flashtool.flashsystem.Category;
import org.flashtool.gui.models.CategoriesContentProvider;
import org.flashtool.gui.models.CategoriesModel;
import org.flashtool.gui.models.SinfilesLabelProvider;
import org.flashtool.gui.tools.WidgetTask;
import org.flashtool.gui.tools.createFTFJob;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class BundleCreator extends Dialog {

	protected Object result = new String("Create");
	protected Shell shlBundler;
	private Text sourceFolder;
	private Text device;
	private Text branding;
	private Text version;
	Vector<BundleEntry> files = new Vector<BundleEntry>();
	ListViewer listViewerFiles;
	private Label lblSelectSourceFolder;
	private Button btnSelectFolder;
	private Label lblNewLabel_2;
	private List listFolder;
	private Label lblNewLabel;
	private FormData fd_btnToRight;
	private Button btnToRight;
	private Composite compositeFirmwareContent;
	private BundleMetaData meta = new BundleMetaData();
	private CategoriesModel model = new CategoriesModel(meta);
	TreeViewer treeViewerCategories;
	Button btnNoFinalVerification;
	private Label lblFolderList;
	private FormData fd_lblFolderList;
	private String _branding = "";
	private String _version = "";
	private String _deviceName="";
	private String _variant = "";

	public void setBranding(String lbranding) {
		_branding = lbranding;
	}
	
	public void setVersion(String lversion) {
		_version = lversion;
	}
	
	public void setVariant(String lid, String lvariant) {
		_deviceName=lid;
		_variant = lvariant;
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BundleCreator(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		
		shlBundler.open();
		shlBundler.layout();
		Display display = getParent().getDisplay();
		while (!shlBundler.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public Object open(String folder) {
		createContents();
		sourceFolder.setText(folder);
		meta.clear();
		files = new Vector<BundleEntry>();
		File srcdir = new File(sourceFolder.getText());
		File[] chld = srcdir.listFiles();
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().toUpperCase().endsWith("FSC") || chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA")) || (chld[i].getName().toUpperCase().endsWith("XML") && (!chld[i].getName().toUpperCase().contains("UPDATE") && !chld[i].getName().toUpperCase().contains("FWINFO")))) {
				files.add(new BundleEntry(chld[i]));
			}
		}
		srcdir = new File(sourceFolder.getText()+File.separator+"boot");
		if (srcdir.exists()) {
			chld = srcdir.listFiles();
			for(int i = 0; i < chld.length; i++) {
				if (chld[i].getName().toUpperCase().endsWith("XML")) {
					files.add(new BundleEntry(chld[i]));
				}
			}
		}		
		srcdir = new File(sourceFolder.getText()+File.separator+"partition");
		if (srcdir.exists()) {
			chld = srcdir.listFiles();
			for(int i = 0; i < chld.length; i++) {
				if (chld[i].getName().toUpperCase().endsWith("XML")) {
					files.add(new BundleEntry(chld[i]));
				}
			}
		}
		model.refresh(meta);
		treeViewerCategories.setInput(model);
		listViewerFiles.setInput(files);
		shlBundler.open();
		shlBundler.layout();
		Display display = getParent().getDisplay();
		while (!shlBundler.isDisposed()) {
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
		shlBundler = new Shell(getParent(), getStyle());
		shlBundler.setSize(664, 466);
		shlBundler.setText("Bundler");
		shlBundler.setLayout(new FormLayout());
		
		listViewerFiles = new ListViewer(shlBundler, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listFolder = listViewerFiles.getList();
		FormData fd_listFolder = new FormData();
		fd_listFolder.left = new FormAttachment(0, 10);
		listFolder.setLayoutData(fd_listFolder);
	    listViewerFiles.setContentProvider(new IStructuredContentProvider() {
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
	    listViewerFiles.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return ((BundleEntry)element).getName();
	        }
	      });
	    listViewerFiles.setSorter(new ViewerSorter(){
	        public int compare(Viewer viewer, Object e1, Object e2) {
	          return ((BundleEntry)e1).getName().compareTo(((BundleEntry)e2).getName());
	        }

	      });
		lblFolderList = new Label(shlBundler, SWT.NONE);
		fd_listFolder.top = new FormAttachment(lblFolderList, 6);
		fd_lblFolderList = new FormData();
		fd_lblFolderList.left = new FormAttachment(0, 10);
		lblFolderList.setLayoutData(fd_lblFolderList);
		lblFolderList.setText("folder list :");
		
		compositeFirmwareContent = new Composite(shlBundler, SWT.NONE);
		fd_listFolder.bottom = new FormAttachment(compositeFirmwareContent, 0, SWT.BOTTOM);
		compositeFirmwareContent.setLayout(new TreeColumnLayout());
		FormData fd_compositeFirmwareContent = new FormData();
		fd_compositeFirmwareContent.top = new FormAttachment(lblFolderList, 6);
		fd_compositeFirmwareContent.right = new FormAttachment(100, -10);
		compositeFirmwareContent.setLayoutData(fd_compositeFirmwareContent);
		
		treeViewerCategories = new TreeViewer(compositeFirmwareContent, SWT.BORDER | SWT.MULTI);
		Tree treeCategories = treeViewerCategories.getTree();
		treeCategories.setHeaderVisible(true);
		treeCategories.setLinesVisible(true);
		treeViewerCategories.setContentProvider(new CategoriesContentProvider());
	    treeViewerCategories.setLabelProvider(new SinfilesLabelProvider());
	    treeViewerCategories.setSorter(new ViewerSorter(){
	        public int compare(Viewer viewer, Object e1, Object e2) {
	        	int cat1 = category(e1);
	        	int cat2 = category(e2);
	        	if (cat1 != cat2) return cat1 - cat2;
		    	if ((e1 instanceof Category) && (e2 instanceof Category))
		    		return ((Category)e1).getId().compareTo(((Category)e2).getId());
		    	else
		    		return ((File)e1).getName().compareTo(((File)e2).getName());
	        }
	      });
	    // Expand the tree
	    treeViewerCategories.setAutoExpandLevel(2);
	    // Provide the input to the ContentProvider
	    treeViewerCategories.setInput(new CategoriesModel(meta));
	    treeViewerCategories.refresh();
		
		Button btnCancel = new Button(shlBundler, SWT.NONE);
		fd_compositeFirmwareContent.bottom = new FormAttachment(btnCancel, -5);
		fd_compositeFirmwareContent.bottom = new FormAttachment(btnCancel, -5);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = new String("Cancel");
				shlBundler.dispose();
			}
		});
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Button btnCreate = new Button(shlBundler, SWT.NONE);
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (sourceFolder.getText().length()==0) {
					showErrorMessageBox("You must point to a folder containing sin files");
					return;					
				}
				if ((device.getText().length()==0) || (version.getText().length()==0) || (branding.getText().length()==0)) {
					showErrorMessageBox("Device, Versio, Branding : all fields must be set");
					return;
				}
				File f = new File(OS.getFolderFirmwares()+File.separator+_variant+"_"+version.getText()+"_"+branding.getText()+".ftf");
				if (f.exists()) {
					showErrorMessageBox("This bundle name already exists");
					return;
				}
				try {
					if (f.createNewFile()) 
						f.delete();
					else {
						showErrorMessageBox("The built filename from variant, version and branding is not valid. Choose another name");
						return;
					}
				}
				catch (IOException ioe) {
					showErrorMessageBox("The built filename from variant, version and branding is not valid. Choose another name");
					return;						
				}
				Bundle b = new Bundle();
				try {
					b.setMeta(meta);
				} catch (Exception ex) {}
				b.setDevice(_variant);
				b.setVersion(version.getText());
				b.setBranding(branding.getText());
				b.setCmd25(btnNoFinalVerification.getSelection()?"true":"false");
				b.setNoErase(sourceFolder.getText()+File.separator+"update.xml");
				if (!b.hasLoader()) {
					DeviceEntry ent = Devices.getDeviceFromVariant(_variant);
						if (ent.hasUnlockedLoader()) {
							String res = WidgetTask.openLoaderSelect(shlBundler);
							if (res.equals("U"))
								b.setLoader(new File(ent.getLoaderUnlocked()));
							else
								if (res.equals("L"))
									b.setLoader(new File(ent.getLoader()));
								else {
									showErrorMessageBox("This bundle must contain a loader");
									return;
								}

						}
						else {
							b.setLoader(new File(ent.getLoader()));
						}
				}
				if (!b.hasFsc()) {
					DeviceEntry dev = Devices.getDeviceFromVariant(_variant);
			    	String fscpath = dev.getFlashScript(version.getText(), _variant);
			    	File fsc = new File(fscpath);
			    	if (fsc.exists()) {
		    			String result = WidgetTask.openYESNOBox(shlBundler, "A FSC script is found : "+fsc.getName()+". Do you want to add it ?");
		    			if (Integer.parseInt(result)==SWT.YES) {
		    				b.setFsc(fsc);
		    			}
			    	}
				}
				createFTFJob j = new createFTFJob("Create FTF");
				j.setBundle(b);
				j.schedule();
				shlBundler.dispose();
			}
		});
		FormData fd_btnCreate = new FormData();
		fd_btnCreate.bottom = new FormAttachment(100,-10);
		fd_btnCreate.right = new FormAttachment(btnCancel, -6);
		btnCreate.setLayoutData(fd_btnCreate);
		btnCreate.setText("Create");
		
		btnToRight = new Button(shlBundler, SWT.NONE);
		fd_listFolder.right = new FormAttachment(btnToRight, -6);
		fd_compositeFirmwareContent.left = new FormAttachment(btnToRight, 6);
		btnToRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerFiles.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					BundleEntry f = (BundleEntry)i.next();
					files.remove(f);
					try {
						meta.process(f);
						model.refresh(meta);
						treeViewerCategories.setInput(model);
					} catch (Exception ex) {ex.printStackTrace();}
					treeViewerCategories.setAutoExpandLevel(2);
					treeViewerCategories.refresh();
					listViewerFiles.refresh();
				}
			}
		});
		fd_btnToRight = new FormData();
		fd_btnToRight.right = new FormAttachment(100, -224);
		btnToRight.setLayoutData(fd_btnToRight);
		btnToRight.setText("->");
		
		Button btnNewToLeft = new Button(shlBundler, SWT.NONE);
		fd_btnToRight.bottom = new FormAttachment(100, -143);
		btnNewToLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)treeViewerCategories.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					Object o = i.next();
					if (o instanceof Category) {
						Category c = (Category)o;
						Iterator<BundleEntry> j = c.getEntries().iterator();
						while (j.hasNext()) {
							BundleEntry f=j.next();
							files.add(f);
							meta.remove(f);
							model.refresh(meta);
							treeViewerCategories.setAutoExpandLevel(2);
							treeViewerCategories.refresh();
							listViewerFiles.refresh();
						}
					}
					if (o instanceof BundleEntry) {
						BundleEntry f = (BundleEntry)o;
						files.add(f);
						meta.remove(f);
						model.refresh(meta);
						treeViewerCategories.setAutoExpandLevel(2);
						treeViewerCategories.refresh();
						listViewerFiles.refresh();
					}
				}
			}
		});
		FormData fd_btnNewToLeft = new FormData();
		fd_btnNewToLeft.top = new FormAttachment(btnToRight, 23);
		fd_btnNewToLeft.right = new FormAttachment(100, -224);
		btnNewToLeft.setLayoutData(fd_btnNewToLeft);
		btnNewToLeft.setText("<-");
		Composite compositeFolderSearch = new Composite(shlBundler, SWT.NONE);
		compositeFolderSearch.setLayout(new GridLayout(3, false));
		FormData fd_compositeFolderSearch = new FormData();
		fd_compositeFolderSearch.left = new FormAttachment(0, 10);
		fd_compositeFolderSearch.right = new FormAttachment(100, -10);
		fd_compositeFolderSearch.top = new FormAttachment(0, 10);
		compositeFolderSearch.setLayoutData(fd_compositeFolderSearch);
		
		lblSelectSourceFolder = new Label(compositeFolderSearch, SWT.NONE);
		GridData gd_lblSelectSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectSourceFolder.widthHint = 151;
		lblSelectSourceFolder.setLayoutData(gd_lblSelectSourceFolder);
		lblSelectSourceFolder.setText("Select source folder :");
		
		sourceFolder = new Text(compositeFolderSearch, SWT.BORDER);
		sourceFolder.setEditable(false);
		GridData gd_sourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_sourceFolder.widthHint = 415;
		sourceFolder.setLayoutData(gd_sourceFolder);
		
		btnSelectFolder = new Button(compositeFolderSearch, SWT.NONE);
		btnSelectFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSelectFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlBundler);

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
		        		meta.clear();
		        		files = new Vector();
		    			File srcdir = new File(sourceFolder.getText());
		    			File[] chld = srcdir.listFiles();
		    			for(int i = 0; i < chld.length; i++) {
		    				if (chld[i].getName().toUpperCase().endsWith("FSC") || chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA")) || (chld[i].getName().toUpperCase().endsWith("XML") && (!chld[i].getName().toUpperCase().contains("UPDATE") && !chld[i].getName().toUpperCase().contains("FWINFO")))) {
		    					files.add(new BundleEntry(chld[i]));
		    				}
		    			}
		    			srcdir = new File(sourceFolder.getText()+File.separator+"boot");
		    			if (srcdir.exists()) {
		    				chld = srcdir.listFiles();
			    			for(int i = 0; i < chld.length; i++) {
			    				if (chld[i].getName().toUpperCase().endsWith("XML")) {
			    					files.add(new BundleEntry(chld[i]));
			    				}
			    			}
		    			}
		    			srcdir = new File(sourceFolder.getText()+File.separator+"partition");
		    			if (srcdir.exists()) {
		    				chld = srcdir.listFiles();
			    			for(int i = 0; i < chld.length; i++) {
			    				if (chld[i].getName().toUpperCase().endsWith("XML")) {
			    					files.add(new BundleEntry(chld[i]));
			    				}
			    			}
		    			}
		    			model.refresh(meta);
		    			treeViewerCategories.setInput(model);
		    			listViewerFiles.setInput(files);
		        	}
		        }
			}
		});
		btnSelectFolder.setText("...");
		
		Composite compositeInfos = new Composite(shlBundler, SWT.NONE);
		fd_lblFolderList.top = new FormAttachment(compositeInfos, 6);
		compositeInfos.setLayout(new GridLayout(3, false));
		FormData fd_compositeInfos = new FormData();
		fd_compositeInfos.right = new FormAttachment(100,-10);
		fd_compositeInfos.top = new FormAttachment(compositeFolderSearch, 6);
		fd_compositeInfos.left = new FormAttachment(0, 10);
		compositeInfos.setLayoutData(fd_compositeInfos);
		
		lblNewLabel = new Label(compositeInfos, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 68;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Device :");
		
		device = new Text(compositeInfos, SWT.BORDER);
		device.setToolTipText("Double click to get list of devices");
		device.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				String result = WidgetTask.openDeviceSelector(shlBundler);
				if (result.length()>0) {
					DeviceEntry ent = new DeviceEntry(result);
					String variant = WidgetTask.openVariantSelector(ent.getId(),shlBundler);
					if (!variant.equals(ent.getId())) {
						device.setText(ent.getName() + " ("+variant+")");
						_variant=variant;
					}
				}
			}
		});
		device.setEditable(false);
		GridData gd_device = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_device.widthHint = 355;
		device.setLayoutData(gd_device);
		new Label(compositeInfos, SWT.NONE);
		
		lblNewLabel_2 = new Label(compositeInfos, SWT.NONE);
		lblNewLabel_2.setText("Branding :");
		
		branding = new Text(compositeInfos, SWT.BORDER);
		GridData gd_branding = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_branding.widthHint = 355;
		branding.setLayoutData(gd_branding);
		
		btnNoFinalVerification = new Button(compositeInfos, SWT.CHECK);
		btnNoFinalVerification.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnNoFinalVerification.setText("No final verification");
		
		Label lblNewLabel_1 = new Label(compositeInfos, SWT.NONE);
		lblNewLabel_1.setText("Version :");
		
		version = new Text(compositeInfos, SWT.BORDER);
		GridData gd_version = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_version.widthHint = 355;
		version.setLayoutData(gd_version);
		new Label(compositeInfos, SWT.NONE);
		Label lblFirmwareContent = new Label(shlBundler, SWT.NONE);
		fd_lblFolderList.right = new FormAttachment(lblFirmwareContent, -67);
		FormData fd_lblFirmwareContent = new FormData();
		fd_lblFirmwareContent.right = new FormAttachment(compositeFirmwareContent, 0, SWT.RIGHT);
		fd_lblFirmwareContent.bottom = new FormAttachment(compositeFirmwareContent, -6);
		fd_lblFirmwareContent.left = new FormAttachment(compositeFirmwareContent, 0, SWT.LEFT);
		lblFirmwareContent.setLayoutData(fd_lblFirmwareContent);
		lblFirmwareContent.setText("Firmware content :");
		
		branding.setText(_branding);
		version.setText(_version);
		if (_deviceName.length()>0)
			device.setText(_deviceName + " ("+_variant+")");

	}
	
	public void showErrorMessageBox(String message) {
		MessageBox mb = new MessageBox(shlBundler,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Errorr");
		mb.setMessage(message);
		int result = mb.open();
	}
}