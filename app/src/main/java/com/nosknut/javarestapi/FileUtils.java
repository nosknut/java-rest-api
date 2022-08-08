package com.nosknut.javarestapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileUtils {

    // https://developer.mozilla.org/en-US/docs/Glossary/Base64
    public static String encodeString(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    // https://developer.mozilla.org/en-US/docs/Glossary/Base64
    public static String encodeBytes(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    // https://developer.mozilla.org/en-US/docs/Glossary/Base64
    public static byte[] decodeByteString(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }

    public static String readFileToByteString(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        // Encodes data to ensure it remains intact during the transfer and protects it from charset conversions etc ...
        return encodeBytes(bytes);
    }

    public static void writeByteStringToFile(String path, String encodedByteString) throws IOException {
        // Encodes data to ensure it remains intact during the transfer and protects it from charset conversions etc ...
        byte[] bytes = decodeByteString(encodedByteString);
        File file = new File(path);
        // https://stackoverflow.com/questions/9620683/java-fileoutputstream-create-file-if-not-exists
        file.getParentFile().mkdirs();
        file.createNewFile();
        System.out.println("Writing file to " + path);
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            outputStream.write(bytes);
        }
    }
}
