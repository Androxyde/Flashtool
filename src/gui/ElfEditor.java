package gui;

import gui.tools.WidgetsTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import nl.lxtreme.binutils.elf.ProgramHeader;
import nl.lxtreme.binutils.elf.Section;
import nl.lxtreme.binutils.elf.Attribute;
import nl.lxtreme.binutils.elf.Elf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.util.HexDump;


public class ElfEditor extends Dialog {

	protected Object result;
	protected Shell shlElfExtractor;
	private Text sourceFile;
	private Text textNbParts;
	private Elf elfobj;
	private Button btnExtract;
	static final Logger logger = LogManager.getLogger(ElfEditor.class);
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ElfEditor(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlElfExtractor);
		shlElfExtractor.open();
		shlElfExtractor.layout();
		Display display = getParent().getDisplay();
		while (!shlElfExtractor.isDisposed()) {
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
		shlElfExtractor = new Shell(getParent(), getStyle());
		shlElfExtractor.setSize(538, 153);
		shlElfExtractor.setText("Elf Extractor");
		shlElfExtractor.setLayout(new FormLayout());
		
		Composite composite = new Composite(shlElfExtractor, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -9);
		composite.setLayoutData(fd_composite);
		
		Label lblElfFile = new Label(composite, SWT.NONE);
		GridData gd_lblElfFile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblElfFile.widthHint = 62;
		lblElfFile.setLayoutData(gd_lblElfFile);
		lblElfFile.setText("Elf file :");
		
		sourceFile = new Text(composite, SWT.BORDER);
		sourceFile.setEditable(false);
		GridData gd_sourceFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_sourceFile.widthHint = 385;
		sourceFile.setLayoutData(gd_sourceFile);
		
		Button btnFileChoose = new Button(composite, SWT.NONE);
		btnFileChoose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlElfExtractor);

		        // Set the initial filter path according
		        // to anything they've selected or typed in
		        dlg.setFilterPath(sourceFile.getText());
		        dlg.setFilterExtensions(new String[]{"*.elf"});

		        // Change the title bar text
		        dlg.setText("ELF File Chooser");
		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	if (!sourceFile.getText().equals(dir)) {
		        		try {
		        		    elfobj = new nl.lxtreme.binutils.elf.Elf(new File(dir));
		        		    elfobj.loadSymbols();
		        		    Attribute attributes = elfobj.getAttributes();
		        		    Section[] sections = elfobj.getSections();
		        		    ProgramHeader[] programHeaders = elfobj.getProgramHeaders();

		        			textNbParts.setText(Integer.toString(elfobj.getProgramHeaders().length));
		        			sourceFile.setText(dir);
		        			btnExtract.setEnabled(true);
		        			logger.info("You can now press the Unpack button to get the elf data content");
		        		}
		        		catch (Exception ex) {
		        			ex.printStackTrace();
		        		}
		        	}
		        }
			}
		});
		GridData gd_btnFileChoose = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnFileChoose.widthHint = 34;
		btnFileChoose.setLayoutData(gd_btnFileChoose);
		btnFileChoose.setText("...");
		btnFileChoose.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		
		Composite composite_1 = new Composite(shlElfExtractor, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(composite, 38, SWT.BOTTOM);
		fd_composite_1.top = new FormAttachment(composite, 7);
		fd_composite_1.left = new FormAttachment(composite, 0, SWT.LEFT);
		fd_composite_1.right = new FormAttachment(100, -9);
		composite_1.setLayoutData(fd_composite_1);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 112;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Number of parts : ");
		
		textNbParts = new Text(composite_1, SWT.BORDER);
		textNbParts.setEditable(false);
		
		btnExtract = new Button(composite_1, SWT.NONE);
		btnExtract.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					doUnpack(elfobj);
				}
				catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
		});
		btnExtract.setText("Unpack");
		btnExtract.setEnabled(false);
		
		Button btnClose = new Button(shlElfExtractor, SWT.NONE);
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlElfExtractor.dispose();
			}
		});
		FormData fd_btnClose = new FormData();
		fd_btnClose.top = new FormAttachment(composite_1, 6);
		fd_btnClose.right = new FormAttachment(composite, 0, SWT.RIGHT);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("Close");

	}
	
	public void doUnpack(Elf elf) throws FileNotFoundException, IOException {
		String ctype="";
		RandomAccessFile fin = new RandomAccessFile(elf.getFilename(),"r");
		ProgramHeader[] aProgramHeaders = elf.getProgramHeaders();
		int i = 0;
		for (ProgramHeader ph : aProgramHeaders) {
			fin.seek(ph.getFileOffset());
			byte[] ident = new byte[ph.getFileSize()<352?(int)ph.getFileSize():352];
			fin.read(ident);
			String identHex = HexDump.toHex(ident);
			if (identHex.contains("1F 8B"))
				ctype="ramdisk.gz";
			else if (identHex.contains("00 00 A0 E1"))
				ctype="Image";
			else if (identHex.contains("41 52 4D 64"))
				ctype="Image";
			else if (identHex.contains("51 43 44 54"))
				ctype="qcdt";
			else if (identHex.contains("53 31 5F 52 50 4D"))
				ctype="rpm.bin";
			else if (new String(ident).contains("S1_Root") || new String(ident).contains("S1_SW_Root"))
				ctype="cert";
			else if (ident.length<200) ctype="bootcmd";
			else ctype=Integer.toString(i);
			fin.seek(ph.getFileOffset());
			byte[] image = new byte[(int)ph.getFileSize()];
			fin.read(image);
			logger.info("Extracting part " + i + " to " +elf.getFilename()+"."+ctype);
			File f = new File(elf.getFilename()+"."+ctype);
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(image);
			image=null;
			fout.flush();
			fout.close();
			i++;
		}
		fin.close();
		logger.info("ELF Extraction finished");		
	}

}
