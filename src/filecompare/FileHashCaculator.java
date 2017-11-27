package filecompare;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author kim
 * �� Ŭ������ ������ hash���� ����ϰ� �����ϱ� ���� Ŭ�����Դϴ�.
 */
public class FileHashCaculator implements ICancelable {
	private File location;

	public List<FileHash> hashes = new ArrayList<>();

	private boolean canceled = false;
	private String[] algos;
	private IProgressUpdateNotifier ipu = progress -> {
    };

	private int totalStage;
	private int currentStage = 0;

	//�Ʒ��κ� �����
	private int bottom = 0;


	public FileHashCaculator(File file, String[] algos, IProgressUpdateNotifier ipu) {
		if (ipu != null) {
			this.ipu = ipu;
		}
		this.algos = Arrays.copyOf(algos, algos.length);
		this.totalStage = this.algos.length;
		this.location = file;
	}

	public void start() throws Exception {
		bottom = 0;
		//�� ���������� ����
		int span = (int) ((float)100/totalStage);
		for(String algorithm : algos){
			//��� ó��
			if(canceled){
				return;
			}
			FileHash hash = digest(location, algorithm, progress->{
				//��ü�� ���Ͽ� ���� �ؽ��� ����� ���
				int current = (int) ((span / 100.f) * progress);
				ipu.updateProgress(bottom + current);
			});
			hashes.add(hash);
			//���������� ������ŵ�ϴ�
			currentStage++;
			//�Ʒ��κ� ������� ����մϴ�
			bottom = (int) (((float)currentStage/totalStage)*100);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileHashCaculator) {
			FileHashCaculator f = (FileHashCaculator) obj;
			return this.hashes.equals(f.hashes);
		} else {
			return false;
		}
	}

	private FileHash digest(File file, String algorithm, IProgressUpdateNotifier updater)
			throws Exception {
		// allocate 10KB buffer
		byte[] buffer = new byte[10240];
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		long totalJob = file.length();
		long currentJob = 0;
		MessageDigest md = MessageDigest.getInstance(algorithm);
		int len;
		while ((len = bis.read(buffer)) != -1) {
			if (canceled) {
				bis.close();
				return null;
			}
			md.update(buffer, 0, len);
			currentJob += len;
			updater.updateProgress((int) (((float) currentJob / totalJob) * 100));
			// System.out.println("subprogress:"+((int)
			// (((float)currentJob/(float)totalJob)*100)));
		}
		bis.close();
		return new FileHash(md.digest(), algorithm);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("��ġ: "+location.getAbsolutePath());
		for(FileHash hash : hashes){
			str.append("\n");
			str.append(hash.algorithm+":"+toHexString(hash.hash));
		}
		return str.toString();
	}

	private static final String HEXES = "0123456789ABCDEF";

	private static String toHexString(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	@Override
	public void cancel() {
		this.canceled = true;
	}

	/*
	�ؽð��� �ؽ� �˰����� ������ ��Ÿ��
	 */
	private class FileHash{
		public final byte[] hash;
		public final String algorithm;

		public FileHash(byte[] hash, String algorithm){
			this.hash = Arrays.copyOf(hash, hash.length);
			this.algorithm = algorithm;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof FileHash){
				FileHash o = ((FileHash) obj);
				return this.algorithm.equals(o.algorithm) && Arrays.equals(this.hash, o.hash);
			}
			return false;
		}
	}
}
