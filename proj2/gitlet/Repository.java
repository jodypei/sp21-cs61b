package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.nio.file.Path;
import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Guojian Chen
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The Stage Area */
    private static final File STAGE_FILE = join(GITLET_DIR, "index");
    /**
     * The Objects directory, stores committed blobs & commits
     */
    private static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    private static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**
     * The Branches directory
     * @param REFS_DIR
     * @param BRANCH_HEADS_DIR
     * @param REMOTE_DIR
     */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");
    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");
    /**
     * The HEAD Object, stores current branch's name if it points to tip
     */
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    /** the head commit. */
    private Commit theHead;
    /** the stage. */
    private Stage theStage;
    /** SHA1 length. (1 character ~ 4 bits) */
    private static final int SHA1_LENGTH = 40;
    /** HEAD ref prefix */
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs/heads/";
    /**
     * INITIALIZE a Repo at Current Working Directory(CWD).
     *
     * .gitlet
     *  > HEAD      -> File
     *  > blobs     -> Content
     *  > commits   -> Content
     *  > index     -> File
     *  > refs      -> Content
     *     >> heads -> [master][branch name]
     */
    public void init() {
        /* Failure Case */
        if (GITLET_DIR.exists()) {
            if (GITLET_DIR.isDirectory()) {
                System.out.println("A Gitlet version-control system" +
                        " already exists in the current directory.");
                System.exit(0);
            } else {
                GITLET_DIR.delete();
            }
        }
        /* Create Repo Skeleton */
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCH_HEADS_DIR.mkdir();
        /* Create then Save(i.e. Persistence) Stage Area */
        theStage = new Stage();
        Utils.writeObject(STAGE_FILE, theStage);
        /* Initial Commit */
        Commit initialCommit = new Commit();
        File initCmt = join(COMMITS_DIR, initialCommit.getThisKey());
        writeObject(initCmt, initialCommit);
        /* Create Branch: master */
        writeContents(join(BRANCH_HEADS_DIR, "master"),
                initialCommit.getThisKey() + '\n');
        /* Create HEAD */
        writeContents(HEAD, HEAD_BRANCH_REF_PREFIX + "master");
    }

    /**
     * ADD a copy of the currently existing file to the Stage Area.
     *
     * @param args args[0]: 'add'
     *             args[1]: filename
     */
    public void add(String[] args) {
        validateRepo();
        getTheHead();
        readTheStage();
        /* Failure Case */
        File file = join(CWD, args[1]);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        /* Construct a Blob */
        Blob blob = new Blob(args[1], CWD);
        String blobId = blob.getId();

        /* get the SHA1 key of the head  */
        String headCommitId = theHead.getTracked().get(args[1]);

        if (headCommitId != null) {
            if (headCommitId.equals(blobId)) {
                theStage.getFilesToAdd().remove(args[1]);
                theStage.getFilesRemoved().remove(args[1]);
            }
        }
        /* if the file was staged once, prevBlobId != blobId */
        String prevBlobId = theStage.getFilesToAdd().put(args[1], blobId);
        if (prevBlobId != null && prevBlobId.equals(blobId)) {
            writeObject(STAGE_FILE, theStage);
        }

        if (!join(BLOBS_DIR, blob.getFilename()).exists()) {
            File temp = join(BLOBS_DIR, blobId);
            writeObject(temp, blobId);
        }
    }

    /**s
     * COMMIT.
     *
     * @param msg
     */
    public void commit(String msg) {
        validateRepo();
        getTheHead();
        readTheStage();
        if (theStage.isEmptyStage()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        /* Put all files staged to add into the HashTable of theHead*/
        theHead.getTracked().putAll(theStage.getFilesToAdd());
        /* Remove the files staged to add in Stage Area */
        for (String filename : theStage.getFilesRemoved()) {
            theHead.getTracked().remove(filename);
        }

        theStage.clearStage();
        writeObject(STAGE_FILE, theStage);

        Commit newCommit = new Commit(msg, theHead, theStage);
        File temp = join(COMMITS_DIR,newCommit.getThisKey());
        writeObject(temp, newCommit.getThisKey());
    }

    /**
     * Check whether the Number of input arguments meets the requirement.
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
    /**
     * Validate the repo subdir internal structure.
     * Init theHead.
     * Init theStage.
     */
    private void validateRepo() {
        /* Validate the repo */
        if (!GITLET_DIR.isDirectory()) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
    }
    /**
     *  get theHead :  get the head Commit of a branch.
     */
    private void getTheHead() {
        if (theHead != null) {
            return;
        }
        /* Get the head Commit of a branch */
        String content = Utils.readContentsAsString(HEAD).strip();
        if (!content.startsWith("ref: ")) {
            throw Utils.error("corrupted internal structure");
        }
        /* remove the first 5 character --- "ref: " */
        Path relativePath = Paths.get(content.strip().substring(5));
        assert (relativePath.startsWith("refs"));
        assert (!relativePath.isAbsolute());

        /* get the Branch Name */
        String branchName = readContentsAsString(HEAD).substring(16);

        /* find the Branch File in 'ref/heads' Directory */
        File branchHeadFile = join(BRANCH_HEADS_DIR, branchName);
        assert (branchHeadFile.exists());

        /* read the content(SHA1 key) in branchFile */
        String sha1Key = Utils.readContentsAsString(branchHeadFile).strip();

        /* cast the SHA1 Key to Commit (deserialize) */
        theHead = castIdToCommit(sha1Key);
    }
    /**
     * read theStage :  read from the Stage Area.
     */
    private void readTheStage() {
        /* Read from the Stage Area(File) if theStage is null, else not change */
        if (theStage == null) {
            theStage = Utils.readObject(STAGE_FILE, Stage.class);
        }
    }
    /**
     * Check whether the given content is a valid SHA1 key.
     *
     * @param content a 160-bit integer hash generated from ANY Byte Sequence
     */
    private boolean isKey(String content) {
        return content.length() == SHA1_LENGTH;
    }

    /**
     * Convert the given SHA1 Key to Commit,
     * if corresponding commit file exists.
     *
     * @param commitId SHA1 Key
     */
    private Commit castIdToCommit(String commitId) {
        File file = join(COMMITS_DIR, commitId);
        if (commitId.equals("null") || !file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
    }
}
