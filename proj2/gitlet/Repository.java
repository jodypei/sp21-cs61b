package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Guojian Chen
 */
public class Repository {
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
                System.out.println("A Gitlet version-control system"
                        + " already exists in the current directory.");
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
        writeContents(getBranchHeadFile("master"),
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
            if (theStage.getFilesRemoved().contains(args[1])) {
                theStage.getFilesRemoved().remove(args[1]);
                writeObject(STAGE_FILE, theStage);
                System.exit(0);
            }
            System.out.println("File does not exist.");
            System.exit(0);
        }

        /* Construct a Blob */
        Blob blob = new Blob(args[1], CWD);
        String blobId = blob.getId();

        /* Check if the current working version of the file
            is identical to the version in the current commit */
        String trackedBlobId = theHead.getTracked().get(args[1]);
        if (trackedBlobId != null) {
            if (trackedBlobId.equals(blobId)) {
                theStage.getFilesToAdd().remove(args[1]);
                if (theStage.getFilesToAdd() != null) {
                    writeObject(STAGE_FILE, theStage);
                    System.exit(0);
                } else if (theStage.getFilesRemoved().remove(args[1])) {
                    writeObject(STAGE_FILE, theStage);
                    System.exit(0);
                }
            }
        }

        /* if the file was staged once, prevBlobId = blobId, overwrite */
        String prevBlobId = theStage.getFilesToAdd().put(args[1], blobId);
        if (prevBlobId != null && prevBlobId.equals(blobId)) {
            System.exit(0);
        }

        /* Create Blob Object if not exists */
        if (!join(BLOBS_DIR, blob.getFilename()).exists()) {
            File temp = join(BLOBS_DIR, blobId);
            writeObject(temp, blobId);
        }
        writeObject(STAGE_FILE, theStage);
    }

    /**
     * COMMIT.
     *
     * @param msg
     */
    public void commit(String msg) {
        validateRepo();
        getTheHead();
        readTheStage();
        /* Check if the Stage is Empty */
        if (theStage.isEmptyStage()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit tempCmt = theHead;
        /* Put all files staged to add into the HashTable of theHead*/
        tempCmt.getTracked().putAll(theStage.getFilesToAdd());
        /* Remove the files staged to add in Stage Area */
        for (String filename : theStage.getFilesRemoved()) {
            tempCmt.getTracked().remove(filename);
        }
        /* Clear and update the Stage File */
        theStage.clearStage();
        writeObject(STAGE_FILE, theStage);

        List<String> parents = new ArrayList<>();
        parents.add(0, theHead.getThisKey());

        /* Create a Commit Object in Directory */
        Commit newCommit = new Commit(msg, parents, tempCmt.getTracked());
        File temp = join(COMMITS_DIR, newCommit.getThisKey());
        writeObject(temp, newCommit);

        /* set branch head */
        File branchHeadFile = getBranchHeadFile(getCurBranchName());
        writeContents(branchHeadFile, newCommit.getThisKey());
    }

    /**
     * rm a file from the Stage Area.
     *
     * @param args args[0]: 'rm'
     *             args[1]: filename
     */
    public void rm(String[] args) {
        validateRepo();
        getTheHead();
        readTheStage();

        /* remove the file in Stage Area */
        if (theStage.getFilesToAdd().remove(args[1]) != null) {
            writeObject(STAGE_FILE, theStage);
            System.exit(0);
        }
        /* else remove the file in CWD */
        if (theHead.getTracked().get(args[1]) != null) {
            File fileToDelete = join(CWD, args[1]);
            if (fileToDelete.exists()) {
                restrictedDelete(fileToDelete);
            }
            if (theStage.getFilesRemoved().add(args[1])) {
                writeObject(STAGE_FILE, theStage);
                System.exit(0);
            }
        }
        System.out.println("No reason to remove the file.");
    }

    /**
     * Print the HISTORY of current head commit.
     */
    public void log() {
        validateRepo();
        getTheHead();
        readTheStage();

        Commit curCmt = theHead;
        while (true) {
            System.out.println("===");
            System.out.println("commit " + curCmt.getThisKey());
            System.out.println("Date: " + curCmt.getDate());
            System.out.println(curCmt.getMessage() + "\n");

            List<String> parentKeys = curCmt.getParentKeys();
            /* Exit. Break when curCmt is initialCommit */
            if (parentKeys == null) {
                break;
            }

            curCmt = castIdToCommit(parentKeys.get(0));
        }
        System.exit(0);
    }

    /**
     * Print the status.
     */
    public void status() {
        validateRepo();
        getTheHead();
        readTheStage();
        StringBuffer statusBuilder = new StringBuffer();

        /* Branches */
        statusBuilder.append("=== Branches ===").append("\n");
        statusBuilder.append("*" + getCurBranchName() + "\n");
        /* get the filenames(except the current Branch File) in ref/heads Directory  */
        String[] branchNames =
                BRANCH_HEADS_DIR.list((dir, name) -> !name.equals(getCurBranchName()));
        
        Arrays.sort(branchNames);
        for (String branchName : branchNames) {
            statusBuilder.append(branchName).append("\n");
        }
        statusBuilder.append("\n");
        /* Branches End HERE */

        Map<String, String> addedFilesMap = theStage.getFilesToAdd();
        List<String> removedFileName = theStage.getFilesRemoved();

        /* Staged Files */
        statusBuilder.append("=== Staged Files ===").append("\n");
        appendFileNamesInOrder(statusBuilder, addedFilesMap.keySet());
        statusBuilder.append("\n");
        /* Staged Files End HERE */

        /* Removed Files */
        statusBuilder.append("=== Removed Files ===").append("\n");
        appendFileNamesInOrder(statusBuilder, removedFileName);
        statusBuilder.append("\n");
        /* Removed Files End HERE */

        /* Modifications Not Staged For Commit */
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");
        statusBuilder.append("\n");
        /* Modifications Not Staged For Commit End HERE */

        /* Untracked Files */
        statusBuilder.append("=== Untracked Files ===").append("\n");
        statusBuilder.append("\n");
        /* Untracked Files End HERE */

        System.out.print(statusBuilder);
        System.exit(0);
    }

    /**
     *  get theHead :  get the head Commit of a branch.
     */
    private void getTheHead() {
        if (theHead != null) {
            return;
        }
        /* Get the Head Commit of a branch */
        String content = Utils.readContentsAsString(HEAD).strip();
        if (!content.startsWith("ref: ")) {
            System.out.println("corrupted internal structure");
            System.exit(0);
        }

        /* get the current Branch Name */
        String branchName = getCurBranchName();

        /* find the Branch File in 'refs/heads' Directory */
        File branchHeadFile = getBranchHeadFile(branchName);
        assert (branchHeadFile.exists());

        /* read the content(SHA1 key) in branchFile */
        String sha1Key = readContentsAsString(branchHeadFile).strip();

        if (isKey(sha1Key)) {
            /* cast the SHA1 Key to Commit (deserialize) */
            theHead = castIdToCommit(sha1Key);
        }
    }

    /**
     * Read from the Stage Area(File) if theStage is null, else not change.
     */
    private void readTheStage() {
        if (theStage == null) {
            theStage = readObject(STAGE_FILE, Stage.class);
        }
    }

    /**
     * check wither given content is a sha1 key.
     * @param content .
     * @return
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
        return Utils.readObject(file, Commit.class);
    }

    /**
     * Get current branch name.
     * @return
     */
    private String getCurBranchName() {
        return readContentsAsString(HEAD).substring(16);
    }

    /**
     * Get branch head ref file in refs/heads folder.
     *
     * @param branchName  Name of the branch
     * @return File instance
     */
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    /**
     * Append lines of file name in order from files paths Set to StringBuilder.
     *
     * @param stringBuffer       StringBuilder instance
     * @param filePathsCollection Collection of file paths
     */
    private static void appendFileNamesInOrder(StringBuffer stringBuffer,
                                               Collection<String> filePathsCollection) {
        List<String> filePathsList = new ArrayList<>(filePathsCollection);
        appendFileNamesInOrder(stringBuffer, filePathsList);
    }

    /**
     * Append lines of file name in order from files paths Set to StringBuilder.
     *
     * @param stringBuffer StringBuilder instance
     * @param filePathsList List of file paths
     */
    private static void appendFileNamesInOrder(StringBuffer stringBuffer,
                                               List<String> filePathsList) {
        filePathsList.sort(String::compareTo);
        for (String filePath : filePathsList) {
            String fileName = Paths.get(filePath).getFileName().toString();
            stringBuffer.append(fileName).append("\n");
        }
    }

    /**
     * Check whether the Number of input arguments meets the requirement.
     *
     * @param args Command line argument list
     * @param n    The expected number of parameters
     */
    public void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Validate the repo.
     */
    private void validateRepo() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
