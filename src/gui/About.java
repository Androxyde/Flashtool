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

public class About extends Dialog {

	protected Object result;
	protected Shell shlAbout;
	public static String build = About.class.getPackage().getImplementationVersion();
	static final Logger logger = LogManager.getLogger(About.class);

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
		shlAbout.setSize(450, 246);
		shlAbout.setText("About");
		shlAbout.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(shlAbout, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Xperia flashing tool "+OS.getChannel());
		
		Label lblNewLabel_1 = new Label(shlAbout, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("Java Version " + System.getProperty("java.version") + " " + System.getProperty("sun.arch.data.model") + "bits Edition");
		
		Label lblNewLabel_2 = new Label(shlAbout, SWT.NONE);
		fd_lblNewLabel.bottom = new FormAttachment(100, -192);
		lblNewLabel_2.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_2.bottom = new FormAttachment(100, -171);
		fd_lblNewLabel_2.left = new FormAttachment(0, 10);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(getVersion());
		
		Label lblNewLabel_3 = new Label(shlAbout, SWT.NONE);
		fd_lblNewLabel_1.bottom = new FormAttachment(lblNewLabel_3, -6);
		lblNewLabel_3.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_3.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("OS Version "+OS.getVersion());
		
		Label lblNewLabel_4 = new Label(shlAbout, SWT.NONE);
		fd_lblNewLabel_3.bottom = new FormAttachment(100, -129);
		lblNewLabel_4.setAlignment(SWT.CENTER);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_4.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText("By Androxyde");
		
		Button btnNewButton = new Button(shlAbout, SWT.NONE);
		fd_lblNewLabel.right = new FormAttachment(btnNewButton, 0, SWT.RIGHT);
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
		
		Link link = new Link(shlAbout, SWT.NONE);
		fd_lblNewLabel_4.bottom = new FormAttachment(100, -108);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://www.flashtool.net");
			}
		});
		FormData fd_link = new FormData();
		fd_link.top = new FormAttachment(lblNewLabel_4, 6);
		fd_link.right = new FormAttachment(100, -172);
		fd_link.left = new FormAttachment(0, 194);
		link.setLayoutData(fd_link);
		link.setText("<a href=\"http://androxyde.github.com\">Homepage</a>");
		
		Label lblManyThanksTo = new Label(shlAbout, SWT.NONE);
		lblManyThanksTo.setAlignment(SWT.CENTER);
		FormData fd_lblManyThanksTo = new FormData();
		fd_lblManyThanksTo.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblManyThanksTo.left = new FormAttachment(0, 10);
		lblManyThanksTo.setLayoutData(fd_lblManyThanksTo);
		lblManyThanksTo.setText("Many thanks to contributors : Bin4ry, DooMLoRD, [NUT],");
		
		Label lblDevshaft = new Label(shlAbout, SWT.NONE);
		lblDevshaft.setAlignment(SWT.CENTER);
		fd_lblManyThanksTo.bottom = new FormAttachment(100, -57);
		FormData fd_lblDevshaft = new FormData();
		fd_lblDevshaft.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblDevshaft.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblDevshaft.top = new FormAttachment(lblManyThanksTo, 6);
		lblDevshaft.setLayoutData(fd_lblDevshaft);
		lblDevshaft.setText("DevShaft, IaguCool");
	}

	public static String getVersion() {
		if (build == null) return " run from eclipse";
		return build;
	}
}
