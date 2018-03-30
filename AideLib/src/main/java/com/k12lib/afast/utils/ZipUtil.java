package com.k12lib.afast.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 说明：压缩，解压的工具类
 */
public class ZipUtil {

	/**
	 * 压缩
	 * @throws IOException
	 */
	public static void zip(File outFile, File fileOrDirectory, String[] files) throws IOException {
		// 提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {

//			File outFile = new File(dest);// 源文件或者目录
//			File fileOrDirectory = new File(src);// 压缩文件路径
			out = new ZipOutputStream(new FileOutputStream(outFile));
			// 如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				// 返回一个文件或空阵列。
//				File[] entries = fileOrDirectory.listFiles();
//				for (int i = 0; i < entries.length; i++) {
//					// 递归压缩，更新curPaths
//					zipFileOrDirectory(out, entries[i], "");
//				}
				for (int i = 0; i < files.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, new File(fileOrDirectory,files[i]), "");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void zipFileOrDirectory(ZipOutputStream out,
			File fileOrDirectory, String curPath) throws IOException {
		// 从文件中读取字节的输入流
		FileInputStream in = null;
		try {
			// 如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				// 实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				// 条目的信息写入底层流
				out.putNextEntry(entry);
				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解压缩
	 * 
	 * @param zipFileName
	 *            zip文件的全目录地址
	 * @param outputDirectory
	 *            解压后的输出地址
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void unzip(String zipFileName, String outputDirectory)
			throws IOException {
		ArrayList<String> mZipAll = new ArrayList<String>();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File dest = new File(outputDirectory);
			dest.mkdirs();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				String name = zipEntry.getName();//.replace('\\', File.separatorChar);
//				android.util.Log.e("unzip","ZipEntry:"+name);
//				if (name.charAt(name.length()-1)==File.separatorChar) 
//					name = name.su
				InputStream in = null;
				FileOutputStream out = null;
				try {
					if (zipEntry.isDirectory()) {
//						name = name.substring(0, name.length() - 1);
						File f = new File(outputDirectory,name);
						f.mkdirs();
					} else {
//						int index = entryName.lastIndexOf("\\");
//						if (index != -1) {
//							File df = new File(outputDirectory + File.separator
//									+ entryName.substring(0, index));
//							df.mkdirs();
//						}
//						index = entryName.lastIndexOf("/");
//						if (index != -1) {
//							File df = new File(outputDirectory + File.separator
//									+ entryName.substring(0, index));
//							df.mkdirs();
//						}
						File f = new File(outputDirectory,name);
						f.getParentFile().mkdirs();
						if (f.getName().endsWith("zip")) {
							mZipAll.add(f.getName());
						}
						// f.createNewFile();
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);

						int c;
						byte[] by = new byte[1024];
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}

				} catch (IOException ex) {
					ex.printStackTrace();
					throw new IOException("解压失败：" + ex.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}

			if (mZipAll.size() > 0) {
				for (String zipfile : mZipAll) {
					int lastIndexOf = zipFileName.lastIndexOf("/");
					String fileName = zipFileName.substring(0, lastIndexOf + 1);
					int lastIndexOf2 = zipfile.lastIndexOf(".");
					String zipDirName = zipfile.substring(0, lastIndexOf2);
					unzip2(fileName + zipfile, outputDirectory+zipDirName);
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new IOException("解压失败：" + ex.toString());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}
	}
	
	
	/**
	 * 解压缩
	 * 
	 * @param zipFileName
	 *            zip文件的全目录地址
	 * @param outputDirectory
	 *            解压后的输出地址
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void unzip2(String zipFileName, String outputDirectory)
			throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File dest = new File(outputDirectory);
			dest.mkdir();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				String entryName = zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				try {
					if (zipEntry.isDirectory()) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length() - 1);
						File f = new File(outputDirectory + File.separator
								+ name);
						f.mkdirs();
					} else {
						int index = entryName.lastIndexOf("\\");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						index = entryName.lastIndexOf("/");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						File f = new File(outputDirectory + File.separator
								+ zipEntry.getName());
						File f1 = null;
						// f.createNewFile();
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);

						int c;
						byte[] by = new byte[1024];
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}

				} catch (IOException ex) {
					ex.printStackTrace();
					throw new IOException("解压失败：" + ex.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new IOException("解压失败：" + ex.toString());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}
	}
}
