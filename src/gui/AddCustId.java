package gui;

import java.util.Iterator;
import java.util.Properties;

import gui.models.TableLine;
import gui.tools.WidgetTask;
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
import org.system.DeviceEntry;
import org.system.DeviceEntryModel;
import org.system.DeviceEntryModelUpdater;
import org.eclipse.swt.widgets.Combo;

public class AddCustId extends Dialog {

	protected Shell shlAddCdfID;
	private Text textID;
	private Combo comboModel;
	private Text textName;
	private DeviceEntry _entry;
	private DeviceEntryModelUpdater _model;
	Properties result=new Properties();

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AddCustId(Shell parent, int style) {
		super(parent, style);
		setText("Root Package chooser");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(DeviceEntryModelUpdater m) {
		_model=m;
		_entry = m.getDevice();
		return commonOpen();
	}

	public Object open(DeviceEntryModelUpdater m,String cda, String region) {
		_model=m;
		_entry = m.getDevice();
		result.setProperty("CDA", cda);
		result.setProperty("REGION", region);
		return commonOpen();
	}

	public Object open(DeviceEntry e) {
		_entry = e;
		return commonOpen();
	}

	public Object commonOpen() {
		createContents();
		WidgetsTool.setSize(shlAddCdfID);
		
		Label lblNewLabel = new Label(shlAddCdfID, SWT.NONE);
		lblNewLabel.setBounds(10, 44, 111, 15);
		lblNewLabel.setText("ID :");
		
		textID = new Text(shlAddCdfID, SWT.BORDER);
		textID.setBounds(10, 65, 194, 21);
		//if (result!=null) textID.setText(result.getDef().getValueOf(0));
		
		comboModel = new Combo(shlAddCdfID, SWT.NONE);
		comboModel.setBounds(60, 15, 144, 23);

		if (_model!=null) {
			comboModel.add(_model.getModel());
			comboModel.select(0);
			comboModel.setEnabled(false);
		}
		else {
			Iterator<DeviceEntryModel> imodel =_entry.getModels().iterator();
			while (imodel.hasNext()) {
				String model = imodel.next().getId();
				comboModel.add(model);
			}
		}
		
		Label lblModel = new Label(shlAddCdfID, SWT.NONE);
		lblModel.setBounds(10, 18, 55, 15);
		lblModel.setText("Model :");
		
		Label lblNewLabel_1 = new Label(shlAddCdfID, SWT.NONE);
		lblNewLabel_1.setBounds(10, 92, 55, 15);
		lblNewLabel_1.setText("Name :");
		
		textName = new Text(shlAddCdfID, SWT.BORDER);
		textName.setBounds(10, 113, 194, 21);
		
		if (result.getProperty("CDA")!=null)
			textID.setText(result.getProperty("CDA"));
		if (result.getProperty("REGION")!=null)
			textName.setText(result.getProperty("REGION"));
		
		shlAddCdfID.open();
		shlAddCdfID.layout();
		Display display = getParent().getDisplay();
		while (!shlAddCdfID.isDisposed()) {
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
		shlAddCdfID = new Shell(getParent(), getStyle());
		shlAddCdfID.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  result.clear();
		    	  event.doit = true;
		      }
		    });
		shlAddCdfID.setSize(237, 209);
		shlAddCdfID.setText("Add CdfID");
		
		Button btnCancel = new Button(shlAddCdfID, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result.clear();
				shlAddCdfID.dispose();
			}
		});
		btnCancel.setBounds(146, 143, 75, 25);
		btnCancel.setText("Cancel");
		
		Button btnOK = new Button(shlAddCdfID, SWT.NONE);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboModel.getText().length()==0 || textID.getText().length()==0 || textName.getText().length()==0)
					WidgetTask.openOKBox(shlAddCdfID, "All fields must be set");
				else {
					TableLine l = new TableLine();
					l.add(textID.getText());
					l.add(textName.getText());
					result.setProperty("CDA",textID.getText());
					result.setProperty("REGION",textName.getText());

					shlAddCdfID.dispose();
				}
			}
		});
		btnOK.setBounds(60, 143, 75, 25);
		btnOK.setText("Ok");

	}

}
