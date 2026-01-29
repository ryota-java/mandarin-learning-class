package id.co.apcu.mandarinedu.service;

import id.co.apcu.mandarinedu.dto.AnnotationRequest;
import id.co.apcu.mandarinedu.model.Annotation;
import id.co.apcu.mandarinedu.repository.AnnotationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class AnnotationService {
    @Autowired
    private AnnotationRepository repository;

    @Transactional
    public void saveOrUpdate(AnnotationRequest request) {
        if (request.getRoomId() == null || request.getPageNumber() < 1) return;

        String dataToSave = request.getFabricData();

        // 🆕 Handle compressed data
        if (Boolean.TRUE.equals(request.getIsCompressed())) {
            System.out.println("📦 Data is compressed, storing as-is");
            // Data sudah dalam bentuk base64 compressed, simpan langsung
            // dengan prefix untuk identifikasi
            dataToSave = "gzip:" + dataToSave;
        }

        // Log size
        int dataSize = dataToSave != null ? dataToSave.length() : 0;
        System.out.println("💾 Saving annotation - Room: " + request.getRoomId() +
                ", Page: " + request.getPageNumber() +
                ", Size: " + (dataSize / 1024) + "KB" +
                (Boolean.TRUE.equals(request.getIsCompressed()) ? " (compressed)" : ""));

        int updated = repository.updateFabricData(
                request.getRoomId(),
                request.getPageNumber(),
                dataToSave
        );

        if (updated == 0) {
            Annotation annotation = new Annotation();
            annotation.setRoomId(request.getRoomId());
            annotation.setPageNumber(request.getPageNumber());
            annotation.setFabricData(dataToSave);
            repository.save(annotation);
        }
    }

    public String getAnnotationData(String roomId, int pageNumber) {
        return repository.findByRoomIdAndPageNumber(roomId, pageNumber)
                .map(annotation -> {
                    String data = annotation.getFabricData();

                    // 🆕 Check if data is compressed
                    if (data != null && data.startsWith("gzip:")) {
                        System.out.println("📦 Data is compressed, returning as-is for client decompression");
                        // Return tanpa prefix, client akan decompress
                        return "\"" + data.substring(5) + "\"";  // Remove "gzip:" prefix and wrap in quotes
                    }

                    // Data tidak terkompress, return normal
                    return data;
                })
                .orElse("{\"objects\":[]}");
    }

    // 🆕 Optional: Utility methods for server-side compression/decompression
    // (Jika Anda ingin server yang handle compression, bukan client)

    public static String compress(String data) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
            gzipStream.write(data.getBytes(StandardCharsets.UTF_8));
            gzipStream.close();

            byte[] compressed = byteStream.toByteArray();
            return Base64.getEncoder().encodeToString(compressed);
        } catch (Exception e) {
            System.err.println("❌ Compression failed: " + e.getMessage());
            return data;  // Return uncompressed if failed
        }
    }

    public static String decompress(String compressedBase64) {
        try {
            byte[] compressed = Base64.getDecoder().decode(compressedBase64);
            GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(compressed));

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                byteStream.write(buffer, 0, len);
            }

            gzipStream.close();
            return byteStream.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            System.err.println("❌ Decompression failed: " + e.getMessage());
            return compressedBase64;  // Return as-is if failed
        }
    }
}