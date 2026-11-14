package nl.testinsight.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class CucumberAttachmentService {

    private final Path attachmentDirectory;

    public CucumberAttachmentService(
            @Value("${testinsight.storage.attachments-directory}")
            String attachmentDirectory) {

        this.attachmentDirectory =
                Path.of(attachmentDirectory)
                        .toAbsolutePath()
                        .normalize();
    }

    // Maximum allowed attachment size: 10 MB.
    private static final int MAX_ATTACHMENT_BYTES =
            10 * 1024 * 1024;

    // contains the processed scenario JSON and the files created during attachment processing.
    public record StoredScenario(
            JsonNode sourcePayload,
            List<Path> storedFiles
    ) {
    }

    // Recursively processes a JSON node and its children to find and handle attachments.
    public StoredScenario processAttachments(JsonNode scenario)
            throws IOException {

        // Create an independent copy of the original JSON.
        JsonNode sanitizedScenario = scenario.deepCopy();

        // Track files created during this operation.
        List<Path> storedFiles = new ArrayList<>();

        try {

            // Find and process all attachments in the JSON.
            processNode(sanitizedScenario, storedFiles);

            // Return the sanitized JSON and created files.
            return new StoredScenario(
                    sanitizedScenario,
                    List.copyOf(storedFiles)
            );

        } catch (IOException | RuntimeException exception) {

            // Attempt to remove files from this failed operation.
            deleteFiles(storedFiles);

            // Pass the original exception to the caller.
            throw exception;
        }
    }

    // Deletes a list of files, ignoring any errors that occur during deletion.
    private void processNode(
            JsonNode node,
            List<Path> storedFiles) throws IOException {

        if (node.isObject()) {

            ObjectNode object = (ObjectNode) node;

            // Check whether this object contains embeddings.
            JsonNode embeddings = object.path("embeddings");

            if (embeddings.isArray()) {

                for (JsonNode embedding : embeddings) {

                    if (embedding.isObject()) {
                        processEmbedding(
                                (ObjectNode) embedding,
                                storedFiles
                        );
                    }
                }
            }

            // Continue searching other nested JSON properties.
            for (var entry : object.properties()) {


                if (!entry.getKey().equals("embeddings")) {
                    processNode(entry.getValue(), storedFiles);
                }
            }

        } else if (node.isArray()) {

            // Process each element inside the JSON array.
            for (JsonNode child : node) {
                processNode(child, storedFiles);
            }
        }
    }

    private void processEmbedding(
            ObjectNode embedding,
            List<Path> storedFiles) throws IOException {

        // Read the base64 attachment data.
        JsonNode dataNode = embedding.path("data");

        if (!dataNode.isTextual()) {
            return;
        }

        // Read the MIME type of the attachment.
        String mimeType =
                embedding.path("mime_type").asText("");

        String extension;

        // Determine the file extension.
        switch (mimeType) {
            case "image/png" -> extension = ".png";
            case "image/jpeg" -> extension = ".jpg";
            default -> throw new IllegalArgumentException(
                    "Unsupported Cucumber attachment type: " + mimeType
            );
        }

        // Read the base64 string.
        String base64 = dataNode.asText();

        long maxEncodedLength =
                ((MAX_ATTACHMENT_BYTES + 2L) / 3L) * 4L;

        if (base64.length() > maxEncodedLength) {
            throw new IllegalArgumentException(
                    "Attachment exceeds maximum size"
            );
        }

        byte[] bytes;

        try {
            // Convert base64 into binary data.
            bytes = Base64.getDecoder().decode(base64);

        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid base64 attachment data",
                    exception
            );
        }

        // Verify the actual decoded size.
        if (bytes.length > MAX_ATTACHMENT_BYTES) {
            throw new IllegalArgumentException(
                    "Attachment exceeds maximum size"
            );
        }

        String filename = UUID.randomUUID() + extension;

        // Create the storage directory if it does not exist.
        Files.createDirectories(attachmentDirectory);

        // Determine the location of the new screenshot.
        Path destination = attachmentDirectory.resolve(filename);

        // Write the binary screenshot data to disk.
        Files.write(
                destination,
                bytes,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        );

        // Remember the file for possible cleanup.
        storedFiles.add(destination);

        // Remove the original base64 string from the copied JSON.
        embedding.remove("data");

        // Add the attachment reference and metadata.
        embedding.put("storage_uri", "attachments/" + filename);
        embedding.put("size_bytes", bytes.length);
    }

    /*
     * Deletes attachment files created during processing.
     *
     * This method is useful when something goes wrong
     * during an import.
     *
     * File cleanup is best-effort and does not replace
     * database transaction management.
     */
    public void deleteFiles(List<Path> files) {

        for (Path file : files) {

            try {
                Files.deleteIfExists(file);

            } catch (IOException exception) {

                // For now, print a safe error message.
                // Do not log attachment content.
                System.err.println(
                        "Failed to delete an attachment during cleanup"
                );
            }
        }
    }

}
