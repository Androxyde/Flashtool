package org.system;

import java.io.InputStream;
import java.util.Scanner;
import org.adb.AdbUtility;
import org.logger.MyLogger;

public class AdbPhoneThread extends Thread {
	
	String currentPid="none";
	boolean done=false;

	private ProcessBuilder builder;
	private InputStream processInput;
	private Scanner sc;
	private StatusListener listener;
	boolean first = true;

	public void done() {
		done=true;
	}
	
	public void run() {
		this.setName("AdbPhoneThread");
		try {
			builder = new ProcessBuilder(OS.getAdbPath(), "status-window");
			final Process adb = builder.start();
		    Thread t = new Thread() {
		    	  public void run() {
		    		  this.setName("AdbProcessReader");
				      processInput = adb.getInputStream();
				      boolean adbok = false;
				      sc = new Scanner(processInput);
				      DeviceIdent id = null;
				      DeviceIdent newid = null;
			    	  while (sc.hasNextLine()) {
			    		  adbok=true;
			    		  String line = sc.nextLine();
			    		  if (line.contains("State")) {
				    		  if (line.contains("device")) {
				    			  DeviceProperties.reload();
				    			  id = Device.getLastConnected(first);
				    			  newid = Device.getLastConnected(first);
				    			  if (!GlobalState.getState(newid.getSerial(), newid.getPid()).equals("adb")) {
				    				  int count=0;
				    				  if (first) count=19;
				    				  while (newid.getPid().equals(id.getPid())) {
				    					  Sleep(100);
				    					  count++;
				    					  newid = Device.getLastConnected(first);
				    					  if (count==20) break;
				    				  }
				    				  GlobalState.setState(newid.getSerial(), newid.getPid(), "adb");
				    			  }
				    			  fireStatusChanged(new StatusEvent(GlobalState.getState(newid.getSerial(), newid.getPid()),true));
				    		  }
				    		  if (first) {
				    			  DeviceChangedListener.start();
				    			  DeviceChangedListener.addStatusListener(listener);
				    			  first = false;
				    		  }
			    		  }
			    	  }
			    	  if (!adbok) {
			    		  Scanner scerr = new Scanner(adb.getErrorStream());
			    		  while (scerr.hasNextLine()) MyLogger.getLogger().error(scerr.nextLine());
			    	  }
				      
		    	  }
		    };
		    t.start();
			while (!done) {
				int count=1;
				while (count<2001 && !done) {
					sleep(1);
					count++;
				}
			}
			adb.destroy();
			try {
				AdbUtility.killServer();
			}
			catch (Exception e) {
			}
		}
		catch (Exception e) {
			MyLogger.getLogger().error(e.getMessage());
		}
	}

	private void Sleep(int ms) {
		try {
			sleep(ms);
		}
		catch (Exception e) {
		}
	}
	public void addStatusListener(StatusListener plistener) {
        listener = plistener;
    }

    public void removeStatusListener(StatusListener listener) {
        listener = null;
    }

    public StatusListener getStatusListeners() {
        return listener;
    }

    protected void fireStatusChanged(StatusEvent e) {
    	if (listener != null)
		    listener.statusChanged(e);
    }

    public void doSleep(int time) {
    	try {
    		sleep(time);
    	}
    	catch (Exception e) {}
    }

}