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
	private SwtHexEdit hxed;
	private StyledText styledTextAscii;
	private Label lblInfos;

	
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
		shlTARestore.setSize(728, 427);
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
		
		fd_lblBackupset.left = new FormAttachment(0, 10);
		lblBackupset.setLayoutData(fd_lblBackupset);
		lblBackupset.setText("Backupset :");
		
		comboBackupset = new Combo(shlTARestore, SWT.READ_ONLY);
		FormData fd_comboBackupset = new FormData();
		fd_comboBackupset.left = new FormAttachment(lblBackupset, 35);
		fd_comboBackupset.right = new FormAttachment(100, -10);
		fd_comboBackupset.top = new FormAttachment(0, 10);
		fd_lblBackupset.top = new FormAttachment(comboBackupset, 4, SWT.TOP);
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
		
		fd_lblPartition.left = new FormAttachment(0, 10);
		lblPartition.setLayoutData(fd_lblPartition);
		lblPartition.setText("Partition : ");
		
		comboPartition = new Combo(shlTARestore, SWT.READ_ONLY);
		FormData fd_comboPartition = new FormData();
		fd_comboPartition.top = new FormAttachment(comboBackupset, 6);
		fd_comboPartition.left = new FormAttachment(comboBackupset, 0, SWT.LEFT);
		fd_lblPartition.top = new FormAttachment(comboPartition, 4, SWT.TOP);
		comboPartition.setLayoutData(fd_comboPartition);
		comboPartition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshUnits();
			}
		});
		lblTAlist = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAlist = new FormData();
		lblTAlist.setLayoutData(fd_lblTAlist);
		lblTAlist.setText("TA Unit list :");
		lblTAFlash = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblTAFlash = new FormData();
		fd_lblTAFlash.top = new FormAttachment(lblTAlist, 0, SWT.TOP);
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
					hxed.setByteData(u.getUnitData());
					styledTextAscii.setText("");
					lblInfos.setText("Unit length : "+u.getDataLength());
					JBBPBitInputStream is = new JBBPBitInputStream(new ByteArrayInputStream(u.getUnitData()));
					byte res[]=null;
					int totalread = 48;
					int total = u.getUnitData().length;
					try {
						while (is.hasAvailableData()) {
							total = total -totalread;
							if (total >= 0)
								res = is.readByteArray(totalread);
							else
								res = is.readByteArray(totalread+total);
							for (int readI = 0; readI < res.length; readI++) {
								byte b = res[readI];
								if (b<32 || b>=127) {
									// ASCII printable:
									res[readI] = '.';
								}
							}
							styledTextAscii.setCaretOffset(styledTextAscii.getText().length());
							styledTextAscii.insert(new String(res)+"\n");
						}
						is.close();
					} catch (Exception e) {}
				}
			}
		});
		listTAUnits = listViewerTAUnits.getList();
		fd_lblTAlist.top = new FormAttachment(comboPartition, 6);
		fd_lblTAlist.left = new FormAttachment(0, 10);
		
		FormData fd_listTAUnits = new FormData();
		fd_listTAUnits.width = 100;
		fd_listTAUnits.top = new FormAttachment(lblTAlist, 6);
		fd_listTAUnits.left = new FormAttachment(0, 10);
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
					hxed.setByteData(u.getUnitData());
					styledTextAscii.setText("");
					lblInfos.setText("Unit length : "+u.getDataLength());
					JBBPBitInputStream is = new JBBPBitInputStream(new ByteArrayInputStream(u.getUnitData()));
					byte res[]=null;
					int totalread = 48;
					int total = u.getUnitData().length;
					try {
						while (is.hasAvailableData()) {
							total = total -totalread;
							if (total >= 0)
								res = is.readByteArray(totalread);
							else
								res = is.readByteArray(totalread+total);
							for (int readI = 0; readI < res.length; readI++) {
								byte b = res[readI];
								if (b<32 || b>=127) {
									// ASCII printable:
									res[readI] = '.';
								}
							}
							styledTextAscii.setCaretOffset(styledTextAscii.getText().length());
							styledTextAscii.insert(new String(res)+"\n");
						}
						is.close();
					} catch (Exception e) {}
				}
			}
		});
		btnLtoR = new Button(shlTARestore, SWT.NONE);
		fd_listTAUnitsToFlash.left = new FormAttachment(btnLtoR, 6);
		FormData fd_btnLtoR = new FormData();
		fd_btnLtoR.left = new FormAttachment(listTAUnits, 6);
		btnLtoR.setLayoutData(fd_btnLtoR);
		btnLtoR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnits.getSelection();
				Iterator i = selection.iterator();
				hxed.setByteData(new byte[]{});
				styledTextAscii.setText("");
				lblInfos.setText("");
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
		fd_btnLtoR.bottom = new FormAttachment(btnRtoL, -19);
		FormData fd_btnRtoL = new FormData();
		fd_btnRtoL.top = new FormAttachment(0, 233);
		fd_btnRtoL.left = new FormAttachment(listTAUnits, 6);
		btnRtoL.setLayoutData(fd_btnRtoL);
		btnRtoL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)listViewerTAUnitsToFlash.getSelection();
				Iterator i = selection.iterator();
				hxed.setByteData(new byte[]{});
				styledTextAscii.setText("");
				lblInfos.setText("");
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
		fd_listTAUnits.bottom = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.bottom = new FormAttachment(100,-10);
		FormData fd_btnFlash = new FormData();
		fd_btnFlash.bottom = new FormAttachment(100,-10);
		fd_btnFlash.right = new FormAttachment(btnCancel, -6);
		btnFlash.setLayoutData(fd_btnFlash);
		btnCancel.setLayoutData(fd_btnCancel);
		fd_listTAUnitsToFlash.bottom = new FormAttachment(btnCancel, -6);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlTARestore.dispose();
			}
		});
		btnCancel.setText("Cancel");
		
		TabFolder tabFolder = new TabFolder(shlTARestore, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(btnCancel, -6);
		fd_tabFolder.right = new FormAttachment(100, -10);
		fd_tabFolder.top = new FormAttachment(lblTAlist, 0, SWT.TOP);
		fd_tabFolder.left = new FormAttachment(listTAUnitsToFlash, 6);
		tabFolder.setLayoutData(fd_tabFolder);
		
		TabItem tbtmHexa = new TabItem(tabFolder, SWT.NONE);
		tbtmHexa.setText("Hexadecimal");
		
		ScrolledComposite scrolledCompositeHexa = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmHexa.setControl(scrolledCompositeHexa);
		scrolledCompositeHexa.setExpandHorizontal(true);
		scrolledCompositeHexa.setExpandVertical(true);
		
		Composite compositeHexa = new Composite(scrolledCompositeHexa, SWT.NONE);
		scrolledCompositeHexa.setContent(compositeHexa);
		scrolledCompositeHexa.setMinSize(compositeHexa.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeHexa.setLayout(new FormLayout());
		hxed = new SwtHexEdit(compositeHexa,SWT.H_SCROLL | SWT.V_SCROLL,30,16,6);
		FormData fd_hxed = new FormData();
		fd_hxed.bottom = new FormAttachment(0, 257);
		fd_hxed.right = new FormAttachment(0, 444);
		fd_hxed.top = new FormAttachment(0);
		fd_hxed.left = new FormAttachment(0, 5);
		hxed.setLayoutData(fd_hxed);
		hxed.setInsertMode(false);
		hxed.setEditable(false);

		TabItem tbtmAscii = new TabItem(tabFolder, SWT.NONE);
		tbtmAscii.setText("Ascii");
		
		
		styledTextAscii = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FontData fontData = new FontData();
		fontData.height=10;
		fontData.setName("Courier");
		styledTextAscii.setFont(new Font( shlTARestore.getDisplay(), fontData));
		styledTextAscii.setEditable(false);

		tbtmAscii.setControl(styledTextAscii);
		
		lblInfos = new Label(shlTARestore, SWT.NONE);
		FormData fd_lblInfos = new FormData();
		fd_lblInfos.top = new FormAttachment(comboBackupset, 10);
		fd_lblInfos.right = new FormAttachment(100, -10);
		fd_lblInfos.left = new FormAttachment(100, -337);
		lblInfos.setLayoutData(fd_lblInfos);
		

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
			hxed.setByteData(new byte[]{});
			styledTextAscii.setText("");
			lblInfos.setText("");
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