package gui;

import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorContentProvider;
import gui.models.VectorLabelProvider;
import gui.tools.USBParseJob;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.simpleusblogger.S1Packet;
import org.simpleusblogger.Session;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.TextFile;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class USBLogviewer extends Dialog {

	protected Object result;
	protected Shell shlUSBLogviewer;
	private Table table;
	private TableViewer tableViewer;
	private Text textSinFolder;
	private Button btnClose;
	private Composite compositeTable;
	//private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Label lblLogfile;
	private Text textLogFile;
	private Button btnParse;
	private Button btnLogFile;
	private Button btnSourceFolder;
	private Label lblSavedPath;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public USBLogviewer(Shell parent, int style) {
		super(parent, style);
		setText("Device Selector");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		
		createContents();
		createTriggers();
		
		lblSavedPath = new Label(shlUSBLogviewer, SWT.NONE);
		FormData fd_lblSavedPath = new FormData();
		fd_lblSavedPath.right = new FormAttachment(btnParse, -6);
		fd_lblSavedPath.bottom = new FormAttachment(100, -15);
		fd_lblSavedPath.left = new FormAttachment(0, 10);
		lblSavedPath.setLayoutData(fd_lblSavedPath);
		
		
		shlUSBLogviewer.open();
		shlUSBLogviewer.layout();
		
		Display display = getParent().getDisplay();
		while (!shlUSBLogviewer.isDisposed()) {
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
		shlUSBLogviewer = new Shell(getParent(), getStyle());
		shlUSBLogviewer.setSize(710, 475);
		shlUSBLogviewer.setText("USB Log Viewer");
		shlUSBLogviewer.setLayout(new FormLayout());
		
		btnClose = new Button(shlUSBLogviewer, SWT.NONE);
		FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(100, -10);
		fd_btnClose.right = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("Close");
		
		compositeTable = new Composite(shlUSBLogviewer, SWT.NONE);
		compositeTable.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_compositeTable = new FormData();
		
		fd_compositeTable.right = new FormAttachment(100, -10);
		fd_compositeTable.left = new FormAttachment(0, 10);
		fd_compositeTable.bottom = new FormAttachment(btnClose, -6);
		compositeTable.setLayoutData(fd_compositeTable);
		
		tableViewer = new TableViewer(compositeTable,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
		tableViewer.setContentProvider(new VectorContentProvider());
		tableViewer.setLabelProvider(new VectorLabelProvider());

		table = tableViewer.getTable();
		TableColumn[] columns = new TableColumn[2];
		columns[0] = new TableColumn(table, SWT.NONE);
		columns[0].setText("Action");
		columns[1] = new TableColumn(table, SWT.NONE);
		columns[1].setText("Parameter");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableSorter sort = new TableSorter(tableViewer);
		Composite compositeSource = new Composite(shlUSBLogviewer, SWT.NONE);
		compositeSource.setLayout(new GridLayout(3, false));
		FormData fd_compositeSource = new FormData();
		fd_compositeSource.top = new FormAttachment(0, 10);
		fd_compositeSource.left = new FormAttachment(0, 10);
		fd_compositeSource.right = new FormAttachment(100, -10);
		compositeSource.setLayoutData(fd_compositeSource);
		
		fd_compositeTable.top = new FormAttachment(compositeSource, 6);
		
		lblLogfile = new Label(compositeSource, SWT.BORDER);
		lblLogfile.setText("USB Log file :");
		
		textLogFile = new Text(compositeSource, SWT.BORDER);
		textLogFile.setEditable(false);
		GridData gd_textLogFile = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_textLogFile.widthHint = 471;
		textLogFile.setLayoutData(gd_textLogFile);
		
		btnLogFile = new Button(compositeSource, SWT.NONE);
		btnLogFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnLogFile.setText("...");
		
		Label lblSinfolder = new Label(compositeSource, SWT.NONE);
		GridData gd_lblSinfolder = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSinfolder.widthHint = 110;
		lblSinfolder.setLayoutData(gd_lblSinfolder);
		lblSinfolder.setText("Source folder :");
		
		textSinFolder = new Text(compositeSource, SWT.BORDER);
		textSinFolder.setEditable(false);
		GridData gd_textSinFolder = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_textSinFolder.widthHint = 509;
		textSinFolder.setLayoutData(gd_textSinFolder);
		
		btnSourceFolder = new Button(compositeSource, SWT.NONE);
		GridData gd_btnSourceFolder = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnSourceFolder.widthHint = 46;
		btnSourceFolder.setLayoutData(gd_btnSourceFolder);
		btnSourceFolder.setText("...");
		btnParse = new Button(shlUSBLogviewer, SWT.NONE);
		FormData fd_btnParse = new FormData();
		fd_btnParse.bottom = new FormAttachment(100,-10);
		fd_btnParse.right = new FormAttachment(btnClose, -6);
		btnParse.setLayoutData(fd_btnParse);
		btnParse.setText("Parse");

	}

	public void createTriggers() {
		
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlUSBLogviewer.dispose();
			}
		});

		table.addListener(SWT.DefaultSelection, new Listener() {
		      public void handleEvent(Event e) {
		        TableItem[] selection = table.getSelection();
		        String string = selection[0].getText(0);
		        result = string;
		        shlUSBLogviewer.dispose();
		      }
		    });

		btnSourceFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlUSBLogviewer);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(textSinFolder.getText());

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
		        	if (!textSinFolder.getText().equals(dir)) {
		        		textSinFolder.setText(dir);
		        		tableViewer.setInput(new Vector());
		        		tableViewer.refresh();
		        		lblSavedPath.setText("");
		        	}
		        }
			}
		});

		btnLogFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlUSBLogviewer);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(textLogFile.getText());

		        // Change the title bar text
		        dlg.setText("TMS File chooser");

		        dlg.setFilterExtensions(new String[] {"*.tms"});

		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	if (!textLogFile.getText().equals(dir)) {
		        		textLogFile.setText(dir);
		        		textSinFolder.setText(new File(dir).getParentFile().getAbsolutePath()+File.separator+"decrypted");
		        		lblSavedPath.setText("");
		        		tableViewer.setInput(new Vector());
		        		tableViewer.refresh();
		        	}
		        }
			}
		});

		btnParse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WaitForUSBParser parse = new WaitForUSBParser(shlUSBLogviewer,SWT.PRIMARY_MODAL | SWT.SHEET);
				Session sess = (Session)parse.open(textLogFile.getText(),textSinFolder.getText());	
				lblSavedPath.setText("Script saved to "+sess.saveScript());
						
				Display.getDefault().asyncExec(
						new Runnable() {
							public void run() {
								tableViewer.setInput(sess.getScript());
							    for (int nbcols=0;nbcols<table.getColumnCount();nbcols++)
							    	table.getColumn(nbcols).pack();
							    tableViewer.refresh();
							}
						}
				);
			}
		});

	}
}
