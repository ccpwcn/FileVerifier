/**
 * 
 */
package com.sinoiov.testtool.fileverifier;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.sinoiov.ui.enhanced.MyJPopupTextField;

/**
 * @author 李伟
 * @since JDK 1.7
 *
 */
public class UI extends JFrame implements Reportable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -708789597726893377L;
	final private String title;
	private int WND_WIDTH = 700;
	private int WND_HIGHT = 350;
	private Container container;
	private MyJPopupTextField textfieldFilename;
	private String userFilename;
	private JButton btnOpenFile;

	private MyJPopupTextField textfieldMD5;
	private MyJPopupTextField textfieldSHA1;
	private MyJPopupTextField textfieldSHA256;
	private MyJPopupTextField textfieldSHA512;

	private MyJPopupTextField textfieldStatus;
	private JButton btnStart;

	private JProgressBar progressBar;

	private WorkThread workThread;
	private long startTime;

	public UI() {
		title = "文件检查校验测试工具";
	}

	public UI(String title) {
		this.title = title;
	}

	/**
	 * 显示窗口
	 */
	public void showWindow() {
		init();
		setSize(WND_WIDTH, WND_HIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		DropTargetListener dropTargetAdapter = new DropTargetAdapter();
		new DropTarget(container, DnDConstants.ACTION_COPY_OR_MOVE, dropTargetAdapter);
		new DropTarget(textfieldFilename, DnDConstants.ACTION_COPY_OR_MOVE, dropTargetAdapter);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter());
		
		setVisible(true);
	}

	/**
	 * 初始化
	 */
	private void init() {
		setTitle(title);
		container = getContentPane();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints(); // 定义一个GridBagConstraints
		c.insets = new Insets(10, 5, 10, 5); // 组件彼此的间距
		container.setFont(new Font("SansSerif", Font.PLAIN, 14));
		container.setLayout(layout);

		// 控制添加进的组件的显示位置
		c.fill = GridBagConstraints.BOTH; // 此项是为了设置如果组件所在的区域比组件本身要大时的显示情况
		// NONE：不调整组件大小。
		// HORIZONTAL：加宽组件，使它在水平方向上填满其显示区域，但是不改变高度。
		// VERTICAL：加高组件，使它在垂直方向上填满其显示区域，但是不改变宽度。
		// BOTH：使组件完全填满其显示区域。
		c.gridwidth = 1; // 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		c.weightx = 0; // 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		c.weighty = 0; // 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间

		// 文件信息行
		makeFileInfoCompents(layout, c);

		// 输出行
		makeResultCompentsMd5(layout, c);
		makeResultCompentsSha1(layout, c);
		makeResultCompentsSha256(layout, c);
		makeResultCompentsSha512(layout, c);

		// 操作信息
		makeOperatorCompents(layout, c);

		// 进度行
		makeProgressCompents(layout, c);
	}

	/**
	 * 创建文件信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeFileInfoCompents(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelFilename = new JLabel("文件名：");
		container.add(labelFilename);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelFilename, c);

		textfieldFilename = new MyJPopupTextField();
		container.add(textfieldFilename);
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldFilename, c);

		btnOpenFile = new JButton("打开");
		btnOpenFile.addActionListener(new ButtonListen());
		container.add(btnOpenFile);
		c.gridwidth = 0;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(btnOpenFile, c); // 设置组件
	}

	/**
	 * 创建结果信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeResultCompentsMd5(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelMD5 = new JLabel("MD5：");
		container.add(labelMD5);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelMD5, c);

		textfieldMD5 = new MyJPopupTextField();
		textfieldMD5.setEditable(false);
		container.add(textfieldMD5);
		c.gridwidth = 0;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldMD5, c);
	}

	/**
	 * 创建结果信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeResultCompentsSha1(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelSHA1 = new JLabel("SHA-1：");
		container.add(labelSHA1);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelSHA1, c);

		textfieldSHA1 = new MyJPopupTextField();
		textfieldSHA1.setEditable(false);
		container.add(textfieldSHA1);
		c.gridwidth = 0;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldSHA1, c);
	}

	/**
	 * 创建结果信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeResultCompentsSha256(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelSHA256 = new JLabel("SHA-256：");
		container.add(labelSHA256);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelSHA256, c);

		textfieldSHA256 = new MyJPopupTextField();
		textfieldSHA256.setEditable(false);
		container.add(textfieldSHA256);
		c.gridwidth = 0;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldSHA256, c);
	}

	/**
	 * 创建结果信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeResultCompentsSha512(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelSHA512 = new JLabel("SHA-512：");
		container.add(labelSHA512);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelSHA512, c);

		textfieldSHA512 = new MyJPopupTextField();
		textfieldSHA512.setEditable(false);
		container.add(textfieldSHA512);
		c.gridwidth = 0;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldSHA512, c);
	}

	/**
	 * 创建状态和操作组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeOperatorCompents(GridBagLayout layout, GridBagConstraints c) {
		JLabel labelStatus = new JLabel("工作状态：");
		container.add(labelStatus);
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(labelStatus, c);

		textfieldStatus = new MyJPopupTextField();
		textfieldStatus.setEditable(false);
		textfieldStatus.setText("就绪...");
		container.add(textfieldStatus);
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(textfieldStatus, c);

		btnStart = new JButton("开始");
		btnStart.addActionListener(new ButtonListen());
		container.add(btnStart);
		c.gridwidth = 0;
		c.weightx = 0;
		c.weighty = 0;
		layout.setConstraints(btnStart, c); // 设置组件
	}

	/**
	 * 创建进度条信息组件
	 * 
	 * @param layout
	 * @param c
	 */
	private void makeProgressCompents(GridBagLayout layout, GridBagConstraints c) {
		progressBar = new JProgressBar();
		c.gridwidth = 0;
		c.weightx = 1;
		c.weighty = 0;
		layout.setConstraints(progressBar, c);
		container.add(progressBar);
	}

	private class ButtonListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnOpenFile) {
				JFileChooser dlg = new JFileChooser();
				dlg.setDialogTitle("打开文件");
				int result = dlg.showOpenDialog(container);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = dlg.getSelectedFile();
					if (file == null) {
						JOptionPane.showMessageDialog(null, "获取指定文件信息失败。", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
					} else {
						userFilename = file.getPath();
						if (userFilename == null || userFilename.isEmpty()) {
							JOptionPane.showMessageDialog(null, "ERROR_MESSAGE", "必须指定一个正确有效的文件名。",
									JOptionPane.ERROR_MESSAGE);
						} else {
							textfieldFilename.setText(userFilename);
							clearResult();
							textfieldStatus.setText("就绪...");
						}
					}
				}
			} else if (e.getSource() == btnStart) {
				userFilename = textfieldFilename.getText();
				if (userFilename == null || userFilename.isEmpty()) {
					textfieldStatus.setText("错误提示：必须指定一个正确有效的文件名才能开始。");
				} else {
					if (workThread != null) {
						JOptionPane.showMessageDialog(null, "前一个任务还没有完成，请等待。", "提示", JOptionPane.INFORMATION_MESSAGE);
					} else {
						workThread = new WorkThread(userFilename, UI.this);
						startTime = System.currentTimeMillis();
						workThread.start();

						clearResult();
					}
				}
			}
		}
	}
	
	private void clearResult() {
		textfieldMD5.setText("");
		textfieldSHA1.setText("");
		textfieldSHA256.setText("");
		textfieldSHA512.setText("");
	}
	
	private class WindowAdapter implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			if (workThread != null && workThread.getState() != Thread.State.TERMINATED) {
				int result = JOptionPane.showConfirmDialog(null, "当前任务还没有完成，确定要要退出吗？", "退出确认",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					dispose();
				}

			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
		
	}
	
	private class DropTargetAdapter implements DropTargetListener {
		@Override
		public void drop(DropTargetDropEvent dtde) {
			try {
				Transferable tf = dtde.getTransferable();
				if (tf.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					@SuppressWarnings("unchecked")
					List<File> lt = (List<File>) tf.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<File> itor = lt.iterator();
					while (itor.hasNext()) {
						File f = (File) itor.next();
						String fname = f.getAbsolutePath();
						if (fname != null && !fname.isEmpty()) {
							textfieldFilename.setText(fname);
							break;
						}
					}
					dtde.dropComplete(true);
					
					// 接受了新拖放进来的文件之后，清除原来的数据
					clearResult();
				} else {
					dtde.rejectDrop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dragExit(DropTargetEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dragOver(DropTargetDragEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void percent(int percent) {
		if (percent < 0) {
			return;
		}

		progressBar.setValue(percent);
		textfieldStatus.setText("正在获取文件信息(" + percent + "%)...");
	}

	@Override
	public void finished() {
		long endTime = System.currentTimeMillis();
		String msg = null;
		if (workThread.isSuccessed()) {
			textfieldMD5.setText(workThread.getMd5Result());
			textfieldSHA1.setText(workThread.getSha1Result());
			textfieldSHA256.setText(workThread.getSha256Result());
			textfieldSHA512.setText(workThread.getSha512Result());
				
			msg = String.format("成功，耗时%.3f秒", (double)(endTime - startTime) / 1000);
		} else {
			String threadMsg = workThread.getStatusMsg();
			if (threadMsg == null || threadMsg.isEmpty()) {
				msg = "失败，原因：unknow";
			} else {
				msg = "失败，" + threadMsg;
			}
		}
		
		textfieldStatus.setText(msg);
		
		// Reset
		reset();
	}
	
	private void reset() {
		userFilename = null;
		workThread = null;
		startTime = 0;
	}
}
