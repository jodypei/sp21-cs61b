package gitlet;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *
 *  <Param>    <Data Type>  <Function>
 *  tracked     HashMap      tracking files by SHA1 key(i.e. BlobID)
 *  timeStamp   Date         record the Commit time
 *  message     String       record the log message
 *  parentKeys  List         the SHA1 key of the parent Commits
 *  id          String       the SHA1 key of this Commit
 *
 *  @author Guojian Chen
 */
public class Commit implements Serializable {
    /** Commit Message */
    private String message;
    /** Commit Time. */
    private Date timeStamp;
    /** Files being tracked (key: file path; value: SHA1 key) */
    private Map<String, String> tracked;
    /** Parent Commit hash keys. Hash Key2 used only in Merge*/
    private List<String> parentKeys;
    /** SHA1 id */
    private String id;

    /**
     * Construct The Initial Commit.
     */
    public Commit() {
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.tracked = new HashMap<>();
        this.id = sha1(message, timeStamp.toString());
    }

    /**
     * Construct a Commit.
     * @param cmtMessage        commit message.
     * @param parents           the First and the Second parent commit.
     * @param trackedFilesMap   Map of tracked files.
     */
    public Commit(String cmtMessage, List<String> parents,
                  Map<String, String> trackedFilesMap) {
        this.message = cmtMessage;
        this.timeStamp = new Date();
        this.tracked = trackedFilesMap;
        this.parentKeys = parents;
        /* Generate SHA1 Key */
        this.id = sha1(message, timeStamp.toString(), parentKeys.toString(), tracked.toString());
    }

    /**
     * Generate hash key(i.e. SHA1 id) of this commit.
     * NOTHING is expected to change after this call.
     * @return SHA1 id
     */
    public String getThisKey() {
        return this.id;
    }
    /**
     * get the Date(commit time) of this commit.
     */
    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(timeStamp);
    }
    /**
     * get the message of this commit.
     */
    public String getMessage() {
        return this.message;
    }
    /**
     * get the Blobs(tracked files) contained in this commit.
     */
    public Map<String, String> getTracked() {
        return this.tracked;
    }
    /**
     * return the FIRST parent SHA1 key.
     */
    public String getParentKey() {
        return parentKeys.get(0);
    }
    /**
     * return the SECOND parent SHA1 key.
     */
    public String getParentKey2() {
        return parentKeys.get(1);
    }
    /**
     * return Parents
     */
    public List<String> getParentKeys() {
        return parentKeys;
    }
}
