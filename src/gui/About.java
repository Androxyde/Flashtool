package gui;

import gui.tools.WidgetsTool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.system.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class About extends Dialog {

	protected Object result;
	protected Shell shlAbout;
	public static String build = About.class.getPackage().getImplementationVersion();
	static final Logger logger = LogManager.getLogger(About.class);
	private Label lblNewLabel;
	private Label lblNewLabel_2;
	private Label lblNewLabel_1;
	private Label lblNewLabel_3;
	private Label lblNewLabel_4;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public About(Shell parent, int style) {
		super(parent, style);
		setText("About");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlAbout);
		
		Composite composite = new Composite(shlAbout, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		
		lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setText("Xperia flashing tool "+OS.getChannel());
		
		lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setAlignment(SWT.CENTER);
		lblNewLabel_2.setText(getVersion());
		
		lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setText("Java Version " + System.getProperty("java.version") + " " + System.getProperty("sun.arch.data.model") + "bits Edition");
		
		lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setAlignment(SWT.CENTER);
		lblNewLabel_3.setText("OS Version "+OS.getVersion());
		
		lblNewLabel_4 = new Label(composite, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setAlignment(SWT.CENTER);
		lblNewLabel_4.setText("By Androxyde");
		
		Label lblManyThanksTo = new Label(composite, SWT.NONE);
		lblManyThanksTo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblManyThanksTo.setAlignment(SWT.CENTER);
		lblManyThanksTo.setText("Many thanks to contributors : Bin4ry, DooMLoRD, [NUT],");
		
		Label lblDevshaft = new Label(composite, SWT.NONE);
		lblDevshaft.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblDevshaft.setAlignment(SWT.CENTER);
		lblDevshaft.setText("DevShaft, IaguCool");
		
		Link link = new Link(composite, SWT.NONE);
		link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://www.flashtool.net");
			}
		});
		link.setText("<a href=\"http://androxyde.github.com\">Homepage</a>");
		shlAbout.open();
		shlAbout.layout();
		Display display = getParent().getDisplay();
		while (!shlAbout.isDisposed()) {
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
		shlAbout = new Shell(getParent(), getStyle());
		shlAbout.setSize(391, 252);
		shlAbout.setText("About");
		shlAbout.setLayout(new FormLayout());
		
		Button btnNewButton = new Button(shlAbout, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlAbout.dispose();
			}
		});
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.bottom = new FormAttachment(100, -10);
		fd_btnNewButton.right = new FormAttachment(100, -10);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Close");
	}

	public static String getVersion() {
		if (build == null) return " run from eclipse";
		return build;
	}
}
