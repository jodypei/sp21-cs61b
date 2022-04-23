package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Guojian Chen
 */
public class Main {
    public static void main(String[] args) {
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
            case "branch":
                repo.validateNumArgs(args, 2);
                repo.branch(args);
            case "rm-branch":
                repo.validateNumArgs(args, 2);
                repo.rmBranch(args);
            case "checkout":
                String dash = "--";
                switch (args.length) {
                    case 2:
                        // TODO: CHECKOUT WITH BRANCHING
                    case 3:
                        if (!args[1].equals(dash)) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        repo.checkout(args[2]);
                    case 4:
                        if (!args[2].equals(dash)) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        repo.checkout(args[1], args[3]);
                    default:
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                }
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
