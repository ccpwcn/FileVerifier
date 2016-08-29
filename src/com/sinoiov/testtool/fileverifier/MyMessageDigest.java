/**
 * 
 */
package com.sinoiov.testtool.fileverifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 支持通过传入文件对象的方式处理GB级以上的大文件
 * 如果调用者传入的是一个文件对象，在不使用时必须要调用close()方法释放资源
 * 
 * @author liwei
 *
 */
public class MyMessageDigest {
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	protected static MessageDigest messagedigest = null;
	protected static FileChannel filechannel = null;
	protected static MappedByteBuffer byteBuffer = null;

	public String getFileMD5String(File file) {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
			return getFileMessageDigestImpl(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFileSha1String(File file) {
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");
			return getFileMessageDigestImpl(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFileSha256String(File file) {
		try {
			messagedigest = MessageDigest.getInstance("SHA-256");
			return getFileMessageDigestImpl(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFileSha512String(File file) {
		try {
			messagedigest = MessageDigest.getInstance("SHA-512");
			return getFileMessageDigestImpl(file);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		try {
			filechannel.close();
			filechannel = null;
			byteBuffer = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileMessageDigestImpl(File file) {
		FileInputStream fis = null;
		boolean successed = false;
		try {
			fis = new FileInputStream(file);
			if (filechannel == null) {
				filechannel = fis.getChannel();
			}
			if (byteBuffer == null) {
				byteBuffer = filechannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			}
			messagedigest.update(byteBuffer);
			successed = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (successed) {
			return bufferToHex(messagedigest.digest());
		} else {
			return null;
		}
	}

	public String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public boolean checkPassword(String password, String md5PwdStr) {
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}
}
