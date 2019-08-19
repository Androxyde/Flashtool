package gui.tools;

import gui.About;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.system.OS;
import org.system.Proxy;

public class VersionCheckerJob extends Job {

	static org.eclipse.swt.widgets.Shell _s = null;
	private boolean aborted=false;
	private boolean ended = false;
	private InputStream ustream=null;
	private HttpURLConnection uconn=null;
	static final Logger logger = LogManager.getLogger(VersionCheckerJob.class);
	

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
			logger.debug("Resolving github");
            if  (Proxy.canResolve("github.com")) {
            	logger.debug("Finished resolving github. Result : Success");
            	URL u;
            	if (About.build==null) throw new Exception("no version");
            	if (OS.getChannel().equals("beta"))
            		u = new URL("https://github.com/Androxyde/Flashtool/raw/master/ant/deploy-beta.xml");
            	else
            		u = new URL("https://github.com/Androxyde/Flashtool/raw/master/ant/deploy-release.xml");
            	logger.debug("opening connection");
				if (!aborted)
					uconn = (HttpURLConnection) u.openConnection();
				if (!aborted)
					uconn.setConnectTimeout(5 * 1000);
				if (!aborted)
					uconn.setRequestMethod("GET");
				if (!aborted)
					uconn.connect();
			    
				logger.debug("Getting stream on connection");
				if (!aborted)
					ustream = uconn.getInputStream();
				if (ustream!=null) logger.debug("stream opened");
				doc = builder.build(ustream);
				Iterator<Element> mainitr = doc.getRootElement().getChildren().iterator();
				while (mainitr.hasNext()) {
					Element e = mainitr.next();
					if (e.getName().equals("property"))
						if (e.getAttributeValue("name").equals("version")) {
							ustream.close();
							return e.getAttributeValue("value");
						}
				}
				ustream.close();
				return "";
            }
            else {
            	logger.debug("Finished resolving github. Result : Failed");
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
			logger.debug("Fetching latest release from github");
			netrelease = getLatestRelease();
			if (netrelease.length()==0) {
				if (!aborted)
					logger.debug("Url content not fetched. Retrying "+nbretry+" of 10");
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
		logger.debug("out of loop");
		final String latest = netrelease;
		logger.debug("Latest : " + latest);
		logger.debug("Current build : "+About.build);
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
			logger.debug("aborting job");
			aborted=true;
			if (uconn!=null)
			try {
				logger.debug("closing connection");
				uconn.disconnect();
			} catch (Exception e) {
				logger.debug("Error : "+e.getMessage());
			}
		}
	}

}