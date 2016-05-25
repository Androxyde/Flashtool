package gui;

import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorContentProvider;
import gui.models.VectorLabelProvider;
import gui.tools.USBParseJob;
import swt_components.SwtHexEdit;

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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;

public class TAUnitViewer extends Dialog {

	protected Object result;
	protected Shell shlTAUnitViewer;
	private Button btnClose;
	private Composite composite;
	private SwtHexEdit hxed;
	private StyledText styledText;
	private ScrolledComposite scrolledComposite_1;
	private Composite composite_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TAUnitViewer(Shell parent, int style) {
		super(parent, style);
		setText("TA Unit Viewer");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(byte[] content) {
		
		createContents();
		createTriggers();
		feedContent(content);
		
		
		shlTAUnitViewer.open();
		shlTAUnitViewer.layout();
		
		Display display = getParent().getDisplay();
		while (!shlTAUnitViewer.isDisposed()) {
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
		shlTAUnitViewer = new Shell(getParent(), getStyle());
		shlTAUnitViewer.setSize(260, 294);
		shlTAUnitViewer.setText("TA Unit Viewer");
		shlTAUnitViewer.setLayout(new FormLayout());
		
		btnClose = new Button(shlTAUnitViewer, SWT.NONE);
		FormData fd_btnClose = new FormData();
		fd_btnClose.bottom = new FormAttachment(100, -10);
		fd_btnClose.right = new FormAttachment(100, -10);
		btnClose.setLayoutData(fd_btnClose);
		btnClose.setText("Close");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shlTAUnitViewer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.right = new FormAttachment(0, 231);
		fd_scrolledComposite.top = new FormAttachment(0,10);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new FormLayout());
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		hxed = new SwtHexEdit(composite,SWT.BORDER,30,8,6);
		FormData fd_hxed = new FormData();
		fd_hxed.top = new FormAttachment(0);
		fd_hxed.left = new FormAttachment(0);
		fd_hxed.bottom = new FormAttachment(100);
		fd_hxed.right = new FormAttachment(100);
		hxed.setLayoutData(fd_hxed);
		
		scrolledComposite_1 = new ScrolledComposite(shlTAUnitViewer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite_1 = new FormData();
		fd_scrolledComposite_1.bottom = new FormAttachment(btnClose, -8);
		fd_scrolledComposite_1.top = new FormAttachment(scrolledComposite, 6);
		fd_scrolledComposite_1.right = new FormAttachment(100, -11);
		fd_scrolledComposite_1.left = new FormAttachment(0, 10);
		scrolledComposite_1.setLayoutData(fd_scrolledComposite_1);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);
		
		composite_1 = new Composite(scrolledComposite_1, SWT.NONE);
		
		styledText = new StyledText(composite_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setBounds(0, 0, 229, 99);
		scrolledComposite_1.setContent(composite_1);
	}

	public void createTriggers() {
		
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
				shlTAUnitViewer.dispose();
			}
		});

	}
	
	public void feedContent(byte[] content) {
		hxed.setByteData(content);
		hxed.redraw();
		styledText.setText(new String(content));
	}
}
