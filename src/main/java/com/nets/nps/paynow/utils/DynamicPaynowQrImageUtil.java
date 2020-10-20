package com.nets.nps.paynow.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.nets.upos.commons.logger.ApsLogger;
import com.nets.upos.commons.utils.DynamicQrImageUtil;

public class DynamicPaynowQrImageUtil {
	private static final ApsLogger logger = new ApsLogger(DynamicQrImageUtil.class);

	static String path = "/images/paynow.png";

	public String generateQrImage(String qrData, int imageSize) {

		Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {

			bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, imageSize, imageSize, hints);

			MatrixToImageConfig config = new MatrixToImageConfig( MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);

			// Load QR image
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

			// Load logo image
			BufferedImage logoImage = ImageIO.read(this.getClass().getResourceAsStream(path));

			// Calculate the delta height and width between QR code and logo
			int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
			int deltaWidth = qrImage.getWidth() - logoImage.getWidth();

			// Initialize combined image
			BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) combined.getGraphics();

			// Write QR code to new image at position 0/0
			g.drawImage(qrImage, 0, 0, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			// Write logo into combine image at position (deltaWidth / 2) and
			// (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
			// the same space for the logo to be centered
			g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

			// Write combined image as PNG to OutputStream
			ImageIO.write(combined, "png", baos);

		} catch (WriterException e) {
			logger.error("WriterException occured", e);
		} catch (IOException e) {
			logger.error("IOException occured", e);

		}

		byte[] dataInBytes = baos.toByteArray();
		byte[] encodedBytes = java.util.Base64.getEncoder().encode(dataInBytes);
		
		baos=null;
		dataInBytes=null;
		return new String(encodedBytes);
	}
}
