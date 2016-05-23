package gui;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import gui.tools.ExtractSinDataJob;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridData;
import org.sinfile.parsers.SinFile;

public class SinEditor extends Dialog {

	protected Object result;
	protected Shell shlSinEditor;
	private Button btnDumpHeader;
	private Button btnDumpData;
	private Button btnAdvanced;
	private Button btnNewButton_1;
	private Button btnClose;
	private Composite composite_1;
	private Label lblSinFile;
	private Text sourceFile;
	private Button button;
	private FormData fd_btnClose;
	static final Logger logger = LogManager.getLogger(SinEditor.class);
	private Composite composite;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SinEditor(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();

		shlSinEditor.open();
		shlSinEditor.layout();
		Display display = getParent().getDisplay();
		while (!shlSinEditor.isDisposed()) {
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
		shlSinEditor = new Shell(getParent(), getStyle());
		shlSinEditor.setSize(528, 159);
		shlSinEditor.setText("Sin Editor");
		shlSinEditor.setLayout(new FormLayout());
		
		btnClose = new Button(shlSinEditor, SWT.NONE);
		fd_btnClose = new FormData();
		fd_btnClose.right = new FormAttachment(100, -10);
		fd_btnClose.bottom = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlSinEditor.dispose();
			}
		});
		btnClose.setText("Close");
		composite_1 = new Composite(shlSinEditor, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.top = new FormAttachment(0, 10);
		fd_composite_1.left = new FormAttachment(0,10);
		fd_composite_1.right = new FormAttachment(100, -10);
		composite_1.setLayoutData(fd_composite_1);
		
		lblSinFile = new Label(composite_1, SWT.NONE);
		GridData gd_lblSinFile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSinFile.widthHint = 62;
		lblSinFile.setLayoutData(gd_lblSinFile);
		lblSinFile.setText("Sin file :");
		
		sourceFile = new Text(composite_1, SWT.BORDER);
		sourceFile.setEnabled(false);
		GridData gd_sourceFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_sourceFile.widthHint = 369;
		sourceFile.setLayoutData(gd_sourceFile);
		
		button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlSinEditor);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(sourceFile.getText());
		        dlg.setFilterExtensions(new String[]{"*.sin"});

		        // Change the title bar text
		        dlg.setText("SIN File Chooser");
		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	if (!sourceFile.getText().equals(dir)) {
		        		sourceFile.setText(dir);
		        		btnDumpHeader.setEnabled(true);
		        		btnDumpData.setEnabled(true);
		        		btnAdvanced.setEnabled(true);
		        		btnNewButton_1.setEnabled(true);
		        	}
		        }
			}
		});
		GridData gd_button = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_button.widthHint = 34;
		button.setLayoutData(gd_button);
		button.setText("...");
		
		composite = new Composite(shlSinEditor, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(composite_1, 6);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);
		
		btnDumpHeader = new Button(composite, SWT.NONE);
		btnDumpHeader.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SinFile sinf = new SinFile(new File(sourceFile.getText()));
					sinf.dumpHeader();
				}
				catch (Exception ex) {
				}
			}
		});
		btnDumpHeader.setText("Dump header");
		btnDumpHeader.setEnabled(false);
		
		btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SinFile sinf = new SinFile(new File(sourceFile.getText()));
					ExtractSinDataJob ej = new ExtractSinDataJob("Sin dump job");
					ej.setSin(sinf);
					ej.setMode("raw");
					ej.schedule();
				}
				catch (Exception ex) {
				}
			}
		});
		btnNewButton_1.setText("Dump raw");
		btnNewButton_1.setEnabled(false);
		
		btnDumpData = new Button(composite, SWT.NONE);
		btnDumpData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SinFile sinf = new SinFile(new File(sourceFile.getText()));
					ExtractSinDataJob ej = new ExtractSinDataJob("Sin dump job");
					ej.setSin(sinf);
					ej.setMode("data");
					ej.schedule();
				}
				catch (Exception ex) {
				}
			}
		});
		btnDumpData.setText("Extract data");
		btnDumpData.setEnabled(false);
		btnAdvanced = new Button(composite, SWT.NONE);
		btnAdvanced.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
				SinFile sin =new SinFile(new File(sourceFile.getText()));
				SinAdvanced sadv = new SinAdvanced(shlSinEditor,SWT.PRIMARY_MODAL | SWT.SHEET);
				sadv.open(sin);
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
		});
		btnAdvanced.setText("Advanced");
		btnAdvanced.setEnabled(false);
	}
}
