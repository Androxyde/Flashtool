package org.flashtool.system;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DevicesGit {

	private static String remotePath="https://github.com/Androxyde/devices.git";
	private static String localPath=OS.getFolderDevices()+File.separator+".git";
    private static Repository localRepo;
    private static Git git;
    
    public static void gitSync() throws IOException, InvalidRemoteException, org.eclipse.jgit.api.errors.TransportException, GitAPIException {
    	if (openRepository()) {
    		pullRepository();
    	}
    	else cloneRepository();
    }

    public static void cloneRepository() {
		try {
			log.info("Cloning devices repository to "+OS.getFolderDevices());
	        File lPath = new File(OS.getFolderDevices());
	        OS.deleteDirectory(lPath);
	        lPath.mkdir();
	        Git result = Git.cloneRepository()
	                .setURI(remotePath)
	                .setDirectory(lPath)
	                .call();
	        result.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static boolean openRepository() {
		try {
			log.info("Opening devices repository.");
	        FileRepositoryBuilder builder = new FileRepositoryBuilder();
	        log.debug("Local path : "+localPath);
	        localRepo = builder.setGitDir(new File(localPath))
	                .readEnvironment() // scan environment GIT_* variables
	                .findGitDir() // scan up the file system tree
	                .build();
	        log.debug("Getting new git object");
	        git = new Git(localRepo);
	        return true;
		} catch (Exception e) {
			log.info("Error opening devices repository.");
			closeRepository();
			return false;
		}
	}

    public static void pullRepository() {
    	try {
	    	log.info("Scanning devices folder for changes.");
	    	git.add().addFilepattern(".").call();
	    	Status status = git.status().call();
	    	if (status.getChanged().size()>0 || status.getAdded().size()>0 || status.getModified().size()>0) {
	    		log.info("Changes have been found. Doing a hard reset (removing user modifications).");
	    		ResetCommand reset = git.reset();
	    		reset.setMode(ResetType.HARD);
	    		reset.setRef(Constants.HEAD);
	    		reset.call();
	    	}
	    	log.info("Pulling changes from github.");
	    	git.pull().call();
    	} catch (NoHeadException e) {
    		log.info("Pull failed. Trying to clone repository instead");
    		closeRepository();
    		cloneRepository();
    	}
    	catch (Exception e1) {
    		closeRepository();
    	}
    }

    public static void closeRepository() {
    	log.info("Quietly closing devices repository.");
    	try {
        	git.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	try {
			localRepo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}