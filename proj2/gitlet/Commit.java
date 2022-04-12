package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.*;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** Commit Message */
    private String message;
    /** Commit Time. */
    private Date timeStamp;
    /** Files being tracked (key: file path; value: SHA1 key) */
    private Map<String, String> tracked;
    /** Parent Commit hash key. */
    private String parentKey;
    /** Parent Commit hash key 2. */
    private String parentKey2;

    /* TODO: fill in the rest of this class. */
    /**
     * Construct a commit.
     * @param cmtMessage  the commit message.
     * @param parent      the parent commit.
     */
    public Commit(String cmtMessage, Commit parent) {
        this.message = cmtMessage;
        this.tracked = new HashMap<>();
        this.timeStamp = new Date(0);
        if (parent != null) {
            this.parentKey = parent.getThisKey();
            /* Copy Parent's HashMap to THIS Commit */
            for (String filePath : parent.tracked.keySet()) {
                tracked.put(filePath, parent.tracked.get(filePath));
            }
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
}
