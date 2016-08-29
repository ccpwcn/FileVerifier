/**
 * 
 */
package com.sinoiov.testtool.fileverifier;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * @author 李伟
 *
 */
public class WorkThread extends Thread {
	private boolean ready;

	private String filename;

	private String md5Result;
	private String sha1Result;
	private String sha256Result;
	private String sha512Result;

	private boolean successed;
	private String statusMsg;
	Reportable report;

	public WorkThread(String filename, Reportable report) {
		this.filename = filename;
		this.report = report;
		
		if (report == null || filename == null || filename.isEmpty()) {
			statusMsg = "无效的运行参数。";
			ready = false;
		}
		File file = new File(filename);
		if (!file.isFile() || file.length() == 0) {
			statusMsg = "这不是一个有效的文件。";
			ready = false;
		}
		if (!file.exists()) {
			statusMsg = "文件不存在。";
			ready = false;
		}
		
		ready = true;
	}

	@Override
	public void run() {
		if (!ready) {
			report.finished();
			return;
		}

		report.percent(0);
		try {
//			successed = getMessageDigitestSmall();
//			successed = getMessageDigitestLarge();
			successed = getMessageDigitestLargeEx();
		} catch (Exception e) {
			statusMsg = e.getMessage();
			if (statusMsg == null || statusMsg.isEmpty()) {
				statusMsg = e.toString();
			}
			successed = false;
		}
		
		report.finished();
	}

	@SuppressWarnings("unused")
	private boolean getMessageDigitestSmall() {
		boolean ret = false;
		DataInputStream in = null;
		try {
			long totalSize = new File(filename).length();
			long finishedSize = 0;
			int bufferSize = 0;
			// [0 - 128MB] --> 1MB
			// [128 - 1024MB] --> 8MB
			// [1024MB - ] --> 32MB
			if (totalSize < 128 * 1024 * 1024) {
				bufferSize = 1 * 1024 * 1024;
			} else if (totalSize < 1024 * 1024 * 1024) {
				bufferSize = 2 * 1024 * 1024;
			} else {
				bufferSize = 4 * 1024 * 1024;
			}
			byte[] buffer = new byte[bufferSize];
			int charsReadCount = 0;
			in = new DataInputStream(new FileInputStream(filename));
			MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
			MessageDigest sha1MessageDigest = MessageDigest.getInstance("SHA-1");
			MessageDigest sha256MessageDigest = MessageDigest.getInstance("SHA-256");
			MessageDigest sha512MessageDigest = MessageDigest.getInstance("SHA-512");
			
			// 读入多个字符到字符数组中，charRead为一次读取字符数
			while ((charsReadCount = in.read(buffer)) != -1) {
				finishedSize += charsReadCount;

				md5MessageDigest.update(buffer, 0, charsReadCount);
				sha1MessageDigest.update(buffer, 0, charsReadCount);
				sha256MessageDigest.update(buffer, 0, charsReadCount);
				sha512MessageDigest.update(buffer, 0, charsReadCount);
				
				report.percent((int) ((double) finishedSize / totalSize * 100));
			}
			
			md5Result = new String(Hex.encodeHex(md5MessageDigest.digest()));
			sha1Result = new String(Hex.encodeHex(sha1MessageDigest.digest()));
			sha256Result = new String(Hex.encodeHex(sha256MessageDigest.digest()));
			sha512Result = new String(Hex.encodeHex(sha512MessageDigest.digest()));
			statusMsg = "OK";
			ret = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		report.percent(100);
		
		return ret;
	}

	@SuppressWarnings("unused")
	private boolean getMessageDigitestLarge() {
		boolean ret = false;
		File file = new File(filename);
		ProgressAccelerate progressAccelerate = new ProgressAccelerate(70);
		progressAccelerate.start();
		MyMessageDigest messageDigest = new MyMessageDigest();
		md5Result = messageDigest.getFileMD5String(file);
		progressAccelerate.reachNow();
		
		sha1Result = messageDigest.getFileSha1String(file);
		report.percent(80);
		sha256Result = messageDigest.getFileSha256String(file);
		report.percent(90);
		sha512Result = messageDigest.getFileSha512String(file);
		
		// 这一步是必须的，否则会造成资源泄漏
		messageDigest.close();
		
		progressAccelerate.reachNow();
		try {
			progressAccelerate.join(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ret = true;
		report.percent(100);
		
		return ret;
	}

	private boolean getMessageDigitestLargeEx() throws Exception {
		boolean ret = false;
		final int BUF_SIZE = 8 * 1024 * 1024;
		ByteBuffer byteBuf = ByteBuffer.allocate(BUF_SIZE);
		FileChannel inChannel = null;
		byte[] bytes = new byte[BUF_SIZE];
		
		MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
		MessageDigest sha1MessageDigest = MessageDigest.getInstance("SHA-1");
		MessageDigest sha256MessageDigest = MessageDigest.getInstance("SHA-256");
		MessageDigest sha512MessageDigest = MessageDigest.getInstance("SHA-512");
		
		@SuppressWarnings("resource")
		RandomAccessFile raf = new RandomAccessFile(filename,"r");
		inChannel = raf.getChannel();
		
		long totalSize = inChannel.size();
		long finishedSize = 0;
		for (int i = 0; i <= totalSize / Integer.MAX_VALUE; i++) {
			long startPosition = i * Integer.MAX_VALUE;
			long endPosition = 0;
			if (totalSize - i * Integer.MAX_VALUE > Integer.MAX_VALUE) {
				endPosition = i * Integer.MAX_VALUE + Integer.MAX_VALUE;
			} else {
				endPosition = totalSize - i * Integer.MAX_VALUE;
			}
			inChannel.map(FileChannel.MapMode.READ_ONLY, startPosition, endPosition);
			
			int readBytesCount = -1;
			while ((readBytesCount = inChannel.read(byteBuf)) != -1) {
				finishedSize += readBytesCount;
				
				bytes = byteBuf.array();
				md5MessageDigest.update(bytes, 0, readBytesCount);
				sha1MessageDigest.update(bytes, 0, readBytesCount);
				sha256MessageDigest.update(bytes, 0, readBytesCount);
				sha512MessageDigest.update(bytes, 0, readBytesCount);
				
				// 这个方法实际上也不会改变缓冲区的数据，而只是简单的重置了缓冲区的主要索引值
				// 不必为了每次读写都创建新的缓冲区,那样做会降低性能
				// 相反，要重用现在的缓冲区，在再次读取之前要清除缓冲区.
				byteBuf.clear();
				
				report.percent((int) ((double) finishedSize / totalSize * 100));
			}
		}
		
		
		
		md5Result = new String(Hex.encodeHex(md5MessageDigest.digest()));
		sha1Result = new String(Hex.encodeHex(sha1MessageDigest.digest()));
		sha256Result = new String(Hex.encodeHex(sha256MessageDigest.digest()));
		sha512Result = new String(Hex.encodeHex(sha512MessageDigest.digest()));
		ret = true;
	
		bytes = null;
		
		try {
			if (inChannel != null) {
				inChannel.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		report.percent(100);
		
		return ret;
	}
	
	/**
	 * 进度加速度处理模块
	 * 
	 * @author liwei
	 *
	 */
	private class ProgressAccelerate extends Thread {
		private int maxPercent;
		private boolean reachNow;
		
		public ProgressAccelerate(int maxPercent) {
			this.maxPercent = maxPercent;
		}
		
		@Override
		public void run() {
			int localPercent = 0;
			double coefficient = 0.1;
			int timeInterval = 100;
			int times = 1;
			while (!reachNow && localPercent < maxPercent) {
				report.percent(localPercent);
				
				coefficient = times * 0.01 + coefficient * 1.1 + Math.sqrt(timeInterval) * 0.1;
				timeInterval = (int)(timeInterval + 3 * Math.sin(times) + 2 * coefficient);
				times++;
				localPercent = (int) (Math.log(timeInterval) + times * 2);
//				System.out.println(coefficient + "-->" + localPercent + "-->" + timeInterval);
				try {
					Thread.sleep(timeInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			report.percent(maxPercent);
		}
		
		public void reachNow() {
			reachNow = true;
		}
	}

	/**
	 * @return the md5Result
	 */
	public String getMd5Result() {
		return md5Result.toUpperCase();
	}

	/**
	 * @return the sha1Result
	 */
	public String getSha1Result() {
		return sha1Result.toUpperCase();
	}

	/**
	 * @return the sha256Result
	 */
	public String getSha256Result() {
		return sha256Result.toUpperCase();
	}

	/**
	 * @return the sha512Result
	 */
	public String getSha512Result() {
		return sha512Result.toUpperCase();
	}
	
	/**
	 * @return
	 */
	public boolean isSuccessed() {
		return successed;
	}
	
	/**
	 * @return
	 */
	public String getStatusMsg() {
		return statusMsg;
	}
}
