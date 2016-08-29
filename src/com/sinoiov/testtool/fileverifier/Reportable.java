/**
 * 
 */
package com.sinoiov.testtool.fileverifier;


/**
 * @author liwei
 *
 */
public interface Reportable {
	/**
	 * 完成进度百分比
	 * @param percent
	 */
	public void percent(int percent);
	
	public void finished();
}
