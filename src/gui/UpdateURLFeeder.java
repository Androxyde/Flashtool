package gui;

import gui.tools.WidgetsTool;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UpdateURLFeeder extends Dialog {

	protected Object result;
	protected Shell shlUpdateUrlFeeder;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UpdateURLFeeder(Shell parent, int style) {
		super(parent, style);
		setText("Root Package chooser");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetsTool.setSize(shlUpdateUrlFeeder);
		
		Label lblNewLabel = new Label(shlUpdateUrlFeeder, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 111, 15);
		lblNewLabel.setText("Enter update URL :");
		
		text = new Text(shlUpdateUrlFeeder, SWT.BORDER);
		text.setBounds(10, 31, 339, 21);
		shlUpdateUrlFeeder.open();
		shlUpdateUrlFeeder.layout();
		Display display = getParent().getDisplay();
		while (!shlUpdateUrlFeeder.isDisposed()) {
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
		shlUpdateUrlFeeder = new Shell(getParent(), getStyle());
		shlUpdateUrlFeeder.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  result = "";
		    	  event.doit = true;
		      }
		    });
		shlUpdateUrlFeeder.setSize(365, 128);
		shlUpdateUrlFeeder.setText("Update URL Feeder");
		
		Button btnCancel = new Button(shlUpdateUrlFeeder, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = "";
				shlUpdateUrlFeeder.dispose();
			}
		});
		btnCancel.setBounds(274, 64, 75, 25);
		btnCancel.setText("Cancel");
		
		Button btnOK = new Button(shlUpdateUrlFeeder, SWT.NONE);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = text.getText(); 
				shlUpdateUrlFeeder.dispose();
			}
		});
		btnOK.setBounds(193, 64, 75, 25);
		btnOK.setText("Ok");

	}
}
