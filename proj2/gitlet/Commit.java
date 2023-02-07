package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Xiaoli Li
 */
public class Commit implements Serializable {

    private final String message;
    private final String timestamp;
    private final String parentID;
    private TreeMap<String, String> filesMapping;

    /** No-argument constructor for the initial commit. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = String.format("%ta %<tb %<te %<tT %<tY %<tz", new Date(0));
        this.parentID = null;
        this.filesMapping = new TreeMap<>();
    }

    /** Constructor with arguments. */
    public Commit(String message, String parentID, TreeMap<String, String> filesMapping) {
        this.message = message;
        this.timestamp = String.format("%ta %<tb %<te %<tT %<tY %<tz", new Date());
        this.parentID = parentID;
        this.filesMapping = new TreeMap<>();
        this.filesMapping.putAll(filesMapping);
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParentID() {
        return this.parentID;
    }

    public TreeMap<String, String> getFilesMapping() {
        return this.filesMapping;
    }

    public void saveCommit(String commitID) {
        File commit = join(OBJECTS_DIR, commitID);
        writeObject(commit, this);
    }

    /** Helper methods to read commit info from disc. */
    public static Commit getCurrentCommit() {
        String commitID = Repository.getCurrentBranchPointer();
        return getCommit(commitID);
    }
    public static Commit getCommit(String commitID) {
        if (commitID == null) {
            return null;
        }
        File commit = join(OBJECTS_DIR, commitID);
        if (!commit.exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commit, Commit.class);
    }

    /** Read commits history for the current branch. */
    public static ArrayList<String[]> readCommitsHistory() {
        String branch = Repository.getHEAD();
        File currentBranchHistory = join(HISTORY_COMMITS_DIR, branch);
        if (!currentBranchHistory.exists()) {
            return new ArrayList<>();
        }
        return readObject(currentBranchHistory, ArrayList.class);
    }

    public static void saveCommitsHistory(ArrayList<String[]> commitsHistory) {
        String branch = Repository.getHEAD();
        File currentBranchHistory = join(HISTORY_COMMITS_DIR, branch);
        writeObject(currentBranchHistory, commitsHistory);
    }

    public static void addToCommitsHistory(Commit commit, String commitID) {
        String[] commitInfo = new String[]
                {commit.getParentID(), commitID, commit.getTimestamp(), commit.getMessage()};
        ArrayList<String[]> commitsHistory = Commit.readCommitsHistory();
        commitsHistory.add(commitInfo);
        saveCommitsHistory(commitsHistory);
    }
}


