package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Repository.COMMITS_OF_BRANCH_DIR;
import static gitlet.Repository.POINTER_OF_BRANCH_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Commit implements Serializable {

    private final String message;
    private final String timestamp;
    private final String parent;
    private TreeMap<String, String> filesMapping;

    /** No-argument constructor for the initial commit. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = (new Date(0)).toString();
        this.parent = null;
        this.filesMapping = new TreeMap<>();
    }

    /** Constructor with arguments. */
    public Commit(String message, String parent, TreeMap<String, String> prevFilesMapping) {
        this.message = message;
        this.timestamp = (new Date()).toString();
        this.parent = parent;
        /** Copy file-address mapping from parent's commit. */
        this.filesMapping.putAll(prevFilesMapping);
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParent() {
        return this.parent;
    }

    public TreeMap<String, String> getFilesMapping() {
        return this.filesMapping;
    }

    // TODO: save modified files into blobs

    public void saveCommit(String commitID, String branch) {
        File commit = join(COMMITS_OF_BRANCH_DIR, branch, commitID);
        writeObject(commit, this);
    }

    /** Helper method to read the parent commit info from computer. */
    public static Commit getParentCommit(String branch) {
        String parentCommitID = readContentsAsString(join(POINTER_OF_BRANCH_DIR, branch));
        File parentCommit = join(COMMITS_OF_BRANCH_DIR, branch, parentCommitID);
        return readObject(parentCommit, Commit.class);
    }


}
