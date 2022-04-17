package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the Staging Area.
 *
 * @author Guojian Chen
 */
public class Stage implements Serializable {
    /**
     * The added files Map.
     * <filename, blob's Key>
     */
    private HashMap<String, String> filesToAdd;
    /**
     * The removed files.
     * <filename>
     */
    private List<String> filesRemoved;

    /**
     * Construct a Stage Area.
     */
    public Stage() {
        filesToAdd = new HashMap<>();
        filesRemoved = new ArrayList<>();
    }

    /**
     * Add a file with its blobID to the Stage Area.
     * Record the filename.
     *
     * Key: filename
     * Value: BlobID(i.e. SHA1 Key generated from the file)
     */
    public void addFile(String filename, String blobID) {
        filesToAdd.put(filename, blobID);
        filesRemoved.remove(filename);
    }

    /**
     * Check if the Stage Area is Empty.
     */
    public boolean isEmptyStage() {
        return filesToAdd.isEmpty() && filesRemoved.isEmpty();
    }

    /**
     * @return files staged in the Stage Area.
     */
    public HashMap<String, String> getFilesToAdd() {
        return filesToAdd;
    }
    /**
     * @return files removed from the Stage Area.
     */
    public List<String> getFilesRemoved() {
        return filesRemoved;
    }

    /**
     * Clear the Stage Area.
     */
    public void clearStage() {
        filesToAdd.clear();
        filesRemoved.clear();
    }
}
