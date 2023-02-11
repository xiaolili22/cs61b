package gitlet;

import java.io.File;
import java.util.TreeMap;

import static gitlet.Repository.CWD;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;
import static gitlet.Utils.join;

/**
 * A collection of methods of file related operations.
 * A Blob object is saved contents of a file on disk.
 *
 * @author Xiaoli Li
 */
public class Blob {
    /** Reads from file on disk, return the entire content as a string. */
    public static String readFileFromDisc(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        return readContentsAsString(file);
    }
    /** Writes the updated string content back to file on disk. */
    public static void writeFileToDisk(String fileContent, String fileName) {
        writeContents(join(CWD, fileName), fileContent);
    }
    /** Reads from blob object, returns history file content. */
    public static String readFileFromBlob(String fileSHA1) {
        return readContentsAsString(join(OBJECTS_DIR, fileSHA1));
    }
    /** Saves the file content as a blob object. */
    public static void writeFileToBlob(String fileContent, String fileSHA1) {
        writeContents(join(OBJECTS_DIR, fileSHA1), fileContent);
    }
    /** Checks if the file is staged to add. */
    public static boolean isStagedToAdd(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && !stagingArea.get(fileName).equals("remove");
    }
    /** Checks if the file is staged to remove.
     * Given the key as fileName, if value is "remove" then return true. */
    public static boolean isStagedToRemove(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && stagingArea.get(fileName).equals("remove");
    }
    /** Checks if the file content is going to be overwritten by history content. */
    public static boolean isOverwrittenBy(String fileName, TreeMap<String, String> filesMapping) {
        return (join(CWD, fileName)).exists()
                && !sha1(Blob.readFileFromDisc(fileName)).equals(filesMapping.get(fileName));
    }
    /** Comparing to one common ancestor, checks if the file is modified
     * and not in the same way in two different branches. */
    public static boolean isModifiedDiff(String fileName,
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
    /** Updates file content when there is merge conflict. */
    public static void handleConflict(String fileName,
                                      TreeMap<String, String> currMapping,
                                      TreeMap<String, String> otherMapping) {
        StringBuilder result = new StringBuilder();
        result.append("<<<<<<< HEAD" + "\n");
        String currFileContent = "";
        if (currMapping.containsKey(fileName)) {
            currFileContent = readFileFromBlob(currMapping.get(fileName));
        }
        result.append(currFileContent + "\n");

        result.append("=======" + "\n");
        String otherFileContent = "";
        if (otherMapping.containsKey(fileName)) {
            otherFileContent = readFileFromBlob(otherMapping.get(fileName));
        }
        result.append(otherFileContent);
        result.append(">>>>>>>");

        writeFileToDisk(result.toString(), fileName);
        message("Encountered a merge conflict.");
    }
}

