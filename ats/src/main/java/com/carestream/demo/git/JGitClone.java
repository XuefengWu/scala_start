package com.carestream.demo.git;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class JGitClone {

    /**
     * @param args
     * @throws GitAPIException 
     * @throws TransportException 
     * @throws InvalidRemoteException 
     */
    public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException {
	CloneCommand clone = new CloneCommand();
	File directory = new File("d:/tmp/jgit");
	directory.mkdirs();
	clone.setDirectory(directory);
	String uri = "https://github.com/XuefengWu/EverythingSearch-sublime2.git";
	clone.setURI(uri);
	Git git = clone.call();
	
	
    }

}
