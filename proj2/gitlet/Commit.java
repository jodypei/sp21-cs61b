package gitlet;

// TODO: 你需要的任何import

import net.sf.saxon.style.PublicStylesheetFunctionLibrary;

import java.io.Serializable;
import java.io.File;
import java.sql.Blob;
import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import java.util.*; // TODO: You'll likely use 'java.util.Date' in this class


/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Guojian Chen
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** 日志信息 */
    private String message;

    /** 提交日期和时间 */
    private Date timeStamp;

    /** 父提交的SHA1代码 */
    private List<String> parents;

    /** 追踪的文件路径图（键：文件路径；值：SHA1代码） */
    private Map<String, String> tracked;

    /** SHA-1 ID */
    private String id;

    /** 文件 */
    private File file;

    /**
     * 构造函数 --- 构造一个commit
     *
     * @param message: commit message
     * @param parents: parent commits
     * @param trackedFilesMap: 追踪的文件路径图（键：文件路径；值：SHA1代码）
     * */
    public Commit(String message, List<String> parents, Map<String, String> trackedFilesMap) {
        this.timeStamp = new Date();
        this.message = message;
        this.parents = parents;
        this.tracked = trackedFilesMap;
        id = generateID();
        file = getObjectFile(id);
    }

    /** 初始 commit. */
    public Commit() {
        this.message = "initial commit";
        this.timeStamp = new Date(0);
        this.parents = new LinkedList<>();
        this.tracked = new HashMap<>();
        id = generateID();
    }

    /**
     * 从有SHA1 id的文件得到一个commit实例
     *
     * @param id : SHA1 id
     * @return Commit实例
     */
    public static Commit fromFile(String id) {
        return readObject(getObjectFile(id), Commit.class);
    }

    /**
     * 根据时间戳，备注，父提交链表，文件路径图生成一个SHA1 ID
     *
     * @return SHA1 id
     * */
    public String generateID() {
        return sha1(getTimeStamp(), message, parents.toString(), tracked.toString());
    }


    /**
     * 获取 SHA1 id
     *
     * @return SHA1 id
     * */
    public String getID() {
        return id;
    }

    /**
     * 获取 时间戳
     *
     * @return Date实例
     * */
    public Date getDate() {
        return timeStamp;
    }
}
