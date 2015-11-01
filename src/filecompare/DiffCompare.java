package filecompare;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

/**
 *
 * @author hasunwoo
 * ���� �ؽð����� GUI�� �������ݴϴ�
 * windowbuilder pro �� �̿��ؼ� ��������ϴ�.
 */
public class DiffCompare extends JFrame {
	public DiffCompare() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("���Ϻ� V1.0 by hasun");
		setResizable(false);
		setSize(687, 410);

		setfile1 = new JButton("����1 ����");
		setfile1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fname1.setText(selectFile());
			}
		});
		setfile1.setBounds(546, 123, 123, 23);

		JLabel subject = new JLabel("���Ϻ񱳱� V1.0");
		subject.setBounds(226, 36, 207, 33);
		subject.setFont(new Font("����", Font.PLAIN, 28));

		reset1 = new JButton("����1 �ʱ�ȭ");
		reset1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f1 = null;
				fname1.setText("");
				updateHashButton();
			}
		});
		reset1.setBounds(546, 90, 123, 23);

		reset2 = new JButton("����2 �ʱ�ȭ");
		reset2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f2 = null;
				fname2.setText("");
				updateHashButton();
			}
		});
		reset2.setBounds(546, 196, 123, 23);
		getContentPane().setLayout(null);

		reset = new JButton("��ü �ʱ�ȭ");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAll();
			}
		});
		reset.setBounds(128, 290, 122, 62);
		getContentPane().add(reset);

		start = new JButton("���ϱ�!!!!");
		start.addActionListener(new ActionListener() {
			private boolean canceled = false;

			public void actionPerformed(ActionEvent e) {
				canceled = false;
				File file1 = new File(fname1.getText());
				File file2 = new File(fname2.getText());
				if (!file1.exists()) {
					JOptionPane.showMessageDialog(DiffCompare.this,
							"����1 �� ã���� �����ϴ�", "����", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!file2.exists()) {
					JOptionPane.showMessageDialog(DiffCompare.this,
							"����2 ��  ã���� �����ϴ�", "����", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					ProgressMonitor pm = new ProgressMonitor(DiffCompare.this,
							"���Ϻ� ���൵", "�ؽð� �����", 0, 100);
					pm.setMillisToDecideToPopup(0);
					pm.setMillisToPopup(0);
					Thread worker = new Thread(new Runnable() {
						@Override
						public void run() {
							f1 = null;
							f2 = null;
							updateHashButton();
							setBusy();
							try {
								IProgressUpdate pu = new IProgressUpdate() {
									@Override
									public void updateProgress(int progress) {
										if (pm.isCanceled()) {
											cancelOperation();
										}
										pm.setProgress(progress);
									}
								};
								f1 = new FileHashCaculator(file1, pu);
								f2 = new FileHashCaculator(file2, pu);
								pm.setNote("�ؽð� ����� - ����1");
								f1.start();
								if (canceled)
									throw new Exception("����ڿ� ���� ��ҵǾ����ϴ�");
								pm.setNote("�ؽð� ����� - ����2");
								f2.start();
								if (canceled)
									throw new Exception("����ڿ� ���� ��ҵǾ����ϴ�");
								if (f1.equals(f2)) {
									JOptionPane.showMessageDialog(
											DiffCompare.this, "�� ������ �����ϴ�",
											"���Ϻ� ���",
											JOptionPane.INFORMATION_MESSAGE);
								} else {
									JOptionPane.showMessageDialog(
											DiffCompare.this, "�� ������ �ٸ��ϴ�",
											"���Ϻ� ���",
											JOptionPane.INFORMATION_MESSAGE);
								}
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(
										DiffCompare.this,
										ex.getClass().getName() + ":"
												+ ex.getMessage(), "����",
										JOptionPane.ERROR_MESSAGE);
								resetAll();
							} finally {
								setIdle();
								pm.close();
								updateHashButton();
							}
						}

						private void cancelOperation() {
							canceled = true;
							if (f1 != null) {
								f1.cancel();
							}
							if (f2 != null) {
								f2.cancel();
							}
							f1 = null;
							f2 = null;
						}
					});
					worker.setName("file-compare");
					worker.start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(DiffCompare.this, ex
							.getClass().getName() + ":" + ex.getMessage(),
							"����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		start.setBounds(299, 289, 122, 64);
		getContentPane().add(start);
		getContentPane().add(reset1);
		getContentPane().add(setfile1);

		setfile2 = new JButton("����2 ����");
		setfile2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fname2.setText(selectFile());
			}
		});
		setfile2.setBounds(546, 229, 123, 23);
		getContentPane().add(setfile2);
		getContentPane().add(reset2);
		getContentPane().add(subject);

		JLabel file1 = new JLabel("����1");
		file1.setFont(new Font("����", Font.PLAIN, 20));
		file1.setBounds(12, 108, 57, 22);
		getContentPane().add(file1);

		fname1 = new JTextField();
		fname1.setBounds(67, 108, 419, 24);
		getContentPane().add(fname1);
		fname1.setColumns(10);

		JLabel file2 = new JLabel("����2");
		file2.setFont(new Font("����", Font.PLAIN, 20));
		file2.setBounds(12, 212, 57, 22);
		getContentPane().add(file2);

		fname2 = new JTextField();
		fname2.setColumns(10);
		fname2.setBounds(67, 212, 419, 24);
		getContentPane().add(fname2);

		status = new JLabel("");
		status.setBounds(550, 54, 105, 15);
		getContentPane().add(status);

		extraf1 = new JButton("����1 �ؽð�");
		extraf1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextArea text = new JTextArea(f1.toString());
				text.setEditable(false);
				JOptionPane.showMessageDialog(DiffCompare.this, text,"����1 �Ӽ�",JOptionPane.PLAIN_MESSAGE);
			}
		});
		extraf1.setBounds(546, 283, 123, 23);
		getContentPane().add(extraf1);

		extraf2 = new JButton("����2 �ؽð�");
		extraf2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextArea text = new JTextArea(f2.toString());
				text.setEditable(false);
				JOptionPane.showMessageDialog(DiffCompare.this, text,"����2 �Ӽ�",JOptionPane.PLAIN_MESSAGE);
			}
		});
		extraf2.setBounds(546, 330, 123, 23);
		getContentPane().add(extraf2);

		URL iconurl = getClass().getResource("/resources/icon.png");
		ImageIcon icon = new ImageIcon(iconurl);
		setIconImage(icon.getImage());

		updateHashButton();
		setVisible(true);
	}
	/**
	 * ���ϼ���â�� ���ϴ�.
	 * @return ������ ������
	 */
	public String selectFile() {
		FileDialog fd = new FileDialog(this, "���ϼ���", FileDialog.LOAD);
		String spr = System.getProperty("file.separator");
		fd.setDirectory("C:" + spr);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) {
			return "";
		}
		return fd.getDirectory() + fd.getFile();
	}

	public void setBusy() {
		status.setText("�۾� ���Դϴ�");
		reset.setEnabled(false);
		start.setEnabled(false);
		reset1.setEnabled(false);
		reset2.setEnabled(false);
		setfile1.setEnabled(false);
		setfile2.setEnabled(false);
		fname1.setEditable(false);
		fname2.setEditable(false);
	}

	public void setIdle() {
		status.setText("");
		reset.setEnabled(true);
		start.setEnabled(true);
		reset1.setEnabled(true);
		reset2.setEnabled(true);
		setfile1.setEnabled(true);
		setfile2.setEnabled(true);
		fname1.setEditable(true);
		fname2.setEditable(true);
	}

	private void updateHashButton() {
		extraf1.setEnabled(f1 != null);
		extraf2.setEnabled(f2 != null);
	}

	private void resetAll() {
		f1 = null;
		f2 = null;
		fname1.setText("");
		fname2.setText("");
		setIdle();
		updateHashButton();
	}

	private static final long serialVersionUID = -411173112597862444L;
	private JTextField fname1;
	private FileHashCaculator f1 = null;
	private JTextField fname2;
	private FileHashCaculator f2 = null;
	private JLabel status;
	private JButton reset;
	private JButton start;
	private JButton reset1;
	private JButton reset2;
	private JButton setfile1;
	private JButton setfile2;
	private JButton extraf1;
	private JButton extraf2;
}
