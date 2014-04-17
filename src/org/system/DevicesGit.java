package org.system;

import java.io.File;
import java.io.IOException;
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
import org.logger.MyLogger;
import com.jcraft.jsch.Session;

public class DevicesGit {

	private static String localPath=OS.getWorkDir()+File.separator+"devices";
	private static String remotePath="https://github.com/Androxyde/devices.git";
    private static Repository localRepo;
    private static Git git;
    
    public static void gitSync() throws IOException, InvalidRemoteException, org.eclipse.jgit.api.errors.TransportException, GitAPIException {
    	SshSessionFactory.setInstance(new JschConfigSessionFactory() {
    		  public void configure(Host hc, Session session) {
    		    session.setConfig("StrictHostKeyChecking", "no");
    		  };
    		}
    	);
    	if (!new File(localPath+File.separator+".git").exists()) {
    		MyLogger.getLogger().info("This is the first sync with devices on github. Renaming devices to devices.old");
			new File(localPath).renameTo(new File(localPath+".old"));
    	}
    	if (!new File(localPath).exists()) {
    		MyLogger.getLogger().info("Cloning devices from github project");
    		Git.cloneRepository().setURI(remotePath).setDirectory(new File(localPath)).call();
    	}
    	else {
    		localRepo = new FileRepository(localPath + "/.git");
    		git = new Git(localRepo);
    		MyLogger.getLogger().info("Checking if changes have been made to devices folder.");
    		git.add().addFilepattern(".").call();
    		if (git.status().call().getChanged().size()>0 || git.status().call().getAdded().size()>0 || git.status().call().getModified().size()>0) {
    			ResetCommand reset = git.reset();
    			reset.setMode(ResetType.HARD);
    			reset.setRef(Constants.HEAD);
    			MyLogger.getLogger().info("Hard reset of devices (removing user modifications)");
    			reset.call();
    		}
    		MyLogger.getLogger().info("Pulling changes from github");
    		git.pull().call();
    	}
    	MyLogger.getLogger().info("Devices sync finished.");
    }
 
}