package gui;

import gui.tools.WidgetTask;
import gui.tools.WidgetsTool;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.system.Devices;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class TARestore extends Dialog {

	protected Shell shlTARestore;
	ListViewer listViewerTAUnits;
	private List listTAUnits;
	private Button btnLtoR;
	private Label lblTAlist;
	private Label lblTAFlash;
	private Button btnCancel;
	private ListViewer listViewerTAUnitsToFlash;
	private Combo comboBackupset;
	private Combo comboPartition;
	private HashMap<String,Vector<TABag>> backupset = new HashMap<String, Vector<TABag>>();
	private Vector<TAUnit> available;
	private Vector<TAUnit> toflash;
	private Vector<TABag> result;

	public class TABag {
		public Vector<TAUnit> available;
		public Vector<TAUnit> toflash;
		public int partition;
		public TABag(File file) {
			try {
				TAFileParser taf = new TAFileParser(file);
				available = taf.entries();
				toflash = new Vector<TAUnit>();
				partition = taf.getPartition();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TARestore(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlTARestore);
		
		shlTARestore.open();
		shlTARestore.layout();
		Display display = getParent().getDisplay();
		while (!shlTARestore.isDisposed()) {
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
		shlTARestore = new Shell(getParent(), getStyle());
		shlTARestore.setSize(273, 427);
		shlTARestore.setText("TA Restore");
		shlTARestore.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    		result = null;
		    	  	event.doit = true;
		      }
		    });
		shlTARestore.setLayout(new FormLayout());
		Label lblBackupset = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblBackupset = new FormData();
		fd_lblBackupset.top = new FormAttachment(0, 9);
		fd_lblBackupset.left = new FormAttachment(0, 5);
		lblBackupset.setLayoutData(fd_lblBackupset);
		lblBackupset.setText("Backupset :");
		
		comboBackupset = new Combo(shlTARestore, SWT.READ_ONLY);
		FormData fd_comboBackupset = new FormData();
		fd_comboBackupset.left = new FormAttachment(lblBackupset, 35);
		fd_comboBackupset.right = new FormAttachment(100, -10);
		fd_comboBackupset.top = new FormAttachment(0, 5);
		comboBackupset.setLayoutData(fd_comboBackupset);
		comboBackupset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshPartitions();
			}
		});
		
				comboBackupset.select(0);
		
		Label lblPartition = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblPartition = new FormData();
		fd_lblPartition.top = new FormAttachment(0, 37);
		fd_lblPartition.left = new FormAttachment(0, 5);
		lblPartition.setLayoutData(fd_lblPartition);
		lblPartition.setText("Partition : ");
		
		comboPartition = new Combo(shlTARestore, SWT.READ_ONLY);
		FormData fd_comboPartition = new FormData();
		fd_comboPartition.top = new FormAttachment(0, 33);
		fd_comboPartition.left = new FormAttachment(0, 100);
		comboPartition.setLayoutData(fd_comboPartition);
		comboPartition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshUnits();
			}
		});
		lblTAlist = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAlist = new FormData();
		fd_lblTAlist.left = new FormAttachment(lblBackupset, 0, SWT.LEFT);
		lblTAlist.setLayoutData(fd_lblTAlist);
		lblTAlist.setText("TA Unit list :");
		lblTAFlash = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAFlash = new FormData();
		fd_lblTAFlash.left = new FormAttachment(lblTAlist, 74);
		fd_lblTAFlash.right = new FormAttachment(100, -40);
		fd_lblTAFlash.top = new FormAttachment(lblTAlist, 0, SWT.TOP);
		lblTAFlash.setLayoutData(fd_lblTAFlash);
		lblTAFlash.setText("TA Unit to flash :");
		
		listViewerTAUnits = new ListViewer(shlTARestore, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listTAUnits = listViewerTAUnits.getList();
		fd_lblTAlist.bottom = new FormAttachment(listTAUnits, -6);
		FormData fd_listTAUnits = new FormData();
		fd_listTAUnits.bottom = new FormAttachment(100, -41);
		fd_listTAUnits.top = new FormAttachment(comboPartition, 38);
		fd_listTAUnits.left = new FormAttachment(lblBackupset, 0, SWT.LEFT);
		fd_listTAUnits.right = new FormAttachment(0, 105);
		listTAUnits.setLayoutData(fd_listTAUnits);
		listViewerTAUnits.setContentProvider(new IStructuredContentProvider() {
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
		listViewerTAUnits.setLabelProvider(new LabelProvider() {
		    public Image getImage(Object element) {
		      return null;
		    }
	   
		    public String getText(Object element) {
		      return ((TAUnit)element).getUnitHex();
		    }
		  });
		listViewerTAUnits.setSorter(new ViewerSorter(){
		    public int compare(Viewer viewer, Object e1, Object e2) {
		      return ((TAUnit)e1).getUnitHex().compareTo(((TAUnit)e2).getUnitHex());
		    }

		  });
		
		listViewerTAUnitsToFlash = new ListViewer(shlTARestore, SWT.BORDER | SWT.V_SCROLL);
		List listTAUnitsToFlash = listViewerTAUnitsToFlash.getList();
		FormData fd_listTAUnitsToFlash = new FormData();
		fd_listTAUnitsToFlash.right = new FormAttachment(comboBackupset, 0, SWT.RIGHT);
		fd_listTAUnitsToFlash.left = new FormAttachment(lblTAFlash, 10, SWT.LEFT);
		fd_listTAUnitsToFlash.bottom = new FormAttachment(listTAUnits, 0, SWT.BOTTOM);
		fd_listTAUnitsToFlash.top = new FormAttachment(listTAUnits, 0, SWT.TOP);
		listTAUnitsToFlash.setLayoutData(fd_listTAUnitsToFlash);
		listViewerTAUnitsToFlash.setContentProvider(new IStructuredContentProvider() {
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
		listViewerTAUnitsToFlash.setLabelProvider(new LabelProvider() {
		    public Image getImage(Object element) {
		      return null;
		    }
	   
		    public String getText(Object element) {
		      return ((TAUnit)element).getUnitHex();
		    }
		  });
		listViewerTAUnitsToFlash.setSorter(new ViewerSorter(){
		    public int compare(Viewer viewer, Object e1, Object e2) {
		      return ((TAUnit)e1).getUnitHex().compareTo(((TAUnit)e2).getUnitHex());
		    }

		  });
		btnLtoR = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnLtoR = new FormData();
		fd_btnLtoR.top = new FormAttachment(comboPartition, 81);
		fd_btnLtoR.left = new FormAttachment(listTAUnits, 6);
		btnLtoR.setLayoutData(fd_btnLtoR);
		btnLtoR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnits.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					TAUnit u = (TAUnit)i.next();
					available.remove(u);
					toflash.add(u);
				}
				listViewerTAUnits.refresh();
				listViewerTAUnitsToFlash.refresh();
			}
		});
		btnLtoR.setText("->");
		
		Button btnRtoL = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnRtoL = new FormData();
		fd_btnRtoL.bottom = new FormAttachment(100, -93);
		fd_btnRtoL.left = new FormAttachment(listTAUnits, 6);
		btnRtoL.setLayoutData(fd_btnRtoL);
		btnRtoL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnitsToFlash.getSelection();
				Iterator i = selection.iterator();
				while (i.hasNext()) {
					TAUnit u = (TAUnit)i.next();
					available.add(u);
					toflash.remove(u);
				}
				listViewerTAUnits.refresh();
				listViewerTAUnitsToFlash.refresh();
			}
		});
		btnRtoL.setText("<-");
		
		Button btnFlash = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnFlash = new FormData();
		fd_btnFlash.top = new FormAttachment(listTAUnitsToFlash, 6);
		fd_btnFlash.left = new FormAttachment(lblTAFlash, 0, SWT.LEFT);
		btnFlash.setLayoutData(fd_btnFlash);
		btnFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlTARestore.dispose();
			}
		});
		btnFlash.setText("Flash");
		
		btnCancel = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.left = new FormAttachment(btnFlash, 13);
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.top = new FormAttachment(btnFlash, 0, SWT.TOP);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlTARestore.dispose();
			}
		});
		btnCancel.setText("Cancel");

		File srcFolder = new File(Devices.getCurrent().getFolderRegisteted()+File.separator+"s1ta");
		File[] chld = srcFolder.listFiles();
		for (int i=0; i < chld.length ; i++) {
			comboBackupset.add(chld[i].getName());
			File srcFolderBackup = new File(Devices.getCurrent().getFolderRegisteted()+File.separator+"s1ta"+File.separator+chld[i].getName());
			File chldPartition[] = srcFolderBackup.listFiles();
			Vector<TABag> bags = new Vector<TABag>();
			for (int j=0;j<chldPartition.length;j++) {
				try {
					TABag bag = new TABag(chldPartition[j]);
					bags.add(bag);
					
				} catch (Exception e) {}
			}
			backupset.put(chld[i].getName(), bags);
		}
		comboBackupset.select(0);
		refreshPartitions();

	}

	public void refreshPartitions() {
		result = backupset.get(comboBackupset.getText());
		comboPartition.removeAll();
		String [] comboarray = new String[result.size()];
		for (int i = 0; i<result.size(); i++) {
			comboarray[i]=String.valueOf(result.get(i).partition);
		}
		Arrays.sort(comboarray);
		comboPartition.setItems(comboarray);
		comboPartition.select(0);
		refreshUnits();
	}

	public void refreshUnits() {
		try {
			TABag b = getPartition(Integer.parseInt(comboPartition.getText()));
			available = b.available;
			toflash = b.toflash;
			listViewerTAUnits.setInput(available);
			listViewerTAUnits.refresh();
			listViewerTAUnitsToFlash.setInput(toflash);
			listViewerTAUnitsToFlash.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public TABag getPartition(int partition) {
		for (int i=0;i<result.size();i++) {
			if (result.get(i).partition==partition) return result.get(i);
		}
		return null;
	}

	public void showErrorMessageBox(String message) {
		MessageBox mb = new MessageBox(shlTARestore,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Errorr");
		mb.setMessage(message);
		int result = mb.open();
	}
}