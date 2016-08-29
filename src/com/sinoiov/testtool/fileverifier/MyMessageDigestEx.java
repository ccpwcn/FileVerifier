/**
 * 
 */
package com.sinoiov.testtool.fileverifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * @author liwei
 * @since JDK 1.7
 *
 */
public abstract class MyMessageDigestEx {
	protected String filename;
	protected String identifyName;
	protected String md5Result;

	protected boolean sha1Check;
	protected String sha1Result;

	protected boolean sha256Check;
	protected String sha256Result;

	protected boolean sha512Check;
	protected String sha512Result;

	Reportable report;

	public MyMessageDigestEx(String filename, Reportable report) {
		this.filename = filename;
		this.report = report;
	}
	
	protected void getMessageDigitest() {
		long totalSize = new File(filename).length();
		long finishedSize = 0;
		FileInputStream fileInputStream = null;
		try {
			byte[] tempChars = new byte[1024];
			int charRead = 0;
			fileInputStream = new FileInputStream(filename);
			MessageDigest messageDigest = null;
			switch (identifyName) {
			case "MD5":
				messageDigest = MessageDigest.getInstance("MD5");
			}
			// 读入多个字符到字符数组中，charRead为一次读取字符数
			while ((charRead = fileInputStream.read(tempChars)) != -1) {
				finishedSize += charRead;

				messageDigest.update(tempChars, 0, charRead);
				
				if (report != null) {
					report.percent((int) ((double) finishedSize / totalSize * 100));
				}
			}
			
			md5Result = new String(Hex.encodeHex(messageDigest.digest()));
			
			if (report != null) {
				report.percent(100);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the md5Result
	 */
	public String getMd5Result() {
		return md5Result;
	}

	/**
	 * @param md5Result the md5Result to set
	 */
	public void setMd5Result(String md5Result) {
		this.md5Result = md5Result;
	}

	/**
	 * @return the sha1Result
	 */
	public String getSha1Result() {
		return sha1Result;
	}

	/**
	 * @param sha1Result the sha1Result to set
	 */
	public void setSha1Result(String sha1Result) {
		this.sha1Result = sha1Result;
	}

	/**
	 * @return the sha256Result
	 */
	public String getSha256Result() {
		return sha256Result;
	}

	/**
	 * @param sha256Result the sha256Result to set
	 */
	public void setSha256Result(String sha256Result) {
		this.sha256Result = sha256Result;
	}

	/**
	 * @return the sha512Result
	 */
	public String getSha512Result() {
		return sha512Result;
	}

	/**
	 * @param sha512Result the sha512Result to set
	 */
	public void setSha512Result(String sha512Result) {
		this.sha512Result = sha512Result;
	}
}

class Md5MessageDigest extends MyMessageDigestEx {
	private String identifyName;
	private boolean successed;
	
	public Md5MessageDigest(String filename, Reportable report) {
		super(filename, report);
		identifyName = "MD5";
	}

	/**
	 * @return the identifyName
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * @param identifyName the identifyName to set
	 */
	public void setIdentifyName(String identifyName) {
		this.identifyName = identifyName;
	}

	/**
	 * @return the successed
	 */
	public boolean isSuccessed() {
		return successed;
	}
}

class Sha1MessageDigest extends MyMessageDigestEx {
	private String identifyName;
	
	public Sha1MessageDigest(String filename, Reportable report) {
		super(filename, report);
		sha1Check = true;
		identifyName = "SHA-1";
	}

	/**
	 * @return the identifyName
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * @param identifyName the identifyName to set
	 */
	public void setIdentifyName(String identifyName) {
		this.identifyName = identifyName;
	}
}

class Sha256MessageDigest extends MyMessageDigestEx {
	private String identifyName;
	
	public Sha256MessageDigest(String filename, Reportable report) {
		super(filename, report);
		sha256Check = true;
		identifyName = "SHA-256";
	}

	/**
	 * @return the identifyName
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * @param identifyName the identifyName to set
	 */
	public void setIdentifyName(String identifyName) {
		this.identifyName = identifyName;
	}
}

class Sha512MessageDigest extends MyMessageDigestEx {
	private String identifyName;
	
	public Sha512MessageDigest(String filename, Reportable report) {
		super(filename, report);
		sha512Check = true;
		identifyName = "SHA-512";
	}

	/**
	 * @return the identifyName
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * @param identifyName the identifyName to set
	 */
	public void setIdentifyName(String identifyName) {
		this.identifyName = identifyName;
	}
}
