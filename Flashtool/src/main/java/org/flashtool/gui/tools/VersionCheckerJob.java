package org.flashtool.gui.tools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.flashtool.gui.About;
import org.flashtool.gui.TARestore;
import org.flashtool.system.OS;
import org.flashtool.system.Proxy;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionCheckerJob extends Job {

	static org.eclipse.swt.widgets.Shell _s = null;
	private boolean aborted=false;
	private boolean ended = false;
	private InputStream ustream=null;
	private HttpURLConnection uconn=null;
	

	public VersionCheckerJob(String name) {
		super(name);
	}
	
	public void setMessageFrame(org.eclipse.swt.widgets.Shell s) {
		_s = s;
	}

	public String getLatestRelease() {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = null;
			log.debug("Resolving github");
            if  (Proxy.canResolve("github.com")) {
            	log.debug("Finished resolving github. Result : Success");
            	URL u;
            	if (About.build==null) throw new Exception("no version");
            	if (OS.getChannel().equals("beta"))
            		u = new URL("https://github.com/Androxyde/Flashtool/raw/master/ant/deploy-beta.xml");
            	else
            		u = new URL("https://github.com/Androxyde/Flashtool/raw/master/gradle.properties");
            	log.debug("opening connection");
				if (!aborted)
					uconn = (HttpURLConnection) u.openConnection();
				if (!aborted)
					uconn.setConnectTimeout(5 * 1000);
				if (!aborted)
					uconn.setRequestMethod("GET");
				if (!aborted)
					uconn.connect();
			    
				log.debug("Getting stream on connection");
				if (!aborted)
					ustream = uconn.getInputStream();
				if (ustream!=null) log.debug("stream opened");
				Properties p = new Properties();
				p.load(ustream);
				ustream.close();
				return p.getProperty("flashtoolVersion");
            }
            else {
            	log.debug("Finished resolving github. Result : Failed");
            	return "";
            }
		}
		catch (Exception e) {
			return "noversion";
		}
	}
	
	protected IStatus run(IProgressMonitor monitor) {
		String netrelease = "";
		int nbretry = 0;
		while (netrelease.length()==0 && !aborted) {
			log.debug("Fetching latest release from github");
			netrelease = getLatestRelease();
			if (netrelease.length()==0) {
				if (!aborted)
					log.debug("Url content not fetched. Retrying "+nbretry+" of 10");
				nbretry++;
				if (nbretry<10) {
					try {
						Thread.sleep(1000);
					} catch (Exception e1) {}
				}
				else
					aborted=true;
			}
		}
		log.debug("out of loop");
		final String latest = netrelease;
		log.debug("Latest : " + latest);
		log.debug("Current build : "+About.build);
		ended = true;
		if (About.build!=null) {
			if (latest.length()>0 && !About.build.contains(latest)) {
				if (_s!=null) {
					Display.getDefault().syncExec(
							new Runnable() {
								public void run() {
		    		   				_s.setText(_s.getText()+"    --- New version "+latest+" available ---");
		    		   			}
		    		   		}
					);
		    	}
		    }
		}
		return Status.OK_STATUS;
   }

	public void done() {
		if (!ended) {
			ended = true;
			log.debug("aborting job");
			aborted=true;
			if (uconn!=null)
			try {
				log.debug("closing connection");
				uconn.disconnect();
			} catch (Exception e) {
				log.debug("Error : "+e.getMessage());
			}
		}
	}

}