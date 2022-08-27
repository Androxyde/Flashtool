package org.flashtool.gui;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.flashtool.flashsystem.S1Command;
import org.flashtool.gui.tools.CreateSinAsJob;
import org.flashtool.parsers.sin.SinFile;
import org.flashtool.util.HexDump;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

@Slf4j
public class SinAdvanced extends Dialog {

	protected Object result;
	protected Shell shlSinEditor;
	private Text textVersion;
	private Text textPartition;
	private Text textSpare;
	private Text textContent;
	private SinFile _sin;
	static final Logger logger = LogManager.getLogger(SinAdvanced.class);

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SinAdvanced(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(SinFile sin) {
		_sin = sin;
		createContents();
		try {
		Button btnClose = new Button(shlSinEditor, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlSinEditor.dispose();
			}
		});
		FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(100, -10);
		fd_btnClose.right = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("Close");
		
		Button btnCreateSinAs = new Button(shlSinEditor, SWT.NONE);
		btnCreateSinAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlSinEditor);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterExtensions(new String[]{"*.yaffs2"});

		        // Change the title bar text
		        dlg.setText("YAFFS2 File Chooser");
		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String file = dlg.open();
		        CreateSinAsJob cj = new CreateSinAsJob("Create SIN");
		        cj.setFile(file);
		        cj.setPartition(textPartition.getText());
		        cj.setSpare(HexDump.toHex(_sin.getPartitionType()));
		        cj.schedule();
		        if (file!=null)
		        	shlSinEditor.dispose();
			}
		});
		FormData fd_btnCreateSinAs = new FormData();
		fd_btnCreateSinAs.bottom = new FormAttachment(100, -10);
		fd_btnCreateSinAs.right = new FormAttachment(btnClose, -6);
		btnCreateSinAs.setLayoutData(fd_btnCreateSinAs);
		btnCreateSinAs.setText("Create Sin As");
		btnCreateSinAs.setEnabled(_sin.getVersion()==1);
		
		Composite composite = new Composite(shlSinEditor, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		FormData fd_composite = new FormData();
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.top = new FormAttachment(0, 10);
		
		
		composite.setLayoutData(fd_composite);
		
		Label lblSinVersion = new Label(composite, SWT.NONE);
		lblSinVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblSinVersion.setText("Sin version :");
		
		textVersion = new Text(composite, SWT.BORDER);
		textVersion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		textVersion.setEditable(false);
		textVersion.setText(Integer.toString(_sin.getVersion()));
		
		Label lblPartition = new Label(composite, SWT.NONE);
		lblPartition.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblPartition.setText("Partition Info :");
		
		textPartition = new Text(composite, SWT.BORDER);
		GridData gd_textPartition = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textPartition.widthHint = 121;
		textPartition.setLayoutData(gd_textPartition);
		textPartition.setEditable(false);
		textPartition.setText(_sin.hasPartitionInfo()?HexDump.toHex(_sin.getPartitionInfo()):"");
		
		Label lblSpare = new Label(composite, SWT.NONE);
		lblSpare.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblSpare.setText("Partition Type :");
		textSpare = new Text(composite, SWT.BORDER);
		textSpare.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		textSpare.setEditable(false);
		textSpare.setText(_sin.getPartypeString());
		
		Label lblContentType = new Label(composite, SWT.NONE);
		lblContentType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblContentType.setText("Content Type :");
		
		textContent = new Text(composite, SWT.BORDER);
		textContent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		textContent.setEditable(false);
		textContent.setText(_sin.getDataType());
		} catch (Exception e) {}
		
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
		shlSinEditor.setSize(299, 314);
		shlSinEditor.setText("Advanced Sin Editor");
		shlSinEditor.setLayout(new FormLayout());
	}
}
