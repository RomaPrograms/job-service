package com.duallab.verapdf.jobservice.server.tools;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Maksim Bezrukov
 */

public class Zipper implements Closeable {

	private ZipOutputStream zipOut;

	private Zipper(FileOutputStream os) {
		this.zipOut = new ZipOutputStream(os);
	}

	public static Zipper init(File tempZip) throws FileNotFoundException {
		return new Zipper(new FileOutputStream(tempZip));
	}

	public void zipFile(File fileToZip) throws IOException {
		if (fileToZip.isFile()) {
			try (FileInputStream fis = new FileInputStream(fileToZip)) {
				ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
				zipOut.putNextEntry(zipEntry);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zipOut.write(bytes, 0, length);
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (zipOut != null) {
			zipOut.close();
		}
	}
}

