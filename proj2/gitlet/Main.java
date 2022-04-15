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
                // TODO: handle the `init` command
                repo.validateNumArgs(args, 1);
                repo.init();
                break;
            case "add":
                repo.validateNumArgs(args, 2);
                repo.add(args);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":
                repo.validateNumArgs(args, 2);
                repo.commit(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
