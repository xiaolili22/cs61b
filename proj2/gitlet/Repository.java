package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Xiaoli Li
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** Stores versioned files and commits, using SHA1 as name. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** Stores pointer (the latest commit) of each branch. File name is branch name. */
    public static final File BRANCH_POINTER_DIR = join(GITLET_DIR, "refs", "heads");

    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();

        /** Creates the initial commit and generates SHA1 of this commit. */
        Commit initialCommit = new Commit();
        String initialCommitID = sha1(serialize(initialCommit));
        initialCommit.saveCommit(initialCommitID);

        /** Creates master branch, which points to the initial commit. */
        BRANCH_POINTER_DIR.mkdirs();
        File masterBranchPointer = join(BRANCH_POINTER_DIR, "master");
        writeContents(masterBranchPointer, initialCommitID);

        /** Let HEAD point to current branch (master branch for now). */
        Repository.setHEAD("master");
    }

    public static void addCommand(String fileName) {
        /** Gets SHA1 according to the file's content. */
        String fileContent = Blob.readFileFromDisc(fileName);
        String fileSHA1 = sha1(fileContent);

        /** Gets mapping info from the staging area and parent commit. */
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> parentFilesMapping = Commit.getCurrentCommit().getFilesMapping();

        /**
         * If current working version of the file is the same as in parent commit,
         * the remove method will remove the file from staging area if it exists.
         * */
        if (fileSHA1.equals(parentFilesMapping.get(fileName))) {
            if (stagingArea.remove(fileName) != null) {
                Repository.saveStagingArea(stagingArea);
            }
        /** If current working version of the file is different from parent commit. */
        } else if (!stagingArea.containsKey(fileName)
                || !fileSHA1.equals(stagingArea.get(fileName))) {
            stagingArea.put(fileName, fileSHA1);
            Repository.saveStagingArea(stagingArea);
            Blob.writeFileToBlob(fileContent, fileSHA1);
        }
    }

    /** Creates a normal commit. */
    public static void commitCommand(String message) {
        String parentID = Repository.getCurrentBranchPointer();
        mergeCommit(message, parentID, null);
    }
    /** Creates a commit for merging. */
    public static void mergeCommit(String message, String parentID1, String parentID2) {
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        if (stagingArea.isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        TreeMap<String, String> filesMapping = Commit.getCurrentCommit().getFilesMapping();
        /**
         * Iterates through stagingArea,
         * if the value for a key is "remove", then remove it from newCommit,
         * otherwise replace the value with the one in stagingArea.
         * */
        for (Map.Entry<String, String> entry : stagingArea.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals("remove")) {
                filesMapping.remove(key);
            } else {
                filesMapping.put(key, value);
            }
        }

        Commit newCommit = new Commit(message, parentID1, filesMapping);
        newCommit.setSecondParentID(parentID2);
        String newCommitID = sha1(serialize(newCommit));
        newCommit.saveCommit(newCommitID);

        Repository.setCurrentBranchPointer(newCommitID);
        stagingArea.clear();
        Repository.saveStagingArea(stagingArea);
    }

    public static void removeCommand(String fileName) {
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> filesMapping = Commit.getCurrentCommit().getFilesMapping();

        if (!stagingArea.containsKey(fileName) && !filesMapping.containsKey(fileName)) {
            message("No reason to remove the file.");
            System.exit(0);
        }
        /** Unstages the files if it's currently staged for addition. */
        if (stagingArea.containsKey(fileName)) {
            stagingArea.remove(fileName);
        }
        /** If tracked in current commit, stages it for removal and remove file from CWD. */
        if (filesMapping.containsKey(fileName)) {
            stagingArea.put(fileName, "remove");
            File file = join(CWD, fileName);
            restrictedDelete(file);
        }

        Repository.saveStagingArea(stagingArea);
    }

    public static void logCommand() {
        Commit commit = Commit.getCurrentCommit();
        String commitInfo;
        StringBuilder commitsHistory = new StringBuilder();
        String commitID = Repository.getCurrentBranchPointer();
        String mergeInfo = "";
        /** For commits with second parent. */
        if (commit.getSecondParentID() != null) {
            mergeInfo = "Merge: "
                    + commit.getParentID().substring(0, 7) + " "
                    + commit.getSecondParentID().substring(0, 7) + "\n";
        }
        while (commit != null) {
            commitInfo = "===" + "\n"
                    + "commit " + commitID + "\n"
                    + mergeInfo
                    + "Date: " + commit.getTimestamp() + "\n"
                    + commit.getMessage() + "\n"
                    + " " + "\n";

            commitsHistory.append(commitInfo);
            commitID = commit.getParentID();
            commit = Commit.getCommit(commitID);
        }
        System.out.print(commitsHistory);
    }

    public static void globalLogCommand() {
        StringBuilder allCommits = new StringBuilder();
        for (String fileName : Commit.commitFileNames()) {
            Commit commit = Commit.getCommit(fileName);
            String commitID = sha1(serialize(commit));
            String commitInfo = "===" + "\n"
                    + "commit " + commitID + "\n"
                    + "Date: " + commit.getTimestamp() + "\n"
                    + commit.getMessage() + "\n"
                    + " " + "\n";
            allCommits.append(commitInfo);
        }
        System.out.print(allCommits);
    }

    public static void findCommand(String message) {
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        StringBuilder allCommitIDs = new StringBuilder();
        for (String fileName : files) {
            if (fileName.length() <= 8) {
                Commit commit = Commit.getCommit(fileName);
                if (commit.getMessage().equals(message)) {
                    String commitID = sha1(serialize(commit));
                    allCommitIDs.append(commitID + "\n");
                }
            }
        }
        if (allCommitIDs.length() == 0) {
            message("Found no commit with that message.");
            System.exit(0);
        }
        System.out.print(allCommitIDs);
    }

    public static void statusCommand() {
        List<String> branchList = plainFilenamesIn(BRANCH_POINTER_DIR);
        List<String> cwdFiles = plainFilenamesIn(CWD);
        StringBuilder status = new StringBuilder();

        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> filesMapping = Commit.getCurrentCommit().getFilesMapping();
        ArrayList<String> stageForAdd = new ArrayList<>();
        ArrayList<String> stageForRemove = new ArrayList<>();
        ArrayList<String> modifiedNotStaged = new ArrayList<>();
        ArrayList<String> deletedNotStaged = new ArrayList<>();
        ArrayList<String> untracked = new ArrayList<>();

        status.append("=== Branches ===" + "\n");
        for (String str : branchList) {
            str = str.equals(Repository.getHEAD()) ? "*" + str : str;
            status.append(str + "\n");
        }
        for (Map.Entry<String, String> entry : stagingArea.entrySet()) {
            if (!entry.getValue().equals("remove")) {
                stageForAdd.add(entry.getKey());
                /** Staged for addition, but deleted in the working directory. */
                if (!join(CWD, entry.getKey()).exists()) {
                    deletedNotStaged.add(entry.getKey());
                }
            } else {
                stageForRemove.add(entry.getKey());
            }
        }
        Collections.sort(stageForAdd);
        Collections.sort(stageForRemove);
        status.append("\n" + "=== Staged Files ===" + "\n");
        for (String str : stageForAdd) {
            status.append(str + "\n");
        }
        status.append("\n" + "=== Removed Files ===" + "\n");
        for (String str : stageForRemove) {
            status.append(str + "\n");
        }
        for (String fileName : cwdFiles) {
            String fileContent = Blob.readFileFromDisc(fileName);
            if (filesMapping.containsKey(fileName)
                    && !sha1(fileContent).equals(filesMapping.get(fileName))
                    && !stagingArea.containsKey(fileName)) {
                modifiedNotStaged.add(fileName);
            } else if (Blob.isStagedToAdd(fileName, stagingArea)
                    && !sha1(fileContent).equals(stagingArea.get(fileName))) {
                modifiedNotStaged.add(fileName);
            } else if (!filesMapping.containsKey(fileName)
                    && !Blob.isStagedToAdd(fileName, stagingArea)) {
                untracked.add(fileName);
            }
        }
        /** Not staged for removal, but tracked in the current commit and deleted from CWD. */
        for (Map.Entry<String, String> entry : filesMapping.entrySet()) {
            if (!join(CWD, entry.getKey()).exists()
                    && !Blob.isStagedToRemove(entry.getKey(), stagingArea)) {
                if (!deletedNotStaged.contains(entry.getKey())) {
                    deletedNotStaged.add(entry.getKey());
                }
            }
        }
        Collections.sort(modifiedNotStaged);
        Collections.sort(deletedNotStaged);
        Collections.sort(untracked);
        status.append("\n" + "=== Modifications Not Staged For Commit ===" + "\n");
        for (String str : deletedNotStaged) {
            status.append(str + " (deleted)" + "\n");
        }
        for (String str : modifiedNotStaged) {
            status.append(str + " (modified)" + "\n");
        }
        status.append("\n" + "=== Untracked Files ===" + "\n");
        for (String str : untracked) {
            status.append(str + "\n");
        }
        System.out.println(status);
    }

    /** Checks out a file from current branch. */
    public static void checkoutFile(String fileName) {
        String commitID = Repository.getCurrentBranchPointer();
        checkoutFile(commitID, fileName);
    }
    /** Checks out a file from a given branch. */
    public static void checkoutFile(String commitID, String fileName) {
        Commit commit = Commit.getCommit(commitID);
        TreeMap<String, String> filesMapping = commit.getFilesMapping();
        if (!filesMapping.containsKey(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        String fileContent = Blob.readFileFromBlob(filesMapping.get(fileName));
        Blob.writeFileToDisk(fileContent, fileName);
    }
    /** Checks out an entire branch. */
    public static void checkoutBranch(String branch) {
        if (Repository.getHEAD().equals(branch)) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }

        String branchPointer = Repository.getBranchPointer(branch);
        TreeMap<String, String> branchFilesMapping
                = Commit.getCommit(branchPointer).getFilesMapping();
        TreeMap<String, String> currentFilesMapping
                = Commit.getCurrentCommit().getFilesMapping();
        updateCWD(branchFilesMapping, currentFilesMapping);

        /** Given branch is the current branch (HEAD) now. */
        Repository.setHEAD(branch);
        Repository.clearStagingArea();
    }
    /** Updates file contents in working directory,
     * given the mapping of file names to blob references. */
    private static void updateCWD(TreeMap<String, String> sourceFilesMapping,
                           TreeMap<String, String> currentFilesMapping) {
        /** Takes all files in the given branch, puts them into WD. */
        for (Map.Entry<String, String> entry : sourceFilesMapping.entrySet()) {
            String fileName = entry.getKey();
            String branchFileSHA1 = entry.getValue();

            if (!currentFilesMapping.containsKey(fileName)
                    && Blob.isOverwrittenBy(fileName, sourceFilesMapping)) {
                message("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
            String fileContent = Blob.readFileFromBlob(branchFileSHA1);
            Blob.writeFileToDisk(fileContent, fileName);
        }
        /** Deletes files tracked in the current branch
         * but are not present in the checked-out branch. */
        for (Map.Entry<String, String> entry : currentFilesMapping.entrySet()) {
            String fileName = entry.getKey();
            if (!sourceFilesMapping.containsKey(fileName)) {
                File file = join(CWD, fileName);
                restrictedDelete(file);
            }
        }
    }

    public static void branchCommand(String branch) {
        File branchPointer = join(BRANCH_POINTER_DIR, branch);
        if (branchPointer.exists()) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        /** Gets the current branch pointer and updates new branch's pointer. */
        String headPointer = Repository.getCurrentBranchPointer();
        writeContents(branchPointer, headPointer);
    }

    public static void removeBranchCommand(String branch) {
        String currentBranch = Repository.getHEAD();
        if (branch.equals(currentBranch)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }

        File branchPointer = join(BRANCH_POINTER_DIR, branch);
        if (!branchPointer.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        branchPointer.delete();
    }

    public static void resetCommand(String commitID) {
        /** Checks out all the files tracked by the given commit. */
        Commit commit = Commit.getCommit(commitID);
        TreeMap<String, String> sourceFilesMapping = commit.getFilesMapping();
        TreeMap<String, String> currentFilesMapping = Commit.getCurrentCommit().getFilesMapping();
        updateCWD(sourceFilesMapping, currentFilesMapping);
        /** Updates current branch's head. */
        Repository.setCurrentBranchPointer(commitID);
        Repository.clearStagingArea();
    }

    public static void mergeCommand(String branch) {
        if (!Repository.getStagingArea().isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        if (!join(BRANCH_POINTER_DIR, branch).exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        String currBranchName = getHEAD();
        if (currBranchName.equals(branch)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String otherBrPointer = Repository.getBranchPointer(branch);
        String currBrPointer = Repository.getCurrentBranchPointer();
        if (Commit.isAncestor(otherBrPointer, currBrPointer)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        String splitPoint = Commit.findSpitPoint(currBranchName, branch);
        if (splitPoint.equals(currBrPointer)) {
            checkoutBranch(branch);
            Repository.setCurrentBranchPointer(otherBrPointer);
            message("Current branch fast-forwarded.");
            System.exit(0);
        }
        TreeMap<String, String> currMapping = Commit.getCurrentCommit().getFilesMapping();
        TreeMap<String, String> splitMapping = Commit.getCommit(splitPoint).getFilesMapping();
        TreeMap<String, String> otherMapping = Commit.getCommit(otherBrPointer).getFilesMapping();
        /** First handles files only in given branch.
         * Because error may occur in this part and exit program. */
        for (Map.Entry<String, String> entry : otherMapping.entrySet()) {
            String otherFileName = entry.getKey();
            if (!splitMapping.containsKey(otherFileName)
                    && !currMapping.containsKey(otherFileName)) {
                if (Blob.isOverwrittenBy(otherFileName, otherMapping)) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
                checkoutFile(otherBrPointer, otherFileName);
                addCommand(otherFileName);
            }
        }
        /** Then iterates the files in split point. */
        for (Map.Entry<String, String> entry : splitMapping.entrySet()) {
            String fileName = entry.getKey();
            /** Handles file present and not modified in HEAD, but
             * 1, not present in the other.
             * 2, present in other but modified. */
            if (currMapping.containsKey(fileName)
                    && currMapping.get(fileName).equals(entry.getValue())) {
                if (!otherMapping.containsKey(fileName)) {
                    removeCommand(fileName);
                } else if (!otherMapping.get(fileName).equals(entry.getValue())) {
                    checkoutFile(otherBrPointer, fileName);
                    addCommand(fileName);
                }
            }
            /** Handles file modified differently in current and given branches. */
            if (Blob.isModifiedDiff(fileName, splitMapping, currMapping, otherMapping)) {
                Blob.handleConflict(fileName, currMapping, otherMapping);
                addCommand(fileName);
            }
        }
        /** Handles files in current branch but not present in split point. */
        for (Map.Entry<String, String> entry : currMapping.entrySet()) {
            String currFileName = entry.getKey();
            if (!splitMapping.containsKey(currFileName)
                    && otherMapping.containsKey(currFileName)
                    && !otherMapping.get(currFileName).equals(entry.getValue())) {
                Blob.handleConflict(currFileName, currMapping, otherMapping);
                addCommand(currFileName);
            }
        }
        /** Creates commit for the merging. */
        String message = "Merged " + branch +  " into " + currBranchName + ".";
        mergeCommit(message, currBrPointer, otherBrPointer);
    }

    /** Sets the HEAD pointer. */
    public static void setHEAD(String branch) {
        File head = join(GITLET_DIR, "HEAD");
        String info = "ref: refs/heads/" + branch;
        writeContents(head, info);
    }

    /** Returns current branch's name. */
    public static String getHEAD() {
        File head = join(GITLET_DIR, "HEAD");
        String[] info = readContentsAsString(head).split("/");
        return info[info.length - 1];
    }

    /** Returns current branch's latest commit ID. */
    public static String getCurrentBranchPointer() {
        String branch = getHEAD();
        return getBranchPointer(branch);
    }

    /** Returns given branch's pointer (latest commit ID). */
    public static String getBranchPointer(String branch) {
        File branchPointer = join(BRANCH_POINTER_DIR, branch);
        if (!branchPointer.exists()) {
            message("No such branch exists.");
            System.exit(0);
        }
        return readContentsAsString(branchPointer);
    }

    public static void setCurrentBranchPointer(String commitID) {
        String branch = getHEAD();
        File currentBranchPointer = join(BRANCH_POINTER_DIR, branch);
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

    public static void clearStagingArea() {
        TreeMap<String, String> stagingArea = getStagingArea();
        stagingArea.clear();
        saveStagingArea(stagingArea);
    }
}




