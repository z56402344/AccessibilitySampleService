package com.k12lib.afast.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Tools for managing files. Not for public consumption.
 */
public class FileUtils {
	public static final int S_IRWXU = 00700;
	public static final int S_IRUSR = 00400;
	public static final int S_IWUSR = 00200;
	public static final int S_IXUSR = 00100;

	public static final int S_IRWXG = 00070;
	public static final int S_IRGRP = 00040;
	public static final int S_IWGRP = 00020;
	public static final int S_IXGRP = 00010;

	public static final int S_IRWXO = 00007;
	public static final int S_IROTH = 00004;
	public static final int S_IWOTH = 00002;
	public static final int S_IXOTH = 00001;

	/** 安全文件名的正则表达式*/
	private static final Pattern SAFE_FILENAME_PATTERN = Pattern
			.compile("[\\w%+,./=_-]+");

	public static native int setPermissions(String file, int mode, int uid,
			int gid);

	/**
	 * returns the FAT file system volume ID for the volume mounted at the given
	 * mount point, or -1 for failure
	 * 
	 * @param mountPoint
	 *            point for FAT volume
	 * @return volume ID or -1
	 */
	public static native int getFatVolumeId(String mountPoint);

	/**
	 * 在输出流上执行sync()，
	 * Perform an fsync on the given FileOutputStream. The stream at this point
	 * must be flushed but not yet closed.
	 */
	public static boolean sync(FileOutputStream stream) {
		try {
			if (stream != null) {
				stream.getFD().sync();
			}
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * 复制源文件(srcFile)到目标文件(destFile),复制成功返回true否则返回false
	 * copy a file from srcFile to destFile, return true if succeed, return
	 * false if fail 
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 */
	public static boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	/**
	 * 复制源文件流(inputStream)到目标文件(destFile),复制成功返回true否则返回false
	 * Copy data from a source stream to destFile. Return true if succeed,
	 * return false if failed.
	 * 
	 */
	public static boolean copyToFile(InputStream inputStream, File destFile) {
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			FileOutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.flush();
				try {
					out.getFD().sync();
				} catch (IOException e) {
				}
				out.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Check if a filename is "safe" (no metacharacters or spaces).
	 * 检查文件名是否是安全的(无字符或空格)
	 * 
	 * @param file
	 *            The file to check
	 */
	public static boolean isFilenameSafe(File file) {
		// Note, we check whether it matches what's known to be safe,
		// rather than what's known to be unsafe. Non-ASCII, control
		// characters, etc. are all unsafe by default.
		return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
	}

	/**
	 * Read a text file into a String, optionally limiting the length.
	 * 从一个文本文件中读取字符串，并可随意限制长度
	 * 
	 * @param file
	 *            to read (will not seek, so things like /proc files are OK)
	 * @param max
	 *            length (positive for head, negative of tail, 0 for no limit)
	 * @param ellipsis
	 *            to add of the file was truncated (can be null)
	 * @return the contents of the file, possibly truncated
	 * @throws IOException
	 *             if something goes wrong reading the file
	 */
	public static String readTextFile(File file, int max, String ellipsis)
			throws IOException {
		InputStream input = new FileInputStream(file);
		// wrapping a BufferedInputStream around it because when reading /proc
		// with unbuffered
		// input stream, bytes read not equal to buffer size is not necessarily
		// the correct
		// indication for EOF; but it is true for BufferedInputStream due to its
		// implementation.
		BufferedInputStream bis = new BufferedInputStream(input);
		try {
			long size = file.length();
			if (max > 0 || (size > 0 && max == 0)) { // "head" mode: read the
														// first N bytes
				if (size > 0 && (max == 0 || size < max))
					max = (int) size;
				byte[] data = new byte[max + 1];
				int length = bis.read(data);
				if (length <= 0)
					return "";
				if (length <= max)
					return new String(data, 0, length);
				if (ellipsis == null)
					return new String(data, 0, max);
				return new String(data, 0, max) + ellipsis;
			} else if (max < 0) { // "tail" mode: keep the last N
				int len;
				boolean rolled = false;
				byte[] last = null, data = null;
				do {
					if (last != null)
						rolled = true;
					byte[] tmp = last;
					last = data;
					data = tmp;
					if (data == null)
						data = new byte[-max];
					len = bis.read(data);
				} while (len == data.length);

				if (last == null && len <= 0)
					return "";
				if (last == null)
					return new String(data, 0, len);
				if (len > 0) {
					rolled = true;
					System.arraycopy(last, len, last, 0, last.length - len);
					System.arraycopy(data, 0, last, last.length - len, len);
				}
				if (ellipsis == null || !rolled)
					return new String(last);
				return ellipsis + new String(last);
			} else { // "cat" mode: size unknown, read it all in streaming
						// fashion
				ByteArrayOutputStream contents = new ByteArrayOutputStream();
				int len;
				byte[] data = new byte[1024];
				do {
					len = bis.read(data);
					if (len > 0)
						contents.write(data, 0, len);
				} while (len == data.length);
				return contents.toString();
			}
		} finally {
			bis.close();
			input.close();
		}
	}

	/**
	 * Writes string to file. Basically same as "echo -n $string > $filename"
	 * 把指定字符串写入文件中
	 * 
	 * @param filename
	 * @param string
	 * @throws IOException
	 */
	public static void stringToFile(String filename, String string)
			throws IOException {
		FileWriter out = new FileWriter(filename);
		try {
			out.write(string);
		} finally {
			out.close();
		}
	}

	/**
	 * Computes the checksum of a file using the CRC32 checksum routine. The
	 * value of the checksum is returned. 计算一个文件使用的CRC32校验程序校验。校验值返回。
	 * 
	 * @param file
	 *            the file to checksum, must not be null
	 * @return the checksum value or an exception is thrown.
	 */
	public static long checksumCrc32(File file) throws FileNotFoundException,
			IOException {
		CRC32 checkSummer = new CRC32();
		CheckedInputStream cis = null;

		try {
			cis = new CheckedInputStream(new FileInputStream(file), checkSummer);
			byte[] buf = new byte[128];
			while (cis.read(buf) >= 0) {
				// Just read for checksum to get calculated.
			}
			return checkSummer.getValue();
		} finally {
			if (cis != null) {
				try {
					cis.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
