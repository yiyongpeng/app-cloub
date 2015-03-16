package app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javassist.NotFoundException;

public class Publish {

	/**
	 * @param args
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void main(String[] args) {
		String basePath = System.getProperty("basepath", ".");
		System.out.println("[basedir] " + basePath);
		try {
			// input args
			String distPath = System.getProperty("distpath", "dist");
			String jarDistPath = System.getProperty("libDir", "lib");
			File libDir = new File(distPath, jarDistPath);
			List<String> files = new ArrayList<String>();
			for (int i = 0; i < args.length; i++) {
				files.add(args[i]);
			}
			String targetPath = "target";

			// cleanup
			FileKit.delete(new File(basePath, distPath));

			// copy files
			for (String fileName : files) {
				FileKit.copy(fileName, distPath, true, basePath);
			}
			// copy common jar
			String commonjar = System.getProperty("commonjar");
			if(commonjar!=null){
				commonjar = replaceVars(commonjar);
				FileKit.copy(commonjar, libDir.toString(), false, basePath);
			}
			// copy this jar
			String jarFileName = getTargetJarFile(basePath, targetPath);
			FileKit.copy(jarFileName, libDir.toString(), false, basePath);
			
			// startup
			String mainClass = System.getProperty("mainClass", System.getenv("MVN_STARTUP"));
			if (mainClass != null) {
				String libCommonDir = System.getProperty("commonJarDir");
				String classpath = getDirClassPath(libCommonDir) + getDirClassPath(libDir.toString());
				System.out.println("[classpath]" + classpath);
				ProcessBuilder builder = new ProcessBuilder("java", "-classpath", classpath, mainClass);
				builder.directory(new File(distPath));
				builder.redirectErrorStream(true);
				final Process p = builder.start();
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						p.destroy();
					}
				});
				Thread outThread = new Thread() {
					public void run() {
						InputStream in = p.getInputStream();
						BufferedReader read = new BufferedReader(new InputStreamReader(in));
						String line = null;
						try {
							while ((line = read.readLine()) != null) {
								System.out.println(line);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				outThread.setDaemon(true);
				outThread.start();
				int result = p.waitFor();
				if (result != 0) {
					System.err.println("[Error] " + mainClass + "  exit: " + result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getDirClassPath(String libDir) {
		if (libDir.indexOf("#{") != -1) {
			libDir = replaceVars(libDir);
		}
		StringBuilder sb = new StringBuilder();
		File libdir = new File(libDir);
		File[] files = libdir.listFiles();
		if (files != null) {
			for (File file : files) {
				sb.append(System.getProperty("path.separator"));
				sb.append(file.getAbsolutePath());
			}
		}
		return sb.toString();
	}

	private static Element root;

	@SuppressWarnings("unchecked")
	private static String replaceVars(String value) {
		loadPOM();
		if (root == null)
			return value;
		int startIdx = -1;
		List<Element> list = root.element("dependencies").elements("dependency");
		while ((startIdx = value.indexOf("#{", startIdx + 1)) != -1) {
			int endIdx = value.indexOf("}", startIdx);
			String varStr = value.substring(startIdx, endIdx + 1);
			String depId = varStr.substring(2, varStr.length() - 1).trim();
			if (list != null && !list.isEmpty()) {
				for (Element element : list) {
					if (element.elementTextTrim("artifactId").equals(depId)) {
						String version = element.elementTextTrim("version");
						value = value.replace(varStr, version);
						break;
					}
				}
			}
			value = value.replace(varStr, "");
		}
		return value;
	}

	private static void loadPOM() {
		if (root == null)
			try {
				SAXReader xml = new SAXReader();
				Document doc = xml.read("pom.xml");
				root = doc.getRootElement();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private static String getTargetJarFile(String basedir, String targetPath) throws NotFoundException {
		loadPOM();
		String jarFileName = root.elementTextTrim("artifactId") + "-" + root.elementTextTrim("version") + ".jar";
		return new File(targetPath, jarFileName).toString();
	}

}
