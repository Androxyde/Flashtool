package gui.tools;

import java.util.Properties;

import flashsystem.X10flash;
import gui.BLUWizard;
import gui.BootModeSelector;
import gui.BundleCreator;
import gui.BusyboxSelector;
import gui.DeviceSelector;
import gui.LoaderSelect;
import gui.RootPackageSelector;
import gui.TABackupSelector;
import gui.TABackupSet;
import gui.VariantSelector;
import gui.WaitDeviceForFastboot;
import gui.WaitDeviceForFlashmode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.system.Devices;

public class WidgetTask {
	
	public static void setEnabled(final ToolItem item, final boolean status) {
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						item.setEnabled(status);
					}
				}
		);
	}

	public static void setMenuName(final MenuItem item, final String text) {
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						item.setText(text);
					}
				}
		);
	}

	public static void setEnabled(final MenuItem item, final boolean status) {
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						WidgetTask.setMenuEnabled(item, status);
					}
				}
		);
	}

	private static void setMenuEnabled(MenuItem item, boolean status) {
		if (item.getMenu()!=null) {;
		MenuItem[] mit = item.getMenu().getItems();
		for (int i=0;i<mit.length;i++)
			WidgetTask.setMenuEnabled(mit[i],status);
		}
		item.setEnabled(status);	
	}

	public static void setEnabled(final Button item, final boolean status) {
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						item.setEnabled(status);
					}
				}
		);
	}

	public static String openDeviceSelector(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		DeviceSelector dial = new DeviceSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
			    		if (obj==null) obj = new String("");
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}
	public static String openVariantSelector(final String devid, final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						VariantSelector vs = new VariantSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
						String obj = (String)vs.open(Devices.getDevice(devid).getVariantList());
			    		if (obj==null) obj = new String(devid);
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}


	public static String openBusyboxSelector(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		BusyboxSelector dial = new BusyboxSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
			    		if (obj==null) obj = new String("");
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openDeviceSelector(final Shell parent, final Properties p) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		DeviceSelector dial = new DeviceSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open(p);
			    		if (obj==null) obj = new String("");
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openLoaderSelect(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		LoaderSelect dial = new LoaderSelect(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openBundleCreator(final Shell parent, final String folder) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		BundleCreator cre = new BundleCreator(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = cre.open(folder);
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openBLWizard(final Shell parent, final String serial, final String imei, final String ulcode, final X10flash flash, final String mode) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						BLUWizard wiz = new BLUWizard(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = wiz.open(serial,imei,ulcode,flash,mode);
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	
	public static String openBootModeSelector(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		BootModeSelector dial = new BootModeSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openRootPackageSelector(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		RootPackageSelector dial = new RootPackageSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}


	public static String openTABackupSet(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		TABackupSet dial = new TABackupSet(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openTABackupSelector(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		TABackupSelector dial = new TABackupSelector(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openWaitDeviceForFlashmode(final Shell parent, final X10flash flash) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		WaitDeviceForFlashmode dial = new WaitDeviceForFlashmode(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open(flash);
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openWaitDeviceForFastboot(final Shell parent) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
			    		WaitDeviceForFastboot dial = new WaitDeviceForFastboot(parent,SWT.PRIMARY_MODAL | SWT.SHEET);
			    		Object obj = dial.open();
						res.setResult(obj);
						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openOKBox(final Shell parent,final String message) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						MessageBox mb = new MessageBox(parent,SWT.ICON_INFORMATION|SWT.OK);
						mb.setText("Information");
						mb.setMessage(message);
						int result = mb.open();
						res.setResult(String.valueOf(result));						
					}
				}
		);
		return (String)res.getResult();
	}

	public static String openYESNOBox(final Shell parent,final String message) {
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						MessageBox mb = new MessageBox(parent,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
						mb.setText("Question");
						mb.setMessage(message);
						int result = mb.open();
						res.setResult(String.valueOf(result));						
					}
				}
		);
		return (String)res.getResult();
	}

	public static class Result {
		
		private Object _res=null;
		
		public Object getResult() {
			return _res;
		}
		
		public void setResult(Object res) {
			_res = res;
		}
	
	}
}
