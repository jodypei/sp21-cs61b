package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.*;

import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *
 *  <Param>    <Data Type>  <Function>
 *  tracked     HashMap      tracking files by SHA1 key(i.e. BlobID)
 *  timeStamp   Date         record the Commit time
 *  message     String       record the log message
 *  parentKey   String       the SHA1 key of the first parent Commit
 *  parentKey2  String       the SHA1 key of the second parent Commit
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
    /** Parent Commit hash key. */
    private String parentKey;
    /** Parent Commit hash key 2, used only in Merge */
    private String parentKey2;
    /** SHA1 id */
    private String id;

    /**
     * Construct The Initial Commit.
     */
    public Commit() {
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.id = sha1(message, timeStamp.toString());
        this.tracked = new HashMap<>();
        this.parentKey = null;
    }

    /**
     * Construct a Commit.
     * @param cmtMessage        commit message.
     * @param parentA           the First parent commit.
     * @param parentB           the Second parent commit.
     * @param trackedFilesMap   Map of tracked files.
     */
    public Commit(String cmtMessage, String parentA, String parentB, Map<String, String> trackedFilesMap) {
        this.message = cmtMessage;
        this.timeStamp = new Date();
        this.tracked = trackedFilesMap;
        this.parentKey = parentA;
        this.parentKey2 = parentB;
        /* Generate SHA1 Key */
        if (parentKey2 == null) {
            this.id = sha1(message, timeStamp.toString(), parentKey, tracked.toString());
        } else {
            this.id = sha1(message, timeStamp.toString(), parentKey, parentKey2, tracked.toString());
        }
    }

    /**
     * Generate hash key(i.e. SHA1 id) of this commit.
     * NOTHING is expected to change after this call.
     * @return SHA1 id
     */
    public String getThisKey() {
        return Utils.sha1(Utils.serialize(this));
    }
    /**
     * get the Date(commit time) of this commit.
     */
    public Date getDate() {
        return this.timeStamp;
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
        return parentKey;
    }
    /**
     * return the SECOND parent SHA1 key.
     */
    public String getParentKey2() {
        return parentKey2;
    }
}
