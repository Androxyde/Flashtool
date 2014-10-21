package gui;

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

import flashsystem.SinFile;
import gui.tools.CreateSinAsJob;
import gui.tools.WidgetsTool;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.util.HexDump;

public class SinAdvanced extends Dialog {

	protected Object result;
	protected Shell shlSinEditor;
	private Text textVersion;
	private Text textPartition;
	private Text textSpare;
	private Text textContent;
	private SinFile _sin;

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
		WidgetsTool.setSize(shlSinEditor);
		
		Label lblSinVersion = new Label(shlSinEditor, SWT.NONE);
		FormData fd_lblSinVersion = new FormData();
		fd_lblSinVersion.top = new FormAttachment(0, 10);
		fd_lblSinVersion.left = new FormAttachment(0, 10);
		lblSinVersion.setLayoutData(fd_lblSinVersion);
		lblSinVersion.setText("Sin version :");
		
		textVersion = new Text(shlSinEditor, SWT.BORDER);
		textVersion.setEditable(false);
		FormData fd_textVersion = new FormData();
		fd_textVersion.right = new FormAttachment(100, -215);
		fd_textVersion.left = new FormAttachment(0, 10);
		fd_textVersion.top = new FormAttachment(lblSinVersion, 6);
		textVersion.setLayoutData(fd_textVersion);
		textVersion.setText(Integer.toString(_sin.getSinHeader().getVersion()));
		
		textPartition = new Text(shlSinEditor, SWT.BORDER);
		textPartition.setEditable(false);
		FormData fd_textPartition = new FormData();
		fd_textPartition.left = new FormAttachment(lblSinVersion, 0, SWT.LEFT);
		textPartition.setLayoutData(fd_textPartition);
		textPartition.setText(_sin.getSinHeader().hasPartitionInfo()?HexDump.toHex(_sin.getSinHeader().getPartitionInfo()).replace("[", "").replace("]", "").replace(", ",""):"");
		
		textSpare = new Text(shlSinEditor, SWT.BORDER);
		textSpare.setEditable(false);
		FormData fd_textSpare = new FormData();
		fd_textSpare.right = new FormAttachment(100, -131);
		fd_textSpare.left = new FormAttachment(0, 10);
		textSpare.setLayoutData(fd_textSpare);
		textSpare.setText(_sin.getSinHeader().getPartypeString());
		
		Label lblPartition = new Label(shlSinEditor, SWT.NONE);
		fd_textPartition.top = new FormAttachment(lblPartition, 6);
		FormData fd_lblPartition = new FormData();
		fd_lblPartition.top = new FormAttachment(textVersion, 6);
		fd_lblPartition.left = new FormAttachment(lblSinVersion, 0, SWT.LEFT);
		lblPartition.setLayoutData(fd_lblPartition);
		lblPartition.setText("Partition Info :");
		
		Label lblSpare = new Label(shlSinEditor, SWT.NONE);
		fd_textSpare.top = new FormAttachment(lblSpare, 6);
		FormData fd_lblSpare = new FormData();
		fd_lblSpare.top = new FormAttachment(textPartition, 6);
		fd_lblSpare.left = new FormAttachment(lblSinVersion, 0, SWT.LEFT);
		lblSpare.setLayoutData(fd_lblSpare);
		lblSpare.setText("Spare Info :");
		
		textContent = new Text(shlSinEditor, SWT.BORDER);
		textContent.setEditable(false);
		FormData fd_textContent = new FormData();
		fd_textContent.right = new FormAttachment(lblSinVersion, 0, SWT.RIGHT);
		fd_textContent.left = new FormAttachment(lblSinVersion, 0, SWT.LEFT);
		textContent.setLayoutData(fd_textContent);
		try {
			textContent.setText(_sin.getDataType());
		}
		catch (Exception e) {
		}
		
		Label lblContentType = new Label(shlSinEditor, SWT.NONE);
		fd_textContent.top = new FormAttachment(lblContentType, 6);
		FormData fd_lblContentType = new FormData();
		fd_lblContentType.top = new FormAttachment(textSpare, 6);
		fd_lblContentType.left = new FormAttachment(lblSinVersion, 0, SWT.LEFT);
		lblContentType.setLayoutData(fd_lblContentType);
		lblContentType.setText("Content Type :");
		
		Button btnClose = new Button(shlSinEditor, SWT.NONE);
		fd_textPartition.right = new FormAttachment(btnClose, -12, SWT.RIGHT);
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
		        cj.setSpare(HexDump.toHex(_sin.getSinHeader().getPartitionType()));
		        cj.schedule();
		        if (file!=null)
		        	shlSinEditor.dispose();
			}
		});
		FormData fd_btnCreateSinAs = new FormData();
		fd_btnCreateSinAs.top = new FormAttachment(btnClose, 0, SWT.TOP);
		fd_btnCreateSinAs.right = new FormAttachment(btnClose, -6);
		btnCreateSinAs.setLayoutData(fd_btnCreateSinAs);
		btnCreateSinAs.setText("Create Sin As");
		
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
		shlSinEditor.setSize(254, 275);
		shlSinEditor.setText("Advanced Sin Editor");
		shlSinEditor.setLayout(new FormLayout());
	}
}
