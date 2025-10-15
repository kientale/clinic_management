package com.kien.project.clinicmanagement.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrFromJsonFile {

    public static void generateQrFromJsonResource(String resourcePath, String outputQrPath, int size)
            throws IOException, WriterException {

        // 1. Đọc file JSON từ resources (UTF-8)
        try (InputStream is = QrFromJsonFile.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Không tìm thấy file trong resources: " + resourcePath);
            }
            @SuppressWarnings("resource")
			String jsonData = new Scanner(is, StandardCharsets.UTF_8)
                    .useDelimiter("\\A").next();

            // 2. Tạo QR Code từ chuỗi JSON (UTF-8)
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(jsonData, BarcodeFormat.QR_CODE, size, size, hints);

            // 3. Xuất ảnh QR ra file PNG
            Path outputPath = Paths.get(outputQrPath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);

            System.out.println("✅ QR code created: " + outputQrPath);
        }
    }

    public static void main(String[] args) throws Exception {
        // Đường dẫn bắt đầu bằng "/" → tính từ gốc thư mục resources
        String resourcePath = "/patient_qr/patient_info/patient1.json";

        // Chỗ này bạn nên đặt đường dẫn tuyệt đối hoặc relative tới thư mục output ảnh
        String outputQr = "src/main/resources/patient_qr/patient_qr_code/patient1_qr.png";

        generateQrFromJsonResource(resourcePath, outputQr, 300);
    }
}
