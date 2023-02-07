package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
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
    public static final File BRANCH_POINTER_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File HISTORY_COMMITS_DIR = join(GITLET_DIR, "logs", "refs", "heads");

    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        HISTORY_COMMITS_DIR.mkdirs();

        /** Make the initial commit and generate SHA1 of this commit. */
        Commit initialCommit = new Commit();
        String initialCommitID = sha1(serialize(initialCommit));
        initialCommit.saveCommit(initialCommitID);

        /** Create master branch, which points to the initial commit. */
        BRANCH_POINTER_DIR.mkdirs();
        File masterBranchPointer = join(BRANCH_POINTER_DIR, "master");
        writeContents(masterBranchPointer, initialCommitID);

        /** Let HEAD point to current branch (master branch for now). */
        Repository.setHEAD("master");

        Commit.addToCommitsHistory(initialCommit, initialCommitID);
    }

    public static void addCommand(String fileName) {
        /** Get SHA1 according to the file's content. */
        byte[] fileContent = Repository.readFileFromDisc(fileName);
        String fileSHA1 = sha1(fileContent);

        /** Get mapping info from the staging area and parent commit. */
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> parentFilesMapping = Commit.getCurrentCommit().getFilesMapping();

        /**
         * If current working version of the file is the same as the version in parent commit,
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
        /**
         * Iterate through stagingArea,
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

        Commit newCommit = new Commit(message, parentID, filesMapping);
        String newCommitID = sha1(serialize(newCommit));
        newCommit.saveCommit(newCommitID);

        Repository.setCurrentBranchPointer(newCommitID);
        Commit.addToCommitsHistory(newCommit, newCommitID);

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
        /** Unstage the files if it's currently staged for addition. */
        if (stagingArea.containsKey(fileName)) {
            stagingArea.remove(fileName);
        }
        /** If tracked in current commit, stage it for removal and remove file from the WD. */
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
        while (commit != null) {
            commitInfo = "===" + "\n"
                    + "commit " + commitID + "\n"
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
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        StringBuilder allCommits = new StringBuilder();
        for (String fileName : files) {
            try {
                Commit commit = Commit.getCommit(fileName);
                String commitID = sha1(serialize(commit));
                String commitInfo = "===" + "\n"
                        + "commit " + commitID + "\n"
                        + "Date: " + commit.getTimestamp() + "\n"
                        + commit.getMessage() + "\n"
                        + " " + "\n";
                allCommits.append(commitInfo);
            } catch (Exception e) {
            }
        }
        System.out.print(allCommits);
    }

    public static void findCommand(String message) {
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        StringBuilder allCommitIDs = new StringBuilder();
        for (String fileName : files) {
            try {
                Commit commit = Commit.getCommit(fileName);
                if (commit.getMessage().equals(message)) {
                    String commitID = sha1(serialize(commit));
                    allCommitIDs.append(commitID + "\n");
                }
            } catch (Exception e) {
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
        String currentBranch = Repository.getHEAD();
        TreeMap<String, String> stagingArea = Repository.getStagingArea();
        TreeMap<String, String> filesMapping = Commit.getCurrentCommit().getFilesMapping();
        ArrayList<String> stageForAdd = new ArrayList<>();
        ArrayList<String> stageForRemove = new ArrayList<>();
        ArrayList<String> modifiedNotStaged = new ArrayList<>();
        ArrayList<String> deletedNotStaged = new ArrayList<>();
        ArrayList<String> untracked = new ArrayList<>();

        status.append("=== Branches ===" + "\n");
        for (String str : branchList) {
            if (str.equals(currentBranch)) {
                str = "*" + str;
            }
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
            byte[] fileContent = Repository.readFileFromDisc(fileName);
            if (filesMapping.containsKey(fileName)
                    && !sha1(fileContent).equals(filesMapping.get(fileName))
                    && !stagingArea.containsKey(fileName)) {
                modifiedNotStaged.add(fileName);
            } else if (isStagedToAdd(fileName, stagingArea)
                    && !sha1(fileContent).equals(stagingArea.get(fileName))) {
                modifiedNotStaged.add(fileName);
            } else if (!filesMapping.containsKey(fileName)
                    && !isStagedToAdd(fileName, stagingArea)) {
                untracked.add(fileName);
            }
        }
        /** Not staged for removal, but tracked in the current commit and deleted from the wd. */
        for (Map.Entry<String, String> entry : filesMapping.entrySet()) {
            if (!join(CWD, entry.getKey()).exists()
                    && !isStagedToRemove(entry.getKey(), stagingArea)) {
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

    private static void updateCWD(TreeMap<String, String> sourceFilesMapping,
                           TreeMap<String, String> currentFilesMapping) {
        /** Take all files in the head of the given branch, put them into WD. */
        for (Map.Entry<String, String> entry : sourceFilesMapping.entrySet()) {
            String fileName = entry.getKey();
            String branchFileSHA1 = entry.getValue();

            if (!currentFilesMapping.containsKey(fileName)
                    && isOverwrittenBy(sourceFilesMapping, fileName)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            byte[] fileContent = Repository.readFileFromBlob(branchFileSHA1);
            Repository.writeFileToDisk(fileContent, fileName);
        }
        for (Map.Entry<String, String> entry : currentFilesMapping.entrySet()) {
            String fileName = entry.getKey();
            if (!sourceFilesMapping.containsKey(fileName)) {
                File file = join(CWD, fileName);
                restrictedDelete(file);
            }
        }
    }

    private static boolean isOverwrittenBy(TreeMap<String, String> filesMapping, String fileName) {
        return (join(CWD, fileName)).exists()
                && !sha1(Repository.readFileFromDisc(fileName)).equals(filesMapping.get(fileName));
    }

    public static void branchCommand(String branch) {
        File branchPointer = join(BRANCH_POINTER_DIR, branch);
        if (branchPointer.exists()) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        /** Get the current branch pointer and write to new branch's pointer file. */
        String headPointer = Repository.getCurrentBranchPointer();
        writeContents(branchPointer, headPointer);
        /** copy commits history to this branch's file under logs directory. */
        ArrayList<String[]> inheritedHistory = Commit.readCommitsHistory();
        File branchHistory = join(HISTORY_COMMITS_DIR, branch);
        writeObject(branchHistory, inheritedHistory);
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

        File branchHistory = join(HISTORY_COMMITS_DIR, branch);
        branchHistory.delete();
    }

    public static void resetCommand(String commitID) {
        /** First checkout to the commit. */
        Commit commit = Commit.getCommit(commitID);
        TreeMap<String, String> sourceFilesMapping = commit.getFilesMapping();
        TreeMap<String, String> currentFilesMapping = Commit.getCurrentCommit().getFilesMapping();
        updateCWD(sourceFilesMapping, currentFilesMapping);
        /** Update the current branch head. */
        Repository.setCurrentBranchPointer(commitID);
        Repository.clearStagingArea();
        /** Rebuild the commits history. */
        ArrayList<String[]> newCommitsHistory = new ArrayList<>();
        while (commit != null) {
            String[] commitInfo = new String[]{
                    commit.getParentID(),
                    commitID,
                    commit.getTimestamp(),
                    commit.getMessage()
            };
            newCommitsHistory.add(0, commitInfo);
            commitID = commit.getParentID();
            commit = Commit.getCommit(commitID);
        }
        Commit.saveCommitsHistory(newCommitsHistory);
    }

    public static void mergeCommand(String branch) {
        // TODO
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

    public static void setHEAD(String branch) {
        File head = join(GITLET_DIR, "HEAD");
        String info = "ref: refs/heads/" + branch;
        writeContents(head, info);
    }
    /** Return current active branch's name. */
    public static String getHEAD() {
        File head = join(GITLET_DIR, "HEAD");
        String[] info = readContentsAsString(head).split("/");
        return info[info.length - 1];
    }

    /** Return current active branch's latest commit ID. */
    public static String getCurrentBranchPointer() {
        String branch = getHEAD();
        return getBranchPointer(branch);
    }
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

    private static boolean isStagedToAdd(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && !stagingArea.get(fileName).equals("remove");
    }

    private static boolean isStagedToRemove(String fileName, TreeMap<String, String> stagingArea) {
        return stagingArea.containsKey(fileName) && stagingArea.get(fileName).equals("remove");
    }


}



