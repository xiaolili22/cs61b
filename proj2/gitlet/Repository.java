package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The logs directory. */
    public static final File logs = join(GITLET_DIR, "logs");
    /** The objects directory. */
    public static final File objects = join(GITLET_DIR, "objects");
    public static final File index = join(GITLET_DIR, "index");

    public Map<String, String> mapping;

    public static void initCommand() throws IOException {
        if (GITLET_DIR.exists()) {
            error("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        /** Create the .gitlet and other directories */
        GITLET_DIR.mkdir();
        logs.createNewFile();
        objects.mkdir();
        index.createNewFile();

        /** Make the initial commit */
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
    }

    public static void addCommand(String fileName) {
        // TODO: add the file to staging area
    }

    public static void commitCommand(String message) {
        // TODO: handle commit command
        Date commitTime = new Date();
        Commit c = new Commit(message, commitTime);
        c.saveCommit();
    }

    // TODO: use ArrayList of string array to store all the commits.
    // TODO: read from the logs file to print all the commits.
    public static void logCommand() {
        // TODO: print logs of all the commits
    }

    private void updateMapping() {
        // TODO:
    }
}

