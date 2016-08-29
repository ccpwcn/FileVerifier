/**
 * 
 */
package com.sinoiov.testtool.fileverifier;

/**
 * @author liwei
 *
 */
public class WorkThreadReportInfo {
	public String identifyName;
	public boolean successed;
	public String messageDigest;

	/**
	 * @param identifyName
	 * @param successed
	 * @param statusMessage
	 */
	public WorkThreadReportInfo(String identifyName, boolean successed, String messageDigest) {
		super();
		this.identifyName = identifyName;
		this.successed = successed;
		this.messageDigest = messageDigest;
	}

	/**
	 * @return the identifyName
	 */
	public String getIdentifyName() {
		return identifyName;
	}

	/**
	 * @param identifyName
	 *            the identifyName to set
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

	/**
	 * @param successed
	 *            the successed to set
	 */
	public void setSuccessed(boolean successed) {
		this.successed = successed;
	}

	/**
	 * @return the messageDigest
	 */
	public String getMessageDigest() {
		return messageDigest;
	}

	/**
	 * @param messageDigest the messageDigest to set
	 */
	public void setMessageDigest(String messageDigest) {
		this.messageDigest = messageDigest;
	}
}
