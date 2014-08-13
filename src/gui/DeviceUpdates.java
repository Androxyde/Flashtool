package gui;

import gui.models.CustIdItem;
import gui.models.ModelUpdater;
import gui.models.Models;
import gui.models.TableLine;
import gui.models.TableSorter;
import gui.models.VectorContentProvider;
import gui.models.VectorLabelProvider;
import gui.tools.DecryptJob;
import gui.tools.WidgetTask;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.system.DeviceEntry;
import org.system.OS;
import org.system.URLDownloader;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.logger.LogProgress;

import com.iagucool.xperifirm.CDFInfoLoader;
import com.iagucool.xperifirm.FileSet;
import com.iagucool.xperifirm.Firmware;

public class DeviceUpdates extends Dialog {

	protected Object result;
	protected Shell shlDeviceUpdateChecker;
	protected CTabFolder tabFolder;
	protected DeviceEntry _entry;
	protected Label lblInfo;
	protected Button closeButton;
	//protected CTabItem tabItem;
	//private Table tableDevice;
	//private TableViewer tableViewer;
	protected Models models;
	private static Logger logger = Logger.getLogger(DeviceUpdates.class);

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DeviceUpdates(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(DeviceEntry entry) {
		_entry = entry;
		createContents();
		shlDeviceUpdateChecker.open();
		shlDeviceUpdateChecker.layout();
		Display display = getParent().getDisplay();
		while (!shlDeviceUpdateChecker.isDisposed()) {
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
		shlDeviceUpdateChecker = new Shell(getParent(), getStyle());
		shlDeviceUpdateChecker.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  event.doit=closeButton.isEnabled();
		      }
		    });

		shlDeviceUpdateChecker.setSize(450, 300);
		shlDeviceUpdateChecker.setText("Device Update Checker");
		
		tabFolder = new CTabFolder(shlDeviceUpdateChecker, SWT.BORDER);
		tabFolder.setBounds(11, 10, 423, 223);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));				
		
		closeButton = new Button(shlDeviceUpdateChecker, SWT.NONE);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDeviceUpdateChecker.dispose();
			}
		});
		closeButton.setBounds(359, 239, 75, 25);
		closeButton.setText("Close");
		
		lblInfo = new Label(shlDeviceUpdateChecker, SWT.NONE);
		lblInfo.setBounds(11, 244, 342, 15);

		fillMap();
		FillJob fj = new FillJob("Update Search");
		fj.schedule();
	}

	public void fillMap() {
		models = _entry.getUpdatableModels(false);
	}

	public void addTab(final String tabtitle) {
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						Vector<TableLine> result = new Vector<TableLine>();
						ModelUpdater mu = (ModelUpdater)models.get(tabtitle);
						CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
						tabItem.setText(tabtitle.length()>0?tabtitle:_entry.getId());
						TableViewer tableViewer = new TableViewer(tabFolder,SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);						
						tableViewer.setContentProvider(new VectorContentProvider());
						tableViewer.setLabelProvider(new VectorLabelProvider());

						// Create the popup menu
						  MenuManager menuMgr = new MenuManager();
						  Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
						  menuMgr.addMenuListener(new IMenuListener() {
						    @Override
						    public void menuAboutToShow(IMenuManager manager) {
						    	  if (closeButton.isEnabled()) {
								    	TableLine tl = (TableLine)tableViewer.getTable().getSelection()[0].getData();
								    	if (tl.getValueOf(2).length()==0) {
									    	manager.add(new Action("Check release") {
									            public void run() {
									            	doCheck(tableViewer,tl,mu);
									            }
									        });
								    	}
								    	else {
									    	manager.add(new Action("Download") {
									            public void run() {
									            	doDownload(tl,mu);
									            }
									        });
								    	}
						    	  }
						    }
						  });

						menuMgr.setRemoveAllWhenShown(true);
						tableViewer.getControl().setMenu(menu);

						
						TableColumn[] columns = new TableColumn[3];
						columns[0] = new TableColumn(tableViewer.getTable(), SWT.NONE);
						columns[0].setText("Id");
						columns[1] = new TableColumn(tableViewer.getTable(), SWT.NONE);
						columns[1].setText("Branding");
						columns[2] = new TableColumn(tableViewer.getTable(), SWT.NONE);
						columns[2].setText("Version");
						tableViewer.getTable().setHeaderVisible(true);
						tableViewer.getTable().setLinesVisible(true);
						TableSorter sort = new TableSorter(tableViewer);
						tableViewer.getTable().setSortColumn(tableViewer.getTable().getColumn(0));
						tableViewer.getTable().setSortDirection(SWT.UP);
						tableViewer.setInput(result);
						tableViewer.getTable().addListener(SWT.DefaultSelection, new Listener() {
						      public void handleEvent(Event e) {
						    	  if (closeButton.isEnabled()) {
							    	  TableLine tl = (TableLine)tableViewer.getTable().getSelection()[0].getData();
							    	  if (tl.getValueOf(2).length()==0) {
							    		  	doCheck(tableViewer,tl,mu);
									  }
							    	  else {
							    		  	doDownload(tl,mu);
							    	  }
						    	  }
						      }
						    });

						Iterator cdflist = mu.getCustIds().getProperties().keySet().iterator();
						while (cdflist.hasNext()) {
							String id = (String)cdflist.next();
							TableLine line1 = new TableLine();
							line1.add(id);
							line1.add(mu.getCustIds().getProperty(id));
							line1.add("");
							result.add(line1);
							tableViewer.refresh();
						}
										tableViewer.setInput(result);
										for (int i = 0, n = tableViewer.getTable().getColumnCount(); i < n; i++) {
											tableViewer.getTable().getColumn(i).pack();
										}
										tableViewer.getTable().pack();
										tableViewer.refresh();
										tabItem.setControl(tableViewer.getTable());

					}
				}
		);
		
	}

	public void doDownload(TableLine tl, ModelUpdater mu) {
    	DownloadJob dj = new DownloadJob("Download FW");
    	dj.setCDF(tl.getValueOf(0));
    	String path = OS.getWorkDir()+File.separator+"firmwares"+File.separator+"Downloads"+File.separator+mu.getModel()+"_"+tl.getValueOf(1).replaceAll(" ","_") + "_" + mu.getReleaseOf(tl.getValueOf(0));
    	dj.setPath(path);
    	dj.setUpdater(mu);
    	dj.schedule();		
	}

	public void doCheck(TableViewer tableViewer, TableLine tl, ModelUpdater mu) {
		CheckJob cj = new CheckJob("Release checker");
		cj.setTableLine(tl);
		cj.setTableViewer(tableViewer);
		cj.setModelUpdater(mu);
		cj.schedule();
	}
	
	public void fillTab() {
		Iterator imodels = models.keySet().iterator();
		while (imodels.hasNext()) {
			addTab((String)imodels.next());
		}
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						tabFolder.redraw();
						tabFolder.setSelection(0);
					}
				}
		);
	}

	class FillJob extends Job {

		boolean canceled = false;

		public FillJob(String name) {
			super(name);
		}
		
		public void stopSearch() {
			canceled=true;
		}
		
	    protected IStatus run(IProgressMonitor monitor) {
			    while (!canceled) {
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									lblInfo.setText("Searching for updates. Please wait");
								}
							}
					);
					fillTab();
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									lblInfo.setText("");
								}
							}
					);
					return Status.OK_STATUS;
			    }
			    return Status.CANCEL_STATUS;
	    }
	}

	class CheckJob extends Job {

		boolean canceled = false;
		TableLine tl=null;
		TableViewer tableViewer=null;
		ModelUpdater mu=null;

		public void setTableLine(TableLine ptl) {
			tl = ptl;
		}
		
		public void setTableViewer(TableViewer ptv) {
			tableViewer = ptv;
		}
		
		public void setModelUpdater(ModelUpdater pmu) {
			mu = pmu;
		}
		
		public CheckJob(String name) {
			super(name);
			this.addJobChangeListener(new JobChangeAdapter(){
				public void done(IJobChangeEvent event) {
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									closeButton.setEnabled(true);
									lblInfo.setText("");
								}
							}
					);
					LogProgress.initProgress(0);
				}
			});
		}
		
		public void stopSearch() {
			canceled=true;
		}
		
		public void setUpdater(ModelUpdater pmu) {
			mu=pmu;
		}
		
	    protected IStatus run(IProgressMonitor monitor) {
			Display.getDefault().asyncExec(
					new Runnable() {
						public void run() {
							closeButton.setEnabled(false);
							lblInfo.setText("Checking latest release. Please wait ...");
						}
					}
			);
	    	String release = mu.getReleaseOf(tl.getValueOf(0));
			Display.getDefault().asyncExec(
					new Runnable() {
						public void run() {
							tl.setValueOf(2, release);
							int lastsize = tableViewer.getControl().getSize().x-tableViewer.getTable().getColumn(0).getWidth()-tableViewer.getTable().getColumn(1).getWidth();
							tableViewer.getTable().getColumn(2).setWidth(lastsize-20);
						    tableViewer.refresh();
						}
					}
			);
			return Status.OK_STATUS;
	    }
	}
	
	class DownloadJob extends Job {

		boolean canceled = false;
		String cdfval;
		ModelUpdater mu=null;
		String _path = "";

		public DownloadJob(String name) {
			super(name);
			this.addJobChangeListener(new JobChangeAdapter(){
				public void done(IJobChangeEvent event) {
					Display.getDefault().asyncExec(
							new Runnable() {
								public void run() {
									closeButton.setEnabled(true);
									lblInfo.setText("");
								}
							}
					);
					LogProgress.initProgress(0);
				}
			});
		}
		
		public void stopSearch() {
			canceled=true;
		}
		
		public void setUpdater(ModelUpdater pmu) {
			mu=pmu;
		}
		
		public void setCDF(String cdf) {
			cdfval = cdf;
		}
		
		public void setPath(String path) {
			_path = path;
			logger.info("Saving firmware to " + _path);
		}
		
	    protected IStatus run(IProgressMonitor monitor) {
			Display.getDefault().asyncExec(
					new Runnable() {
						public void run() {
							closeButton.setEnabled(false);
							lblInfo.setText("Downloading latest release. Please wait ...");
						}
					}
			);
            	Firmware v = mu.getFilesOf(cdfval);
            	Iterator<FileSet> i = v.getFileSets().iterator();
            	try {
            		Vector result = new Vector();
            		while (i.hasNext()) {
            			FileSet f = i.next();
            			f.setFolder(_path);
            			f.download();
            			result.add(new File(_path+File.separator+f.getName()));	
            		}
            		System.out.println(result);
    				//Decrypt decrypt = new Decrypt(shlDeviceUpdateChecker,SWT.PRIMARY_MODAL | SWT.SHEET);
    				if (result!=null) {
    					DecryptJob dec = new DecryptJob("Decrypt");
    					dec.addJobChangeListener(new IJobChangeListener() {
    						public void aboutToRun(IJobChangeEvent event) {
    						}

    						public void awake(IJobChangeEvent event) {
    						}

    						public void done(IJobChangeEvent event) {
    							String result = WidgetTask.openBundleCreator(shlDeviceUpdateChecker,_path);
    							if (result.equals("Cancel"))
    								logger.info("Bundle creation canceled");
    						}

    						public void running(IJobChangeEvent event) {
    						}

    						public void scheduled(IJobChangeEvent event) {
    						}

    						public void sleeping(IJobChangeEvent event) {
    						}
    					});

    					dec.setFiles(result);
    					dec.schedule();
    				}
            	}
            	catch (IOException ioe) {
            		ioe.printStackTrace();
            	}
			    return Status.OK_STATUS;
	    }
	}
}