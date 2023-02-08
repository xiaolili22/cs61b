package gitlet;


import java.io.File;
import java.util.TreeMap;

import static gitlet.Repository.CWD;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;
import static gitlet.Utils.join;

/**
 * Represents a file object.
 * This class contains methods of file operations.
 */
public class Blob {
    public static byte[] readFileFromDisc(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        return readContents(file);
    }

    public static void writeFileToDisk(byte[] fileContent, String fileName) {
        writeContents(join(CWD, fileName), fileContent);
    }

    public static byte[] readFileFromBlob(String fileSHA1) {
        return readContents(join(OBJECTS_DIR, fileSHA1));
    }

    public static void writeFileToBlob(byte[] fileContent, String fileSHA1) {
        writeContents(join(OBJECTS_DIR, fileSHA1), fileContent);
    }

    public static boolean isStagedToAdd(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && !stagingArea.get(fileName).equals("remove");
    }

    public static boolean isStagedToRemove(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && stagingArea.get(fileName).equals("remove");
    }

    public static boolean isOverwrittenBy(String fileName, TreeMap<String, String> filesMapping) {
        return (join(CWD, fileName)).exists()
                && !sha1(Blob.readFileFromDisc(fileName)).equals(filesMapping.get(fileName));
    }
}
