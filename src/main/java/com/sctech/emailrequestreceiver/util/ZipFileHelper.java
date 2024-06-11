package com.sctech.emailrequestreceiver.util;

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
    public String fileContentFromZip(String desiredFileName, MultipartFile zipFile) {
        try {
            System.out.println("Requested File Name : " + desiredFileName);
            // Create a ZipInputStream to read the uploaded zip file
            ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream());

            // Extract each entry from the zip file
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // Check if the entry matches the desired file name
                System.out.println("Zip File Name : " + entry.getName());
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
                    String base64Data = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                    System.out.println("Content Base64" + base64Data);

                    // Close the ZipInputStream
                    zipInputStream.close();

                    // Optionally, you can further process the JSON object or return it as is
                    return base64Data;
                }
            }
            return "NotFound";
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
            return "Error";
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
