package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Guojian Chen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                repo.validateNumArgs(args, 1);
                repo.init();
                break;
            case "add":
                repo.validateNumArgs(args, 2);
                repo.add(args);
                break;
            case "commit":
                repo.validateNumArgs(args, 2);
                String message = args[1];
                if (message.length() == 0) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                repo.commit(message);
                break;
            case "rm":
                repo.validateNumArgs(args, 2);
                repo.rm(args);
                break;
            case "log":
                repo.validateNumArgs(args, 1);
                repo.log();
            case "status":
                repo.validateNumArgs(args, 1);
                repo.status();
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
