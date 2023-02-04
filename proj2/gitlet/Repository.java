package gitlet;


import java.io.File;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** Store versioned files and history commit, using SHA1 as name. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** Store pointer (the latest commit) of each branch. File name is branch name. */
    public static final File POINTER_OF_BRANCH_DIR = join(GITLET_DIR, "refs", "heads");

    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();

        /** Initialize the branch as master branch. */
        String branch = "master";

        /** Make the initial commit and generate SHA1 of this commit. */
        Commit initialCommit = new Commit();
        String initialCommitID = sha1(serialize(initialCommit));
        initialCommit.saveCommit(initialCommitID);

        /** The master branch now points to initial commit. */
        POINTER_OF_BRANCH_DIR.mkdirs();
        Repository.setCurrentBranchPointer(initialCommitID);

        /** headPointer indicates the current branch. */
        // TODO: need to figure out how to store it
        File headPointer = join(GITLET_DIR, "HEAD");
    }

    public static void addCommand(String fileName) {
        /** Get SHA1 according to the file's content. */
        byte[] fileContent = Repository.readFileFromDisc(fileName);
        String fileSHA1 = sha1(fileContent);
        System.out.println("This is the SHA1 of this added file. " + fileSHA1);

        /** Get info from the staging area and parent commit. */
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> parentFilesMapping = Commit.getCurrentCommit().getFilesMapping();

        /**
         * If current working version of the file is the same as the version in parent commit,
         * the remove method will remove the file from staging area if it exists.
         * */
        if (fileSHA1.equals(parentFilesMapping.get(fileName))){
            if (stagingArea.remove(fileName) != null) {
                Repository.saveStagingArea(stagingArea);
            }
        }
        /** Following codes run if current working version of the file is different from parent commit. */
        else if (!stagingArea.containsKey(fileName) || !fileSHA1.equals(stagingArea.get(fileName))) {
            stagingArea.put(fileName, fileSHA1);
            Repository.saveStagingArea(stagingArea);
            Repository.writeFileToBlob(fileContent, fileSHA1);
        }
    }

    public static void commitCommand(String message) {
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        if (stagingArea.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        String parentID = Repository.getCurrentBranchPointer();

        TreeMap<String, String> filesMapping = Commit.getCurrentCommit().getFilesMapping();
        filesMapping.putAll(stagingArea);

        Commit newCommit = new Commit(message, parentID, filesMapping);
        String newCommitID = sha1(serialize(newCommit));
        newCommit.saveCommit(newCommitID);

        Repository.setCurrentBranchPointer(newCommitID);
        stagingArea.clear();
        Repository.saveStagingArea(stagingArea);
    }

    public static void checkoutFile(String fileName) {
        String parentID = Repository.getCurrentBranchPointer();
        checkoutFile(parentID, fileName);
    }

    public static void checkoutFile(String commitID, String fileName) {
        Commit commit = Commit.getCommit(commitID);
        TreeMap<String, String> filesMapping = commit.getFilesMapping();
        if (!filesMapping.containsKey(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        byte[] fileContent = Repository.readFileFromBlob(filesMapping.get(fileName));
        Repository.writeFileToDisk(fileContent, fileName);
    }

    public static void checkoutBranch(String branchName) {
        // TODO
    }

    public static void logCommand() {
        Commit commit = Commit.getCurrentCommit();
        String commitInfo;
        String commitsHistory = "";
        String parentID = Repository.getCurrentBranchPointer();
        while (commit != null) {
            commitInfo = "===" + "\n"
                    + "commit " + parentID + "\n"
                    + "Date: " + commit.getTimestamp() + "\n"
                    + commit.getMessage() + "\n"
                    + " " + "\n";

            commitsHistory = commitsHistory + commitInfo;
            commit = Commit.getCommit(commit.getParentID());
        }
        System.out.print(commitsHistory);
    }

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


    public static String getCurrentBranchPointer() {
        File currentBranchPointer = join(POINTER_OF_BRANCH_DIR, "master");
        return readContentsAsString(currentBranchPointer);
    }

    public static void setCurrentBranchPointer(String commitID) {
        File currentBranchPointer = join(POINTER_OF_BRANCH_DIR, "master");
        writeContents(currentBranchPointer, commitID);
    }

    public static TreeMap<String, String> getStagingArea() {
        File index = join(GITLET_DIR, "index");
        if (!index.exists()) {
            return new TreeMap<>();
        }
        return readObject(index, TreeMap.class);
    }

    public static void saveStagingArea(TreeMap<String, String> stagingArea) {
        File index = join(GITLET_DIR, "index");
        writeObject(index, stagingArea);
    }

}


