package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.logs;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public String message;
    /** Commit time. */
    public String timestamp;
    /** SHA1 of this Commit. */
    public String ID;
    /** TreeMap that records a mapping of file names to blob references. */
    public Map<String, String> fileBlobMapping;

    /** No-argument constructor for the initial commit. */
    public Commit() {
        this.message = "initial commit";
        this.timestamp = (new Date(0)).toString();
        fileBlobMapping = new TreeMap<>();
        //setID();
    }

    /** Constructor with arguments. */
    public Commit(String message, Date commitTime) {
        this.message = message;
        this.timestamp = commitTime.toString();
        handleStagingArea();
        //setID();
    }

    /** Read from logs to retrieve the commits history. */
    public static LinkedList<Commit> getCommitsHistory() {
        return (LinkedList<Commit>) readObject(logs, LinkedList.class);
    }

    private void setID() {
        this.ID = Utils.sha1(this);
    }

    // TODO: save modified files into blobs
    /**
     * All modified files will be saved at following path
     * .gitlet/objects/[first_two_digits_of_this_commit_id]/[leftover_digits_of_this_commit_id]
     * */
    private void handleStagingArea() {
        // Map<String, String> stagingArea = readObject(index);
        Map<String, String> prevMapping = Commit.getCommitsHistory().get(0).fileBlobMapping;
        // TODO:
        return;
    }

    public void saveCommit() {
        LinkedList<Commit> commits = Commit.getCommitsHistory();
        /** Update the commits history and save it back to file. */
        commits.addFirst(this);
        writeObject(logs, commits);
    }
}
