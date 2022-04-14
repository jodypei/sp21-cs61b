package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;

/**
 * Represent a file object.
 * BLOBs represents different versions of A single file.
 *
 * @author Guojian Chen
 */
public class Blob implements Serializable {
    /** the filename */
    private String filename;
    /** content of the file */
    private byte[] content;
    /** Blob id */
    private String id;

    /**
     * Construct a Blob in CWD.
     *
     * Blob.content = file.content(null if the file doesn't exist)
     */
    public Blob(String filename, File curDirectory) {
        this.filename = filename;
        File file = join(curDirectory, filename);

        if (file.exists()) {
            this.content = readContents(file);
            this.id = sha1(filename, content);
        } else {
            this.content = null;
            this.id = sha1(filename);
        }
    }

    /**
     * check if the file is not null.
     */
    public boolean exists() {
        return this.content != null;
    }

    /**
     * get the file name.
     */
    public String getFilename() {
        return filename;
    }
    /**
     * return the content of the file in the format of byte array.
     */
    public byte[] getContent() {
        return content;
    }
    /**
     * return the content of the file in the format of String.
     */
    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }
    /**
     * return the BlobID.
     */
    public String getId() {
        return id;
    }
}