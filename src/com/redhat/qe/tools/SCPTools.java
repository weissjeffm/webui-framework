package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;

public class SCPTools {
	protected String userName;
	protected File sshPemFile;
	protected String password;
	protected String server;
	protected static Logger log = Logger.getLogger(SCPTools.class.getName());
	
	public SCPTools(String server,
			String user,
			File sshPemFile,
			String password){
		this.userName = user;
		this.sshPemFile = sshPemFile;
		this.password = password;
		this.server = server;
	}
	
	public boolean sendFile(String source, String dest){
		Connection newConn = new Connection(server);
		log.info("SCP: Copying "+source+" to "+this.server+":"+dest);
		try {
			newConn.connect();
			newConn.authenticateWithPublicKey(userName, sshPemFile, password);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: Connection failed:", e);
			return false;
		}
		SCPClient scp = new SCPClient(newConn);
		try {
			scp.put(source, dest);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		}
		log.info("SCP: Transfer succeeded");
		
		return true;
	}
	
	public boolean getFile(String remoteFile, String target){
		Connection newConn = new Connection(server);
		log.info("SCP: Copying "+server+":"+remoteFile+" to "+target);
		try {
			newConn.connect();
			newConn.authenticateWithPublicKey(userName, sshPemFile, password);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: Connection failed:", e);
			return false;
		}
		SCPClient scp = new SCPClient(newConn);
		try {
			scp.get(remoteFile, target);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		}
		log.info("SCP: Transfer succeeded");
		
		return true;
	}
}
