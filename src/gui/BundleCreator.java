package gui;

import flashsystem.Bundle;
import flashsystem.BundleMetaData;
import gui.models.CategoriesContentProvider;
import gui.models.CategoriesModel;
import gui.models.Category;
import gui.models.SinfilesLabelProvider;
import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;
import gui.tools.createFTFJob;

import java.io.File;
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
import org.system.DeviceEntry;
import org.system.OS;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class BundleCreator extends Dialog {

	protected Object result = new String("Create");
	protected Shell shlBundler;
	private Text sourceFolder;
	private Text device;
	private String deviceId="";
	private Text branding;
	private Text version;
	Vector files = new Vector();
	ListViewer listViewerFiles;
	private Label lblSelectSourceFolder;
	private Button btnNewButton;
	private Label lblNewLabel_2;
	private List list;
	private Label lblNewLabel;
	private FormData fd_btnNewButton_1;
	private Button btnNewButton_1;
	private Composite composite_5;
	private BundleMetaData meta = new BundleMetaData();
	private CategoriesModel model = new CategoriesModel(meta);
	TreeViewer treeViewerCategories;
	Button btnNoFinalVerification;
	private Label lblNewLabel_3;
	private FormData fd_lblNewLabel;

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
		WidgetsTool.setSize(shlBundler);
		
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
		WidgetsTool.setSize(shlBundler);
		sourceFolder.setText(folder);
		meta.clear();
		files = new Vector();
		File srcdir = new File(sourceFolder.getText());
		File[] chld = srcdir.listFiles();
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA") && !chld[i].getName().toUpperCase().contains("SIMLOCK")) || (chld[i].getName().toUpperCase().endsWith("XML") && !chld[i].getName().toUpperCase().contains("UPDATE"))) {
				files.add(chld[i]);
			}
		}
		srcdir = new File(sourceFolder.getText()+File.separator+"boot");
		if (srcdir.exists()) {
			chld = srcdir.listFiles();
			for(int i = 0; i < chld.length; i++) {
				if (chld[i].getName().toUpperCase().endsWith("XML")) {
					files.add(chld[i]);
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
		shlBundler.setSize(626, 447);
		shlBundler.setText("Bundler");
		shlBundler.setLayout(new FormLayout());
		
		listViewerFiles = new ListViewer(shlBundler, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list = listViewerFiles.getList();
		FormData fd_list = new FormData();
		fd_list.left = new FormAttachment(0, 10);
		list.setLayoutData(fd_list);
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
	          return ((File)element).getName();
	        }
	      });
	    listViewerFiles.setSorter(new ViewerSorter(){
	        public int compare(Viewer viewer, Object e1, Object e2) {
	          return ((File)e1).getName().compareTo(((File)e2).getName());
	        }

	      });
		lblNewLabel_3 = new Label(shlBundler, SWT.NONE);
		fd_list.top = new FormAttachment(lblNewLabel_3, 6);
		fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel);
		lblNewLabel_3.setText("folder list :");
		
		composite_5 = new Composite(shlBundler, SWT.NONE);
		fd_list.bottom = new FormAttachment(composite_5, 0, SWT.BOTTOM);
		composite_5.setLayout(new TreeColumnLayout());
		FormData fd_composite_5 = new FormData();
		fd_composite_5.top = new FormAttachment(lblNewLabel_3, 6);
		fd_composite_5.right = new FormAttachment(100, -10);
		composite_5.setLayoutData(fd_composite_5);
		
		treeViewerCategories = new TreeViewer(composite_5, SWT.BORDER | SWT.MULTI);
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
		    		return ((Category)e1).getName().compareTo(((Category)e2).getName());
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
		fd_composite_5.bottom = new FormAttachment(btnCancel, -5);
		fd_composite_5.bottom = new FormAttachment(btnCancel, -5);
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
				File f = new File(OS.getWorkDir()+File.separator+"firmwares"+File.separator+deviceId+"_"+version.getText()+"_"+branding.getText()+".ftf");
				if (f.exists()) {
					showErrorMessageBox("This bundle name already exists");
					return;
				}
				Bundle b = new Bundle();
				b.setMeta(meta);
				b.setDevice(deviceId);
				b.setVersion(version.getText());
				b.setBranding(branding.getText());
				b.setCmd25(btnNoFinalVerification.getSelection()?"true":"false");
				if (!b.hasLoader()) {
					String result = WidgetTask.openDeviceSelector(shlBundler);
					if (result.length()>0) {
						DeviceEntry ent = new DeviceEntry(result);
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
					else {
						showErrorMessageBox("This bundle must contain a loader");
						return;						
					}
				}
				createFTFJob j = new createFTFJob("Create FTF");
				j.setBundle(b);
				j.schedule();
				shlBundler.dispose();
			}
		});
		FormData fd_btnCreate = new FormData();
		fd_btnCreate.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnCreate.right = new FormAttachment(btnCancel, -6);
		btnCreate.setLayoutData(fd_btnCreate);
		btnCreate.setText("Create");
		
		btnNewButton_1 = new Button(shlBundler, SWT.NONE);
		fd_list.right = new FormAttachment(btnNewButton_1, -6);
		fd_composite_5.left = new FormAttachment(btnNewButton_1, 6);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerFiles.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					File f = (File)i.next();
					files.remove(f);
					try {
						meta.process(f.getName(), f.getAbsolutePath());
						model.refresh(meta);
						treeViewerCategories.setInput(model);
					} catch (Exception ex) {ex.printStackTrace();}
					treeViewerCategories.setAutoExpandLevel(2);
					treeViewerCategories.refresh();
					listViewerFiles.refresh();
				}
			}
		});
		fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.left = new FormAttachment(0, 353);
		fd_btnNewButton_1.right = new FormAttachment(100, -224);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("->");
		
		Button btnNewButton_2 = new Button(shlBundler, SWT.NONE);
		fd_btnNewButton_1.bottom = new FormAttachment(100, -143);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)treeViewerCategories.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					Object o = i.next();
					if (o instanceof Category) {
						Category c = (Category)o;
						Iterator<File> j = c.getSinfiles().iterator();
						while (j.hasNext()) {
							File f=j.next();
							files.add(f);
							meta.remove(f.getName());
							model.refresh(meta);
							treeViewerCategories.setAutoExpandLevel(2);
							treeViewerCategories.refresh();
							listViewerFiles.refresh();
						}
					}
					if (o instanceof File) {
						String internal = ((File)o).getName();
						files.add(new File(meta.getPath(internal)));
						meta.remove(internal);
						model.refresh(meta);
						treeViewerCategories.setAutoExpandLevel(2);
						treeViewerCategories.refresh();
						listViewerFiles.refresh();
					}
				}
			}
		});
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.top = new FormAttachment(btnNewButton_1, 23);
		fd_btnNewButton_2.right = new FormAttachment(btnNewButton_1, 0, SWT.RIGHT);
		fd_btnNewButton_2.left = new FormAttachment(0, 353);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);
		btnNewButton_2.setText("<-");
		Composite composite = new Composite(shlBundler, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.top = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		lblSelectSourceFolder = new Label(composite, SWT.NONE);
		GridData gd_lblSelectSourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectSourceFolder.widthHint = 121;
		lblSelectSourceFolder.setLayoutData(gd_lblSelectSourceFolder);
		lblSelectSourceFolder.setText("Select source folder :");
		
		sourceFolder = new Text(composite, SWT.BORDER);
		sourceFolder.setEditable(false);
		GridData gd_sourceFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_sourceFolder.widthHint = 428;
		sourceFolder.setLayoutData(gd_sourceFolder);
		
		btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
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
		    				if (chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA") && !chld[i].getName().toUpperCase().contains("SIMLOCK")) || (chld[i].getName().toUpperCase().endsWith("XML") && !chld[i].getName().toUpperCase().contains("UPDATE"))) {
		    					files.add(chld[i]);
		    				}
		    			}
		    			srcdir = new File(sourceFolder.getText()+File.separator+"boot");
		    			if (srcdir.exists()) {
		    				chld = srcdir.listFiles();
			    			for(int i = 0; i < chld.length; i++) {
			    				if (chld[i].getName().toUpperCase().endsWith("XML")) {
			    					files.add(chld[i]);
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
		btnNewButton.setText("...");
		
		Composite composite_1 = new Composite(shlBundler, SWT.NONE);
		fd_lblNewLabel.top = new FormAttachment(0, 154);
		composite_1.setLayout(new GridLayout(3, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(lblNewLabel_3, -6);
		fd_composite_1.right = new FormAttachment(composite_5, 0, SWT.RIGHT);
		fd_composite_1.top = new FormAttachment(composite, 2);
		fd_composite_1.left = new FormAttachment(0, 10);
		composite_1.setLayoutData(fd_composite_1);
		
		lblNewLabel = new Label(composite_1, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 68;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Device :");
		
		device = new Text(composite_1, SWT.BORDER);
		device.setToolTipText("Double click to get list of devices");
		device.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				String result = WidgetTask.openDeviceSelector(shlBundler);
				if (result.length()>0) {
					DeviceEntry ent = new DeviceEntry(result);
					String variant = WidgetTask.openVariantSelector(ent.getId(),shlBundler);
					device.setText(ent.getName() + " ("+variant+")");
					deviceId=variant;
				}
			}
		});
		device.setEditable(false);
		GridData gd_device = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_device.widthHint = 355;
		device.setLayoutData(gd_device);
		new Label(composite_1, SWT.NONE);
		
		lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setText("Branding :");
		
		branding = new Text(composite_1, SWT.BORDER);
		GridData gd_branding = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_branding.widthHint = 355;
		branding.setLayoutData(gd_branding);
		
		btnNoFinalVerification = new Button(composite_1, SWT.CHECK);
		btnNoFinalVerification.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnNoFinalVerification.setText("No final verification");
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setText("Version :");
		
		version = new Text(composite_1, SWT.BORDER);
		GridData gd_version = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_version.widthHint = 355;
		version.setLayoutData(gd_version);
		new Label(composite_1, SWT.NONE);
		Label lblFirmwareContent = new Label(shlBundler, SWT.NONE);
		fd_lblNewLabel.right = new FormAttachment(lblFirmwareContent, -67);
		FormData fd_lblFirmwareContent = new FormData();
		fd_lblFirmwareContent.right = new FormAttachment(composite_5, 0, SWT.RIGHT);
		fd_lblFirmwareContent.bottom = new FormAttachment(composite_5, -6);
		fd_lblFirmwareContent.left = new FormAttachment(composite_5, 0, SWT.LEFT);
		lblFirmwareContent.setLayoutData(fd_lblFirmwareContent);
		lblFirmwareContent.setText("Firmware content :");

	}
	
	public void showErrorMessageBox(String message) {
		MessageBox mb = new MessageBox(shlBundler,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Errorr");
		mb.setMessage(message);
		int result = mb.open();
	}
}