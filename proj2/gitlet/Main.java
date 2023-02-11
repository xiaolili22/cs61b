package gitlet;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *
 *  @author Xiaoli Li
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        /** If args is empty */
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            /** Usage: java gitlet.Main init */
            case "init":
                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;
            /** Usage: java gitlet.Main add [file name] */
            case "add":
                validateInit();
                validateNumArgs("add", args, 2);
                Repository.addCommand(args[1]);
                break;
            /** Usage: java gitlet.Main commit [message] */
            case "commit":
                validateInit();
                if (args.length == 1 || args[1].trim().isEmpty()) {
                    message("Please enter a commit message.");
                    break;
                }
                validateNumArgs("commit", args, 2);
                Repository.commitCommand(args[1]);
                break;
            /** Usage: java gitlet.Main rm [file name] */
            case "rm":
                validateInit();
                validateNumArgs("rm", args, 2);
                Repository.removeCommand(args[1]);
                break;
            /** Usage: java gitlet.Main log */
            case "log":
                validateInit();
                validateNumArgs("log", args, 1);
                Repository.logCommand();
                break;
            /** Usage: java gitlet.Main global-log */
            case "global-log":
                validateInit();
                validateNumArgs("global-log", args, 1);
                Repository.globalLogCommand();
                break;
            /** Usage: java gitlet.Main find [commit message] */
            case "find":
                validateInit();
                validateNumArgs("find", args, 2);
                Repository.findCommand(args[1]);
                break;
            /** Usage: java gitlet.Main status */
            case "status":
                validateInit();
                validateNumArgs("status", args, 1);
                Repository.statusCommand();
                break;
            /** Handles 3 use cases. */
            case "checkout":
                validateInit();
                /**
                 * java gitlet.Main checkout -- [file name]
                 * java gitlet.Main checkout [commit id] -- [file name]
                 * java gitlet.Main checkout [branch name]
                 * */
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutFile(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                break;
            /** Usage: java gitlet.Main branch [branch name] */
            case "branch":
                validateInit();
                validateNumArgs("branch", args, 2);
                Repository.branchCommand(args[1]);
                break;
            /** Usage: java gitlet.Main rm-branch [branch name] */
            case "rm-branch":
                validateInit();
                validateNumArgs("rm-branch", args, 2);
                Repository.removeBranchCommand(args[1]);
                break;
            /** Usage: java gitlet.Main reset [commit id] */
            case "reset":
                validateInit();
                validateNumArgs("reset", args, 2);
                Repository.resetCommand(args[1]);
                break;
            /** Usage: java gitlet.Main merge [branch name] */
            case "merge":
                validateInit();
                validateNumArgs("merge", args, 2);
                Repository.mergeCommand(args[1]);
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Check the number of arguments versus the expected number,
     * print a message if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     * */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }

    public static void validateInit() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}



