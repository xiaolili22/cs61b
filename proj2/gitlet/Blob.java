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
    public static String readFileFromDisc(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        return readContentsAsString(file);
    }

    public static void writeFileToDisk(String fileContent, String fileName) {
        writeContents(join(CWD, fileName), fileContent);
    }

    public static String readFileFromBlob(String fileSHA1) {
        return readContentsAsString(join(OBJECTS_DIR, fileSHA1));
    }

    public static void writeFileToBlob(String fileContent, String fileSHA1) {
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

    public static boolean isChangedDiff(String fileName,
                                        TreeMap<String, String> splitMapping,
                                        TreeMap<String, String> currMapping,
                                        TreeMap<String, String> otherMapping) {
        if (splitMapping.get(fileName).equals(currMapping.get(fileName))) {
            return false;
        }
        if (splitMapping.get(fileName).equals(otherMapping.get(fileName))) {
            return false;
        }
        if (!currMapping.containsKey(fileName) && !otherMapping.containsKey(fileName)) {
            return false;
        }
        if (currMapping.get(fileName).equals(otherMapping.get(fileName))) {
            return false;
        }
        return true;
    }

    public static void handleConflict(String fileName,
                                      TreeMap<String, String> currMapping,
                                      TreeMap<String, String> otherMapping) {
        StringBuilder result = new StringBuilder();
        result.append("<<<<<<< HEAD" + "\n");
        String currFileContent = " ";
        if (currMapping.containsKey(fileName)) {
            currFileContent = readFileFromBlob(currMapping.get(fileName));
        }
        result.append(currFileContent + "\n");

        result.append("=======" + "\n");
        String otherFileContent = " ";
        if (otherMapping.containsKey(fileName)) {
            otherFileContent = readFileFromBlob(otherMapping.get(fileName));
        }
        result.append(otherFileContent + "\n");
        result.append(">>>>>>>" + "\n");

        writeFileToDisk(result.toString(), fileName);

        message("Encountered a merge conflict.");
    }
}
