package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Pez
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // handle the `init` command
                if (!validateNumArgs(args, 1)) {
                    System.exit(0);
                }
                Repository.init();
                break;
            case "add":
                // handle the `add [filename]` command
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            // FILL THE REST IN
            case "commit":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                } else if (args[1].isEmpty()) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "log":
                if (!validatedInitialized() || !validateNumArgs(args, 1)) {
                    System.exit(0);
                }
                Repository.log();
                break;
            case "checkout":
                if (!validatedInitialized()) {
                    System.exit(0);
                }
                if (args.length == 2) {
                    // case checkout [branch name]
                    Repository.checkoutBranch(args[1]);

                } else if (args.length == 3) {
                    // case checkout -- [file name]
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4) {
                    // case checkout [commit id] -- [file name]
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkoutSpecifiedFile(args[1], args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            // PARSE 1 done !
            case "rm":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.remove(args[1]);
                break;
            case "global-log":
                if (!validatedInitialized() || !validateNumArgs(args, 1)) {
                    System.exit(0);
                }
                Repository.logGlobal();
                break;
            case "find":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            case "status":
                if (!validatedInitialized() || !validateNumArgs(args, 1)) {
                    System.exit(0);
                }
                Repository.status();
                break;
            case "branch":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.branchRemove(args[1]);
                break;
            case "reset":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                if (!validatedInitialized() || !validateNumArgs(args, 2)) {
                    System.exit(0);
                }
                Repository.merge(args[1]);
                break;
            // PARSE 2 done
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);

        }
    }


    private static boolean validatedInitialized() {
        /** Returns true if the current directory is an initialized Gitlet directory.
         * */
        if (!Utils.join(".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory");
            return false;
        }
        return true;
    }

    private static boolean validateNumArgs(String[] args, int excepted) {
        /** Validates the number of arguments for a command.
         * */
        if (args.length != excepted) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }
}
