package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import flashsystem.Bundle;
import gui.tools.WidgetsTool;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FTFSelector1 extends Dialog {

	protected Bundle result;
	protected Shell shlFirmwareSelector;
	private boolean simulate = false;
	private int maxbuffer = 0;
	private Text textSourceFolder;
	private Composite compositeSearch;
	private Composite compositeContent;
	private Composite compositeFirmware;
	private Label lblNewLabel;
	private Label lblNewLabel_1;
	private Table table;
	private Label lblNewLabel_2;
	private ScrolledComposite scrolledComposite_1;
	private Label lblNewLabel_3;
	private ScrolledComposite scrolledComposite_2;
	private ScrolledComposite scrolledComposite_3;
	private Composite composite;
	private Composite composite_1;
	private Composite composite_2;
	private Composite composite_3;
	private Label lblNewLabel_4;
	private Label lblNewLabel_5;
	private Label lblNewLabel_6;
	private Label lblNewLabel_7;
	private Composite compositeSettings;
	private Composite compositeExclude;
	private Button btnNewButton;
	private Button btnNewButton_1;
	private Tree tree;
	private FormData fd_tree;
	
	public FTFSelector1(Shell parent, int style) {
		super(parent, style);
		setText("Firmware selector");
	}

	public Object open(String pathname, String ftfname) {
		
		createContents(pathname, ftfname);

		WidgetsTool.setSize(shlFirmwareSelector);
		
		shlFirmwareSelector.open();
		shlFirmwareSelector.layout();
		Display display = getParent().getDisplay();
		while (!shlFirmwareSelector.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (result!=null) {
			result.setSimulate(simulate);
			result.setMaxBuffer(maxbuffer);
		}
		return result;
	}

	private void createContents(String pathname, String ftfname) {
		
		// Creation du Shell
		shlFirmwareSelector = new Shell(getParent(), getStyle());
		shlFirmwareSelector.setSize(714, 496);
		shlFirmwareSelector.setText("Firmware Selector");
		shlFirmwareSelector.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
					result = null;
					shlFirmwareSelector.dispose();
		      }
		    });
		shlFirmwareSelector.setLayout(new FormLayout());
		
		// Search bar
		compositeSearch = new Composite(shlFirmwareSelector, SWT.NONE);
		compositeSearch.setLayout(new GridLayout(3, false));
		FormData fd_compositeSearch = new FormData();
		fd_compositeSearch.top = new FormAttachment(0);
		fd_compositeSearch.left = new FormAttachment(0, 10);
		fd_compositeSearch.bottom = new FormAttachment(0, 31);
		fd_compositeSearch.right = new FormAttachment(0, 698);
		compositeSearch.setLayoutData(fd_compositeSearch);
		
		Label lblSourceFolder = new Label(compositeSearch, SWT.NONE);
		lblSourceFolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSourceFolder.setText("Source folder :");
		
		textSourceFolder = new Text(compositeSearch, SWT.BORDER);
		GridData gd_textSourceFolder = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textSourceFolder.widthHint = 563;
		textSourceFolder.setLayoutData(gd_textSourceFolder);
		
		Button btnSourcefolder = new Button(compositeSearch, SWT.NONE);
		btnSourcefolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnSourcefolder.setText("...");

		compositeFirmware = new Composite(shlFirmwareSelector, SWT.NONE);
		compositeFirmware.setLayout(new FormLayout());
		FormData fd_compositeFirmware = new FormData();
		fd_compositeFirmware.bottom = new FormAttachment(compositeSearch, 393, SWT.BOTTOM);
		fd_compositeFirmware.top = new FormAttachment(compositeSearch, 6);
		fd_compositeFirmware.left = new FormAttachment(0, 10);
		fd_compositeFirmware.right = new FormAttachment(0, 206);
		compositeFirmware.setLayoutData(fd_compositeFirmware);

		compositeContent = new Composite(shlFirmwareSelector, SWT.NONE);
		FormData fd_compositeContent = new FormData();
		fd_compositeContent.bottom = new FormAttachment(compositeFirmware, 0, SWT.BOTTOM);
		fd_compositeContent.top = new FormAttachment(compositeSearch, 6);
		fd_compositeContent.left = new FormAttachment(compositeFirmware, 6);
		compositeContent.setLayoutData(fd_compositeContent);
		
		Composite compositeWipe = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeContent.right = new FormAttachment(compositeWipe, -6);
		FormData fd_compositeWipe = new FormData();
		fd_compositeWipe.top = new FormAttachment(compositeSearch, 6);
		fd_compositeWipe.right = new FormAttachment(compositeSearch, 0, SWT.RIGHT);
		fd_compositeWipe.left = new FormAttachment(0, 390);
		compositeWipe.setLayoutData(fd_compositeWipe);
		
		compositeExclude = new Composite(shlFirmwareSelector, SWT.NONE);
		fd_compositeWipe.bottom = new FormAttachment(compositeExclude, -6);
		
		lblNewLabel_2 = new Label(compositeWipe, SWT.NONE);
		lblNewLabel_2.setBounds(10, 10, 55, 15);
		lblNewLabel_2.setText("Wipe");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(compositeWipe, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 31, 152, 158);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		lblNewLabel_4 = new Label(composite, SWT.NONE);
		lblNewLabel_4.setText("Sin files");
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		scrolledComposite_1 = new ScrolledComposite(compositeWipe, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite_1.setBounds(158, 31, 150, 158);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);
		
		composite_1 = new Composite(scrolledComposite_1, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		
		lblNewLabel_5 = new Label(composite_1, SWT.NONE);
		lblNewLabel_5.setText("Misc TA");
		scrolledComposite_1.setContent(composite_1);
		scrolledComposite_1.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		FormData fd_compositeExclude = new FormData();
		fd_compositeExclude.bottom = new FormAttachment(compositeFirmware, 0, SWT.BOTTOM);
		fd_compositeExclude.left = new FormAttachment(compositeContent, 6);
		fd_compositeExclude.right = new FormAttachment(100, -10);
		fd_compositeExclude.top = new FormAttachment(0, 232);
		
		lblNewLabel = new Label(compositeFirmware, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Firmwares");
		
		TreeViewer treeViewer = new TreeViewer(compositeFirmware, SWT.BORDER);
		tree = treeViewer.getTree();
		fd_tree = new FormData();
		fd_tree.top = new FormAttachment(lblNewLabel, 6);
		fd_tree.bottom = new FormAttachment(100);
		fd_tree.left = new FormAttachment(0);
		fd_tree.right = new FormAttachment(0, 196);
		tree.setLayoutData(fd_tree);
		
		lblNewLabel_1 = new Label(compositeContent, SWT.NONE);
		lblNewLabel_1.setBounds(10, 10, 55, 15);
		lblNewLabel_1.setText("Content");
		
		TableViewer tableViewer = new TableViewer(compositeContent, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setBounds(0, 31, 172, 356);
		compositeExclude.setLayoutData(fd_compositeExclude);
		
		lblNewLabel_3 = new Label(compositeExclude, SWT.NONE);
		lblNewLabel_3.setBounds(10, 10, 55, 15);
		lblNewLabel_3.setText("Exclude");
		
		scrolledComposite_2 = new ScrolledComposite(compositeExclude, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite_2.setBounds(0, 31, 152, 161);
		scrolledComposite_2.setExpandHorizontal(true);
		scrolledComposite_2.setExpandVertical(true);
		
		composite_2 = new Composite(scrolledComposite_2, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		
		lblNewLabel_6 = new Label(composite_2, SWT.NONE);
		lblNewLabel_6.setText("Sin files");
		scrolledComposite_2.setContent(composite_2);
		scrolledComposite_2.setMinSize(composite_2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		scrolledComposite_3 = new ScrolledComposite(compositeExclude, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite_3.setBounds(158, 31, 150, 161);
		scrolledComposite_3.setExpandHorizontal(true);
		scrolledComposite_3.setExpandVertical(true);
		
		composite_3 = new Composite(scrolledComposite_3, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));
		
		lblNewLabel_7 = new Label(composite_3, SWT.NONE);
		lblNewLabel_7.setText("Misc TA");
		scrolledComposite_3.setContent(composite_3);
		scrolledComposite_3.setMinSize(composite_3.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeSettings = new Composite(shlFirmwareSelector, SWT.NONE);
		FormData fd_compositeSettings = new FormData();
		fd_compositeSettings.bottom = new FormAttachment(compositeFirmware, 33, SWT.BOTTOM);
		fd_compositeSettings.top = new FormAttachment(compositeFirmware, 6);
		
		Button btnNewButton_2 = new Button(compositeFirmware, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.bottom = new FormAttachment(100, -357);
		fd_btnNewButton_2.right = new FormAttachment(lblNewLabel, 102, SWT.RIGHT);
		fd_btnNewButton_2.left = new FormAttachment(lblNewLabel, 19);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);
		btnNewButton_2.setText("Device filter");
		fd_compositeSettings.right = new FormAttachment(compositeSearch, 0, SWT.RIGHT);
		fd_compositeSettings.left = new FormAttachment(0, 10);
		compositeSettings.setLayoutData(fd_compositeSettings);
		
		btnNewButton = new Button(compositeSettings, SWT.NONE);
		btnNewButton.setBounds(613, 0, 75, 25);
		btnNewButton.setText("Cancel");
		
		btnNewButton_1 = new Button(compositeSettings, SWT.NONE);
		btnNewButton_1.setBounds(532, 0, 75, 25);
		btnNewButton_1.setText("Flash");
		
		Button btnCheckButton = new Button(compositeSettings, SWT.CHECK);
		btnCheckButton.setBounds(0, 4, 75, 16);
		btnCheckButton.setText("Simulate");
		
		Button btnCheckButton_1 = new Button(compositeSettings, SWT.CHECK);
		btnCheckButton_1.setBounds(92, 4, 155, 16);
		btnCheckButton_1.setText("Disable final verification");
		
		Combo combo = new Combo(compositeSettings, SWT.NONE);
		combo.setBounds(379, 2, 91, 23);
		
		Label lblNewLabel_8 = new Label(compositeSettings, SWT.NONE);
		lblNewLabel_8.setBounds(281, 5, 92, 15);
		lblNewLabel_8.setText("Max USB buffer");

	}
}
