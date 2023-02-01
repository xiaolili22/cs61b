package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  TODO: does at a high level.
 *
 *  @author Xiaoli Li
 */
public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    public static final File POINTER_OF_BRANCH_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File COMMITS_OF_BRANCH_DIR = join(GITLET_DIR, "logs", "refs", "heads");

    //public Map<String, String> mapping;

    public static void initCommand() {
        // TODO: BUG here, run init twice should display error message and exit
        if (GITLET_DIR.exists()) {
            error("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        /** Create the .gitlet directory */
        GITLET_DIR.mkdir();
        /**
         * Create the directory to store all commits of each branch
         * File name is branch name, file content is all commits of that branch
         * */
        COMMITS_OF_BRANCH_DIR.mkdirs();
        /** Create the directory to store the pointer (the latest commit ID) of each branch */
        POINTER_OF_BRANCH_DIR.mkdirs();

        /** Initialize the branch as master branch */
        String branch = "master";

        /** Make the initial commit and generate SHA1 of this commit */
        Commit initialCommit = new Commit();
        String currentCommitID = sha1(serialize(initialCommit));
        System.out.println(currentCommitID);
        /** Save the commit to corresponding branch together with its ID */
        initialCommit.saveCommit(currentCommitID, branch);
        System.out.println("Print this line after saving the commit.");

        /** The master branch now points to initial commit */
        File masterBranchPointer = join(POINTER_OF_BRANCH_DIR, branch);
        writeContents(masterBranchPointer, currentCommitID);
        System.out.println("Print this line after saving commit ID");

        /**
         * Let the HEAD pointer points to master branch
         * Later when branch is changed, just update the headPointer file
         * */
        //File headPointer = join(GITLET_DIR, "HEAD");
        //File ref = join("refs", "heads", branch);
        //writeObject(headPointer, ref);

    }

    public static void addCommand(String fileName) {
        // TODO: add the file to staging area
    }

    public static void commitCommand(String message, Commit parent) {
        // Read from my computer the head commit object and the staging area
        // Clone the HEAD commit
        // Modify its message and timestamp according to user input
        // Use the staging area in order to modify the files tracked by the new commit
        // Write back any new object made or any modified objects read earlier

    }

    public static void logCommand() {
        // TODO: print logs of all the commits
    }

}


