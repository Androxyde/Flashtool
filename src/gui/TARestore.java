package gui;

import gui.models.TABag;
import gui.tools.WidgetTask;
import swt_components.SwtHexEdit;

import java.io.ByteArrayInputStream;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.custom.StyledText;

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
	CTabItemWithHexViewer hexviewer;

	
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
	public Object open(HashMap<String,Vector<TABag>> backupset) {
		this.backupset = backupset;
		createContents();
		
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
		shlTARestore = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlTARestore.setSize(661, 500);
		shlTARestore.setText("TA Restore");
		shlTARestore.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    		result = null;
		    	  	event.doit = true;
		      }
		    });
		shlTARestore.setLayout(new FormLayout());
		Composite composite = new Composite(shlTARestore, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.left = new FormAttachment(0,115);
		fd_composite.right = new FormAttachment(100,-115);
		composite.setLayoutData(fd_composite);

		lblTAlist = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAlist = new FormData();
		
		lblTAlist.setLayoutData(fd_lblTAlist);
		lblTAlist.setText("TA Unit list :");
		lblTAFlash = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAFlash = new FormData();
		
		;
		lblTAFlash.setLayoutData(fd_lblTAFlash);
		lblTAFlash.setText("TA Unit to flash :");
		
		listViewerTAUnits = new ListViewer(shlTARestore, SWT.BORDER | SWT.V_SCROLL);
		listViewerTAUnits.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnits.getSelection();
				if (selection.size()==1) {
					listViewerTAUnitsToFlash.setSelection(StructuredSelection.EMPTY);
					TAUnit u = (TAUnit)selection.getFirstElement();
					hexviewer.loadContent(u.getUnitData());
				}
			}
		});
		listTAUnits = listViewerTAUnits.getList();
		fd_lblTAlist.left = new FormAttachment(listTAUnits, 0, SWT.LEFT);
		
		FormData fd_listTAUnits = new FormData();
		fd_listTAUnits.top = new FormAttachment(lblTAlist, 6);
		fd_listTAUnits.left = new FormAttachment(composite, 0, SWT.LEFT);
		fd_listTAUnits.width = 100;
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
		fd_lblTAFlash.left = new FormAttachment(listTAUnitsToFlash, 0, SWT.LEFT);
		FormData fd_listTAUnitsToFlash = new FormData();
		fd_listTAUnitsToFlash.top = new FormAttachment(lblTAFlash, 6);
		fd_listTAUnitsToFlash.right = new FormAttachment(composite, 0, SWT.RIGHT);
		fd_listTAUnitsToFlash.width = 100;
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
		
		listViewerTAUnitsToFlash.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnitsToFlash.getSelection();
				if (selection.size()==1) {
					listViewerTAUnits.setSelection(StructuredSelection.EMPTY);
					TAUnit u = (TAUnit)selection.getFirstElement();
					hexviewer.loadContent(u.getUnitData());
				}
			}
		});
		btnLtoR = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnLtoR = new FormData();
		fd_btnLtoR.top = new FormAttachment(composite, 57);
		fd_btnLtoR.left = new FormAttachment(listTAUnits, 69);
		btnLtoR.setLayoutData(fd_btnLtoR);
		btnLtoR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnits.getSelection();
				Iterator i = selection.iterator();
				hexviewer.loadContent(new byte[]{});
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
		fd_btnRtoL.top = new FormAttachment(btnLtoR, 6);
		fd_btnRtoL.right = new FormAttachment(btnLtoR, 0, SWT.RIGHT);
		btnRtoL.setLayoutData(fd_btnRtoL);
		btnRtoL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnitsToFlash.getSelection();
				Iterator i = selection.iterator();
				hexviewer.loadContent(new byte[]{});
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
		
		btnFlash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlTARestore.dispose();
			}
		});
		btnFlash.setText("Flash");
		
		btnCancel = new Button(shlTARestore, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100,-10);
		FormData fd_btnFlash = new FormData();
		fd_btnFlash.bottom = new FormAttachment(100,-10);
		fd_btnFlash.right = new FormAttachment(btnCancel, -6);
		btnFlash.setLayoutData(fd_btnFlash);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlTARestore.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		CTabFolder tabFolder = new CTabFolder(shlTARestore, SWT.NONE);
		fd_listTAUnits.bottom = new FormAttachment(tabFolder, -6);
		fd_listTAUnitsToFlash.bottom = new FormAttachment(tabFolder, -6);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.right = new FormAttachment(100, -10);
		fd_tabFolder.bottom = new FormAttachment(btnCancel, -8);
		fd_tabFolder.top = new FormAttachment(0, 200);
		tabFolder.setLayoutData(fd_tabFolder);
		hexviewer = new CTabItemWithHexViewer(tabFolder,"TA unit content",SWT.BORDER);
		
		Label lblBackupset = new Label(composite, SWT.NONE);
		lblBackupset.setAlignment(SWT.RIGHT);
		GridData gd_lblBackupset = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblBackupset.widthHint = 74;
		lblBackupset.setLayoutData(gd_lblBackupset);
		lblBackupset.setText("Backupset :");
		fd_lblTAlist.top = new FormAttachment(composite,6);
		fd_lblTAFlash.top = new FormAttachment(composite, 6);
				comboBackupset = new Combo(composite, SWT.READ_ONLY);
				GridData gd_comboBackupset = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_comboBackupset.widthHint = 174;
				comboBackupset.setLayoutData(gd_comboBackupset);
				
				Label lblPartition = new Label(composite, SWT.NONE);
				lblPartition.setAlignment(SWT.RIGHT);
				lblPartition.setText("Partition :");
				GridData gd_lblPartition = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
				gd_lblPartition.widthHint = 76;
				lblPartition.setLayoutData(gd_lblPartition);
				
				comboPartition = new Combo(composite, SWT.READ_ONLY);
				comboPartition.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						refreshUnits();
					}
				});
				comboBackupset.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						refreshPartitions();
					}
				});
				
				comboBackupset.select(0);
		

		if (backupset.size()>0) {
			Iterator keys = backupset.keySet().iterator();
			while (keys.hasNext()) {
				comboBackupset.add((String)keys.next());
			}
			comboBackupset.select(0);
			refreshPartitions();
		}
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
			hexviewer.loadContent(new byte[]{});
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