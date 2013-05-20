package com.carestream.demo.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

public class JGitPull {

    /**
     * @param args
     * @throws IOException 
     * @throws GitAPIException 
     * @throws TransportException 
     * @throws NoHeadException 
     * @throws RefNotFoundException 
     * @throws CanceledException 
     * @throws InvalidRemoteException 
     * @throws DetachedHeadException 
     * @throws InvalidConfigurationException 
     * @throws WrongRepositoryStateException 
     */
    public static void main(String[] args) throws IOException, WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, NoHeadException, TransportException, GitAPIException {
	File dir = new File("d:/tmp/jgit");
	Git git = Git.open(dir);
	PullResult result = git.pull().call();
	System.out.println(result.getFetchResult().getMessages());
	Status status = git.status().call();
	System.out.println(status.getChanged());
	System.out.println(status.getAdded());
	System.out.println(status.getUntracked());
    }

}
