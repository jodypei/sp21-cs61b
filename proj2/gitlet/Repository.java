package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Guojian Chen
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The Stage Object
     */
    private static final File STAGE_FILE = join(GITLET_DIR, "stage");
    /**
     * The Objects directory, stores committed blobs & commits
     * */
    private static final File COMMITS_DIR= join(GITLET_DIR, "commits");
    private static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**
     * The Branches directory
     * @param REFS_DIR
     * @param BRANCH_HEADS_DIR
     * @param REMOTE_DIR
     * */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");
    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");
    /**
     * The HEAD Object, stores current branch's name if it points to tip
     */
    private static final File HEAD = join(GITLET_DIR, "HEAD");
    /** the stage. */
    private Stage theStage;

    /* TODO: fill in the rest of this class. */

    public void init() {
        /* Failure Case */
        if (GITLET_DIR.exists()) {
            if (GITLET_DIR.isDirectory()) {
                throw Utils.error("A Gitlet version-control system already exists in the current directory");
            } else {
                GITLET_DIR.delete();
            }
        }
        /* Create Repo Skeleton */
        GITLET_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        REFS_DIR.mkdirs();
        BRANCH_HEADS_DIR.mkdirs();
        /* Create then Save(i.e. Persistence) Stage Area */
        theStage = new Stage();
        Utils.writeObject(STAGE_FILE, theStage);
        /* Initial Commit */
        Commit initialCommit = new Commit("initial commit", null);
        /* Create Branch: master */
        writeContents(join(BRANCH_HEADS_DIR, "master"), initialCommit.getThisKey());
        /* Create HEAD */
        writeContents(HEAD, BRANCH_HEADS_DIR + "master");
    }

    /**
     * set branch to a commit.
     * @param branchFile the file of the branch.
     * @param commit     the target commit.
     */
    private void setBranch(File branchFile, Commit commit) {
        Utils.writeContents(branchFile, commit.getThisKey() + '\n');
    }

    /**
     * Check whether the number of input parameters meets the requirements.
     *
     * @param args Command line argument list
     * @param n    The expected number of parameters
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
