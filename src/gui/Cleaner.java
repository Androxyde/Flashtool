package gui;

import gui.tools.DeviceApps;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Cleaner extends Dialog {

	protected Shell shlDecruptWizard;
	ListViewer listViewerInstalled;
	ListViewer listViewerToRemove;
	ListViewer listViewerAvailable;
	ListViewer listViewerToInstall;
	Vector<String> installed = new Vector<String>();
	Vector<String> toremove = new Vector<String>();
	Vector<String> available = new Vector<String>();
	Vector<String> toinstall = new Vector<String>();
	private List listInstalled;
	private List listToRemove;
	private Button btnCancel;
	private Composite compositeButtongroup1;
	DeviceApps apps;
	Combo comboProfile;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Cleaner(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public DeviceApps open() {
		init();
		createContents();
		Button btnProfile = new Button(shlDecruptWizard, SWT.NONE);
		btnProfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				apps.saveProfile();
			}
		});
		FormData fd_btnProfile = new FormData();
		fd_btnProfile.bottom = new FormAttachment(100, -10);
		fd_btnProfile.left = new FormAttachment(0,10);
		btnProfile.setLayoutData(fd_btnProfile);
		btnProfile.setText("Save profile");
		
		Button btnSaveAsNew = new Button(shlDecruptWizard, SWT.NONE);
		btnSaveAsNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProfileSave save = new ProfileSave(shlDecruptWizard,SWT.PRIMARY_MODAL | SWT.SHEET);
				save.open(apps);
				comboProfile.removeAll();
				Iterator<String> itprofiles = apps.getProfiles().iterator();
				while (itprofiles.hasNext()) {
					comboProfile.add(itprofiles.next());
				}
				comboProfile.select(comboProfile.indexOf(apps.getCurrentProfile()));
				init();
			}
		});
		FormData fd_btnSaveAsNew = new FormData();
		fd_btnSaveAsNew.left = new FormAttachment(btnProfile, 6);
		fd_btnSaveAsNew.bottom = new FormAttachment(100,-10);
		btnSaveAsNew.setLayoutData(fd_btnSaveAsNew);
		btnSaveAsNew.setText("Save as new profile");
		shlDecruptWizard.open();
		shlDecruptWizard.layout();
		Display display = getParent().getDisplay();
		while (!shlDecruptWizard.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return apps;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlDecruptWizard = new Shell(getParent(), getStyle());
		shlDecruptWizard.setSize(539, 497);
		shlDecruptWizard.setText("ROM Cleaner");
		shlDecruptWizard.setLayout(new FormLayout());
		
		listViewerInstalled = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listInstalled = listViewerInstalled.getList();
		listViewerInstalled.setSorter(new ViewerSorter());
		FormData fd_listInstalled = new FormData();
		fd_listInstalled.bottom = new FormAttachment(0, 229);
		fd_listInstalled.right = new FormAttachment(0, 223);
		fd_listInstalled.top = new FormAttachment(0, 71);
		fd_listInstalled.left = new FormAttachment(0, 10);
		listInstalled.setLayoutData(fd_listInstalled);

	    listViewerInstalled.setContentProvider(new IStructuredContentProvider() {
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
	    listViewerInstalled.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return (String)element;
	        }
	      });

		Label lblInstalled = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lblInstalled = new FormData();
		fd_lblInstalled.right = new FormAttachment(0, 173);
		fd_lblInstalled.top = new FormAttachment(0, 51);
		fd_lblInstalled.left = new FormAttachment(0, 10);
		lblInstalled.setLayoutData(fd_lblInstalled);
		lblInstalled.setText("Installed on device :");
		
		listViewerToRemove = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL);
		listViewerToRemove.setSorter(new ViewerSorter());
		listToRemove = listViewerToRemove.getList();
		FormData fd_listToRemove = new FormData();
		fd_listToRemove.bottom = new FormAttachment(listInstalled, 0, SWT.BOTTOM);
		fd_listToRemove.top = new FormAttachment(listInstalled, 0, SWT.TOP);
		fd_listToRemove.right = new FormAttachment(0, 522);
		fd_listToRemove.left = new FormAttachment(0, 282);
		listToRemove.setLayoutData(fd_listToRemove);

		listViewerToRemove.setContentProvider(new IStructuredContentProvider() {
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
	    
		listViewerToRemove.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return (String)element;
	        }
	      });
		
		Label lbltoremove = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lbltoremove = new FormData();
		fd_lbltoremove.right = new FormAttachment(0, 415);
		fd_lbltoremove.top = new FormAttachment(0, 51);
		fd_lbltoremove.left = new FormAttachment(0, 282);
		lbltoremove.setLayoutData(fd_lbltoremove);
		lbltoremove.setText("To be removed :");
		
		btnCancel = new Button(shlDecruptWizard, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100,-10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				apps=null;
				shlDecruptWizard.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		Button btnClean = new Button(shlDecruptWizard, SWT.NONE);
		FormData fd_btnClean = new FormData();
		fd_btnClean.bottom = new FormAttachment(100, -10);
		fd_btnClean.right = new FormAttachment(btnCancel, -6);
		btnClean.setLayoutData(fd_btnClean);
		btnClean.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				apps.saveProfile();
				shlDecruptWizard.dispose();
			}
		});
		btnClean.setText("Clean");
		
		Composite compositeProfile = new Composite(shlDecruptWizard, SWT.NONE);
		compositeProfile.setLayout(new GridLayout(2, false));
		FormData fd_compositeProfile = new FormData();
		fd_compositeProfile.bottom = new FormAttachment(lblInstalled, -6);
		fd_compositeProfile.top = new FormAttachment(0, 10);
		fd_compositeProfile.left = new FormAttachment(listInstalled, 0, SWT.LEFT);
		fd_compositeProfile.right = new FormAttachment(listToRemove, 0, SWT.RIGHT);
		compositeProfile.setLayoutData(fd_compositeProfile);
		
		Label lblProfile = new Label(compositeProfile, SWT.NONE);
		GridData gd_lblProfile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblProfile.widthHint = 92;
		lblProfile.setLayoutData(gd_lblProfile);
		lblProfile.setText("Profile :");
		
		comboProfile = new Combo(compositeProfile, SWT.READ_ONLY);
		comboProfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!apps.getCurrentProfile().equals(comboProfile.getText())) {
					apps.setProfile(comboProfile.getText());
					init();
					listViewerInstalled.refresh();
					listViewerToRemove.refresh();
					listViewerToInstall.refresh();
					listViewerAvailable.refresh();
				}
			}
		});
		comboProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboProfile.setText("default");
		
		Iterator<String> itprofiles = apps.getProfiles().iterator();
		while (itprofiles.hasNext()) {
			comboProfile.add(itprofiles.next());
		}
		comboProfile.select(comboProfile.indexOf("default"));


		
		compositeButtongroup1 = new Composite(shlDecruptWizard, SWT.NONE);
		compositeButtongroup1.setLayout(new GridLayout(1, false));
		FormData fd_compositeButtongroup1 = new FormData();
		fd_compositeButtongroup1.bottom = new FormAttachment(listInstalled, -36, SWT.BOTTOM);
		fd_compositeButtongroup1.top = new FormAttachment(compositeProfile, 61);
		fd_compositeButtongroup1.right = new FormAttachment(listToRemove, -6);
		fd_compositeButtongroup1.left = new FormAttachment(listInstalled, 5);
		compositeButtongroup1.setLayoutData(fd_compositeButtongroup1);
		
		Button btnAddToRemove = new Button(compositeButtongroup1, SWT.NONE);
		GridData gd_btnAddToRemove = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnAddToRemove.heightHint = 26;
		gd_btnAddToRemove.widthHint = 40;
		btnAddToRemove.setLayoutData(gd_btnAddToRemove);
		btnAddToRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerInstalled.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					String f = (String)i.next();
					installed.remove(f);
					toremove.add(f);
					apps.setSafe(apps.getApkName(f));
					listViewerInstalled.refresh();
					listViewerToRemove.refresh();
				}
			}
		});
		btnAddToRemove.setText("->");
		new Label(compositeButtongroup1, SWT.NONE);
		
		Button btnAddToInstalled = new Button(compositeButtongroup1, SWT.NONE);
		GridData gd_btnAddToInstalled = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnAddToInstalled.heightHint = 26;
		gd_btnAddToInstalled.widthHint = 40;
		btnAddToInstalled.setLayoutData(gd_btnAddToInstalled);
		btnAddToInstalled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerToRemove.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					String f = (String)i.next();
					toremove.remove(f);
					installed.add(f);
					apps.setUnsafe(apps.getApkName(f));
					listViewerInstalled.refresh();
					listViewerToRemove.refresh();
				}
			}
		});
		btnAddToInstalled.setText("<-");
		Label lblAvailable = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lblAvailable = new FormData();
		fd_lblAvailable.top = new FormAttachment(listInstalled, 6);
		fd_lblAvailable.left = new FormAttachment(0, 10);
		lblAvailable.setLayoutData(fd_lblAvailable);
		lblAvailable.setText("Available for installation :");
		
		Label lblToInstall = new Label(shlDecruptWizard, SWT.NONE);
		FormData fd_lblToInstall = new FormData();
		fd_lblToInstall.top = new FormAttachment(listToRemove, 6);
		fd_lblToInstall.left = new FormAttachment(listToRemove, 0, SWT.LEFT);
		lblToInstall.setLayoutData(fd_lblToInstall);
		lblToInstall.setText("To be installed :");
		
		listViewerAvailable = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL);
		listViewerAvailable.setSorter(new ViewerSorter());
		List listAvailable = listViewerAvailable.getList();
		FormData fd_listAvailable = new FormData();
		fd_listAvailable.top = new FormAttachment(lblAvailable, 6);
		fd_listAvailable.right = new FormAttachment(listInstalled, 0, SWT.RIGHT);
		fd_listAvailable.left = new FormAttachment(0, 10);
		listAvailable.setLayoutData(fd_listAvailable);
	    listViewerAvailable.setContentProvider(new IStructuredContentProvider() {
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
	    
	    listViewerAvailable.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return (String)element;
	        }
	      });
		
		listViewerToInstall = new ListViewer(shlDecruptWizard, SWT.BORDER | SWT.V_SCROLL);
		listViewerToInstall.setSorter(new ViewerSorter());
		List listToInstall = listViewerToInstall.getList();
		fd_listAvailable.bottom = new FormAttachment(listToInstall, 0, SWT.BOTTOM);
		FormData fd_listToInstall = new FormData();
		fd_listToInstall.bottom = new FormAttachment(btnCancel, -6);
		fd_listToInstall.top = new FormAttachment(lblToInstall, 6);
		fd_listToInstall.left = new FormAttachment(listToRemove, 0, SWT.LEFT);
		fd_listToInstall.right = new FormAttachment(100, -11);
		listToInstall.setLayoutData(fd_listToInstall);
		
	    listViewerToInstall.setContentProvider(new IStructuredContentProvider() {
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
	    
	    listViewerToInstall.setLabelProvider(new LabelProvider() {
	        public Image getImage(Object element) {
	          return null;
	        }
	   
	        public String getText(Object element) {
	          return (String)element;
	        }
	      });
		
		Composite compositeButtongroup2 = new Composite(shlDecruptWizard, SWT.NONE);
		compositeButtongroup2.setLayout(new GridLayout(1, false));
		FormData fd_compositeButtongroup2 = new FormData();
		fd_compositeButtongroup2.bottom = new FormAttachment(100, -89);
		fd_compositeButtongroup2.right = new FormAttachment(compositeButtongroup1, 0, SWT.RIGHT);
		compositeButtongroup2.setLayoutData(fd_compositeButtongroup2);
		
		Button btnAddToBeInstalled = new Button(compositeButtongroup2, SWT.NONE);
		GridData gd_btnAddToBeInstalled = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnAddToBeInstalled.widthHint = 40;
		gd_btnAddToBeInstalled.heightHint = 26;
		btnAddToBeInstalled.setLayoutData(gd_btnAddToBeInstalled);
		btnAddToBeInstalled.setText("->");
		new Label(compositeProfile, SWT.NONE);
		btnAddToBeInstalled.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerAvailable.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					String f = (String)i.next();
					toinstall.add(f);
					available.remove(f);
					apps.setUnsafe(apps.getApkName(f));
					listViewerToInstall.refresh();
					listViewerAvailable.refresh();
				}
			}
		});
		
		Button btnAddToAvailable = new Button(compositeButtongroup2, SWT.NONE);
		GridData gd_btnAddToAvailable = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnAddToAvailable.widthHint = 40;
		gd_btnAddToAvailable.heightHint = 26;
		btnAddToAvailable.setLayoutData(gd_btnAddToAvailable);
		btnAddToAvailable.setText("<-");
		new Label(compositeProfile, SWT.NONE);
		btnAddToAvailable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerToInstall.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					String f = (String)i.next();
					toinstall.remove(f);
					available.add(f);
					apps.setSafe(apps.getApkName(f));
					listViewerToInstall.refresh();
					listViewerAvailable.refresh();
				}
			}
		});
		listViewerInstalled.setInput(installed);
		listViewerToRemove.setInput(toremove);
		listViewerToInstall.setInput(toinstall);
		listViewerAvailable.setInput(available);
	}
	
	public void init() {
		if (apps==null)
			apps = new DeviceApps();
		Enumeration<String> e1 = apps.getInstalled(false).elements();
		installed.clear();
		toremove.clear();
		toinstall.clear();
		available.clear();
		while (e1.hasMoreElements()) {
			String elem = e1.nextElement();
			installed.add(elem);
//			ListItem li = new ListItem(elem, apps.getRealName(elem),Color.black,Color.white);
//			listInstalledModel.addElement(li);
		}
		e1 = apps.getToBeRemoved(false).elements();
		while (e1.hasMoreElements()) {
			String elem = e1.nextElement();
			toremove.add(elem);
//			ListItem li = new ListItem(elem, apps.getRealName(elem),Color.black,Color.white);
//			listInstalledModel.addElement(li);
		}
		e1 = apps.getToBeInstalled(false).elements();
		while (e1.hasMoreElements()) {
			String elem = e1.nextElement();
			toinstall.add(elem);
//			ListItem li = new ListItem(elem, apps.getRealName(elem),Color.black,Color.white);
//			listInstalledModel.addElement(li);
		}
		e1 = apps.getRemoved(false).elements();
		while (e1.hasMoreElements()) {
			String elem = e1.nextElement();
			available.add(elem);
//			ListItem li = new ListItem(elem, apps.getRealName(elem),Color.black,Color.white);
//			listInstalledModel.addElement(li);
		}
	}
}
