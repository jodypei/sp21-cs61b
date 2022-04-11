package gitlet;

/** Gitlet的驱动类，它是Git版本控制系统的一个子集。
 *  @author Guojian Chen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.error("Please enter a command.", args);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
        System.out.println("No command with that name exists.");
        System.exit(0);
    }
}
