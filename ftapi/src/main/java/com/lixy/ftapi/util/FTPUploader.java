package com.lixy.ftapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


public class FTPUploader {

	FTPClient ftp = null;

	public FTPUploader(String host, String user, String pwd) throws Exception { // NOSONAR
		ftp = new FTPClient();
		ftp.addProtocolCommandListener(new com.lixy.ftapi.listener.PrintCommandListener()); // NOSONAR
		int reply;
		ftp.connect(host);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server."); // NOSONAR
		}
		ftp.login(user, pwd);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();
	}

	public void uploadFile(String localFileFullName, String fileName, String hostDir) throws IOException {
		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			this.ftp.mkd(hostDir);
			this.ftp.storeFile( hostDir + fileName, input);
		}
	}		
	
	public String getWorkingDir() throws IOException{
		return this.ftp.printWorkingDirectory();
	}

	public void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException f) { // NOSONAR
				// do nothing as file is already saved to server
			}
		}
	}

	

}
