package fr.univmobile.backend.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class QrCode {
	
	public static String getQrFileName(long poiId, Long imageMapId) {
		return String.format("qr_%s_%s.png", poiId, imageMapId);
	}
	
	public static String getQrFilePath(long poiId, long imageMapId, String qrBaseDir) {
		return Paths.get(qrBaseDir, getQrFileName(poiId, imageMapId)).toString();
	}
	
	public static void createQrCode(String content, String outputFilePath) throws IOException {
        ByteArrayOutputStream out = QRCode.from(content).to(ImageType.PNG).stream();
		
        FileOutputStream fout;
		fout = new FileOutputStream(new File(outputFilePath));
        fout.write(out.toByteArray());
        fout.flush();
        fout.close();
	}

	public static String composeQrURL(String linkPattern, String imageMapLink) {
		return linkPattern.replaceAll("%LINK%", imageMapLink);
	}

}
