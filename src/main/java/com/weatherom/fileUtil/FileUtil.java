package com.weatherom.fileUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static void writeOnFile (String fileName, String text) {
        boolean isInList = false;

        List<String> fileContent = readFile(fileName);
        // Check if the city name is already in the file
        for (String cityName : fileContent) {
            if (cityName.equals(text)) {
                isInList = true;
            }
        }

        // If the city name is not in the file, then we write it, otherwise it won't be written again
        if (!isInList) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
                bw.write(text);
                bw.newLine();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public static List<String> readFile (String filePath) {
        try {
            return Files.readAllLines(Path.of(filePath));

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return List.of();
        }
    }

    public static void removeFromFile (String cityName, String fileName) {
        List<String> fileContent = readFile(fileName);

        // Creating a new mutable list and filtering out the removed city
        List<String> updatedContent = new ArrayList<>();
        for (String line : fileContent) {
            if (!line.trim().equalsIgnoreCase(cityName)) {
                updatedContent.add(line);
            }
        }

        // Write the file again from the beginning without the removed city
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (String city : updatedContent) {
                bw.write(city);
                bw.newLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
