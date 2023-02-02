package gitlet;

import java.io.IOException;
import static gitlet.Utils.*;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Xiaoli Li
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // If args is empty
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            /** Handle `init` command */
            case "init":
                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;
            /** Handle `add [filename]` command */
            case "add":
                validateNumArgs("add", args, 2);
                Repository.addCommand(args[1]);
                break;
            /** Handle `commit [message]` command */
            case "commit":
                if (args.length == 1) {
                    message("Please enter a commit message.");
                    break;
                }
                validateNumArgs("commit", args, 2);
                Repository.commitCommand(args[1]);
                break;
            /** Handle `log` command */
            case "log":
                validateNumArgs("log", args, 1);
                Repository.logCommand();
                break;
            /** Handle `checkout -- [file name]` */
            case "checkout":
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
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }
}


