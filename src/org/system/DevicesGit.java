package org.system;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import com.jcraft.jsch.Session;

public class DevicesGit {

	private static String remotePath="https://github.com/Androxyde/devices.git";
    private static Repository localRepo;
    private static Git git;
    private static Logger logger = Logger.getLogger(DevicesGit.class);
    
    public static void gitSync(String localPath) throws IOException, InvalidRemoteException, org.eclipse.jgit.api.errors.TransportException, GitAPIException {
    	SshSessionFactory.setInstance(new JschConfigSessionFactory() {
    		  public void configure(Host hc, Session session) {
    		    session.setConfig("StrictHostKeyChecking", "no");
    		  };
    		}
    	);
    	if (!new File(localPath+File.separator+".git").exists()) {
    		if (new File(localPath).exists()) {
    			logger.info("This is the first sync with devices. Renaming devices to devices.old");
    			new File(localPath).renameTo(new File(localPath+".old"));
    		}
    	}
    	if (!new File(localPath).exists()) {
    		logger.info("Cloning devices repository");
    		try {
    			Git.cloneRepository().setURI(remotePath).setDirectory(new File(localPath)).call();
    		} catch (Exception e) {
    			logger.error("Cannot clone devices repository : "+e.getMessage());
    		}
    	}
    	else {
    		localRepo = new FileRepository(localPath + "/.git");
    		git = new Git(localRepo);
    		logger.info("Scanning devices folder for changes.");
    		git.add().addFilepattern(".").call();
    		if (git.status().call().getChanged().size()>0 || git.status().call().getAdded().size()>0 || git.status().call().getModified().size()>0) {
        		logger.info("Changes have been found. Doing a hard reset (removing user modifications).");
    			ResetCommand reset = git.reset();
    			reset.setMode(ResetType.HARD);
    			reset.setRef(Constants.HEAD);
    			reset.call();
    		}
    		logger.info("Pulling changes from github.");
			git.pull().call();
    	}
    }
}