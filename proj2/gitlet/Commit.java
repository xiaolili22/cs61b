package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

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
    private String secondParentID;
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

    public String getSecondParentID() {
        return this.secondParentID;
    }

    public void setSecondParentID(String parentID) {
        this.secondParentID = parentID;
    }

    public TreeMap<String, String> getFilesMapping() {
        return this.filesMapping;
    }

    public void saveCommit(String commitID) {
        String abbrCommitID = commitID.substring(0, 8);
        File commit = join(OBJECTS_DIR, abbrCommitID);
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
        String abbrCommitID = commitID.substring(0, 8);
        File commit = join(OBJECTS_DIR, abbrCommitID);
        if (!commit.exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commit, Commit.class);
    }

    /** Generate file names array of all commits. */
    public static List<String> commitFileNames() {
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        List<String> commitFiles = new ArrayList<>();
        for (String file : files) {
            if (file.length() <= 8) {
                commitFiles.add(file);
            }
        }
        return commitFiles;
    }

    public static ArrayList<String> findAncestors(String commitID) {
        ArrayList<String> ancestors = new ArrayList<>();
        Queue<String> parents = new ArrayDeque<>();

        ancestors.add(commitID);
        parents.add(commitID);

        while (!parents.isEmpty()) {
            String aCommit = parents.remove();
            Commit commit = Commit.getCommit(aCommit);

            String parent1 = commit.getParentID();
            if (parent1 != null && !ancestors.contains(parent1)) {
                ancestors.add(parent1);
                parents.add(parent1);
            }

            String parent2 = commit.getSecondParentID();
            if (parent2 != null && !ancestors.contains(parent2)) {
                ancestors.add(parent2);
                parents.add(parent2);
            }
        }
        return ancestors;
    }

    public static String findSpitPoint(String branch1, String branch2) {
        String commitID1 = Repository.getBranchPointer(branch1);
        String commitID2 = Repository.getBranchPointer(branch2);

        ArrayList<String> commit1Ancestors = findAncestors(commitID1);
        ArrayList<String> commit2Ancestors = findAncestors(commitID2);

        String latestCommonAncestor = null;
        for (String ancestor : commit2Ancestors) {
            if (commit1Ancestors.contains(ancestor)) {
                latestCommonAncestor = ancestor;
                break;
            }
        }
        return latestCommonAncestor;
    }
}



