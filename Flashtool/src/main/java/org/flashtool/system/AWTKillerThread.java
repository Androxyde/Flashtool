package org.flashtool.system;

import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AWTKillerThread extends Thread {

	boolean done = false;

	public void done() {
		done=true;
	}
	
	public void run() {
		if (OS.getName().equals("mac")) {
			this.setName("KillerAWT");
			while (!done) {
				Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
				Iterator<Thread> i =  threadSet.iterator();
				while (i.hasNext()) {
					Thread t = i.next();
					if (t.getName().startsWith("AWT")) {
						t.interrupt();
					}
				}
				Sleep(10);
			}
		}
	}

	private void Sleep(int ms) {
		try {
			sleep(ms);
		}
		catch (Exception e) {}
	}

    public void doSleep(int time) {
    	try {
    		sleep(time);
    	}
    	catch (Exception e) {}
    }

}