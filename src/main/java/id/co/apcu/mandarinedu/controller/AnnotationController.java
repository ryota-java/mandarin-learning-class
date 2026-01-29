package id.co.apcu.mandarinedu.controller;

import id.co.apcu.mandarinedu.dto.AnnotationRequest;
import id.co.apcu.mandarinedu.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/annotations")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AnnotationController {

    @Autowired
    private AnnotationService service;

    /**
     * Save annotation data via HTTP POST
     * More reliable than WebSocket for large payloads
     */
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody AnnotationRequest request) {
        try {
            // Validation
            if (request == null || request.getRoomId() == null || request.getPageNumber() < 1) {
                System.err.println("❌ Invalid request: " + request);
                return ResponseEntity.badRequest().body("Invalid request data");
            }

            // Log size
            int dataSize = request.getFabricData() != null ? request.getFabricData().length() : 0;
            System.out.println("💾 HTTP Save - Room: " + request.getRoomId() +
                    ", Page: " + request.getPageNumber() +
                    ", Size: " + (dataSize / 1024) + "KB");

            // Save to database
            service.saveOrUpdate(request);

            System.out.println("✅ HTTP Save successful");
            return ResponseEntity.ok("Saved successfully");

        } catch (Exception e) {
            System.err.println("❌ HTTP save failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Save failed: " + e.getMessage());
        }
    }

    /**
     * Get annotation data via HTTP GET
     * More reliable than WebSocket for retrieving large data
     */
    @GetMapping("/get")
    public ResponseEntity<String> get(
            @RequestParam String roomId,
            @RequestParam int pageNumber
    ) {
        try {
            // Validation
            if (roomId == null || roomId.isEmpty() || pageNumber < 1) {
                System.err.println("❌ Invalid parameters: roomId=" + roomId + ", page=" + pageNumber);
                return ResponseEntity.badRequest().body("{\"objects\":[]}");
            }

            System.out.println("🔍 HTTP Get - Room: " + roomId + ", Page: " + pageNumber);

            // Get from database
            String data = service.getAnnotationData(roomId, pageNumber);

            if (data == null || data.isEmpty()) {
                System.out.println("📭 No data found, returning empty canvas");
                data = "{\"objects\":[]}";
            } else {
                System.out.println("📬 Data found, size: " + (data.length() / 1024) + "KB");
            }

            System.out.println("✅ HTTP Get successful");
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            System.err.println("❌ HTTP get failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"objects\":[]}");
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Annotation API is running");
    }
}