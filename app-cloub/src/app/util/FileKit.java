package app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public final class FileKit {
	private static final Logger log = Logger.getLogger(FileKit.class);

	public static List<String[]> load(String path) {
		List<String[]> lines = new ArrayList<String[]>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.add(line.split("\t"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return lines;
	}

	public static void delete(File file) {
		if (file.exists() == false)
			return;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					delete(f);
				}
			}
		}
		if (!file.delete()) {
			System.err.println("[delete]" + file + " : Can't delete file");
		} else {
			System.out.println("[delete]" + file);
		}
	}

	/**
	 * 
	 * @param fileName  源文件相对路径
	 * @param distPath  目标目录路径
	 * @param structure  是否创建建构
	 * @param basedir  源文件基础路径
	 * @throws IOException
	 */
	public static void copy(String fileName, String distPath, boolean structure, String basedir) throws IOException {
		File file = new File(basedir, fileName);
		File distDir = new File(basedir, distPath);
		if (file.exists() == false) {
			System.err.println(fileName + " : Not found file");
			return;
		}
		mkdir(distDir);
		// make parent dir
		if (structure) {
			List<File> fileDirs = parentDirFiles(new File(fileName));
			for (File dirFile : fileDirs) {
				distDir = new File(distDir, dirFile.getName());
				if (!distDir.exists() && !distDir.mkdir() || distDir.isFile()) {
					throw new IOException("Can't create directory: " + distDir);
				}
			}
		}
		// copy dir or file
		if (file.isDirectory() && !file.getName().equals(".svn")) {
			File dir = new File(distDir, file.getName());
			boolean mkdir = dir.mkdirs();
			System.out.println(file +" \t => \t "+dir+"   "+mkdir);
			File[] files = file.listFiles();
			for (File f : files) {
				copy(new File(fileName, f.getName()).toString(), distPath, structure, basedir);
			}
		} else if (file.isFile()) {
			copy(file, new File(distDir, file.getName()));
		}
	}

	private static void mkdir(File distDir) throws IOException {
		if (distDir.exists())
			return;
		List<File> fileDirs = parentDirFiles(distDir);
		fileDirs.add(distDir);
		for (File dir : fileDirs) {
			if ((dir.exists() == false && dir.mkdir() == false) || dir.isFile()) {
				throw new IOException("Can't create directory : " + distDir);
			}
		}
	}

	private static byte[] b = new byte[1024];

	/**
	 * 复制文件内容
	 * 
	 * @param src
	 * @param dist
	 * @throws IOException
	 */
	private static void copy(File src, File dist) throws IOException {
		if (dist.exists() == false && dist.createNewFile() == false) {
			throw new IOException("[create] " + dist + " : Can't create file");
		}
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(dist));
			for (int i; (i = in.read(b)) != -1; out.flush()) {
				out.write(b, 0, i);
			}
			System.out.println("[copy] " + src + " \t => \t " + dist);
		} catch (Exception e) {
			System.err.println("[copy] " + src + " \t ~> \t " + dist + " : " + e);
			if (dist.exists()) {
				dist.delete();
			}
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e2) {
				}
			if (out != null)
				try {
					out.close();
				} catch (Exception e2) {
				}
		}
	}

	public static List<File> parentDirFiles(File file) {
		LinkedList<File> list = new LinkedList<File>();
		while ((file = file.getParentFile()) != null) {
			list.addFirst(file);
		}
		return list;
	}

	public static void write(File file, ByteBuffer data, int length) {
		boolean outmp = System.getProperty("os.name", "Windows").toLowerCase().indexOf("linux") != -1;
		write(file, data, length, outmp);
	}

	public static void write(File file, ByteBuffer data, int length, boolean outmp) {
		File dist = file;
		if (outmp) {
			file = new File(file.toString() + ".tmp");
		}
		BufferedOutputStream out = null;
		try {
			if (file.exists() == false)
				file.createNewFile();
			out = new BufferedOutputStream(new FileOutputStream(file));
			byte[] bytes = new byte[length];
			data.get(bytes);
			out.write(bytes);
			out.flush();
			if (outmp) {
				dist.delete();
				if (!file.renameTo(dist)) {
					log.error("[rename] " + file + "  >  " + dist + " : rename fault!");
					return;
				}
			}
			log.debug("[write] " + dist + " : " + length + " bytes");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
				}
		}
	}

	public static ByteBuffer loadByteBuffer(File file) throws IOException {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[(int) file.length()];
			in.read(bytes);
			return ByteBufferUtils.create(bytes);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	public static String read2String(File file, String charsetName) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}
}
