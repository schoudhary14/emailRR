package com.sctech.emailrequestreceiver.util;

import com.sctech.emailrequestreceiver.exceptions.InvalidRequestException;
import com.sctech.emailrequestreceiver.exceptions.NotExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipFileHelper {
    private static final Logger logger = LogManager.getLogger(ZipFileHelper.class);
    public byte[] fileContentFromZip(String desiredFileName, MultipartFile zipFile) {
        try {
            // Create a ZipInputStream to read the uploaded zip file
            ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream());

            // Extract each entry from the zip file
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Check if the entry matches the desired file name
                if (entry.getName().equals(desiredFileName)) {
                    // Create a ByteArrayOutputStream to hold the contents of the entry
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    // Write the contents of the entry to the ByteArrayOutputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // Convert the contents of the entry to a base64-encoded string
                    // String base64Data = Base64.getEncoder().encodeToString(outputStream.toByteArray());

                    // Close the ZipInputStream
                    zipInputStream.close();
                    // Optionally, you can further process the JSON object or return it as is
                    // return base64Data;
                    return outputStream.toByteArray();
                }
            }
            throw new NotExistsException("File not found in the zip file");
        } catch (IOException e) {
            logger.error("Error reading zip file : " + e.getMessage());
            throw new InvalidRequestException("Invalid Zip File");
        }
    }

    public String getFileContentType(String fileName) {
        String contentType = null;
        // Get the file extension from the file name
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            String fileExtension = fileName.substring(lastDotIndex + 1);
            // Get the content type for the file extension
            contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);

            // If the content type is null, try to guess it based on the file extension
            if (contentType == null) {
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                fileNameMap.getContentTypeFor("file." + fileExtension);
            }
        }else{
            contentType = "InvalidFileName";
        }

        return contentType;
    }

}
