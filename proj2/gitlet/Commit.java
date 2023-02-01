package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import static gitlet.Repository.COMMITS_OF_BRANCH_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Commit implements Serializable {

    private String message;
    private String timestamp;
    private String parent;
    // TODO: Something that keeps track of what files this commit is tracking
    // TODO: ?? TreeMap that records a mapping of file names to blob references
    //public Map<String, String> fileBlobMapping;

    /** No-argument constructor for the initial commit. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = (new Date(0)).toString();
        this.parent = null;
    }

    /** Constructor with arguments. */
    public Commit(String message, String parent) {
        this.message = message;
        this.timestamp = (new Date()).toString();
        this.parent = parent;
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

    // TODO: save modified files into blobs

    public void saveCommit(String ID, String path) {
        File commits = join(COMMITS_OF_BRANCH_DIR, path);
        writeObject(commits, this);
    }


}
