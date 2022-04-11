package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/** 各式各样的实用工具。
 * 好好阅读这个文件，因为它提供了几个有用的实用函数，以节省您的一些时间。
 *
 * SHA-1 哈希值.
 * FILE 删除
 * 读写文件内容
 * 目录
 * 其他FILE工具
 * 序列化工具
 * 消息和错误报告
 *  @author P. N. Hilfinger
 */
class Utils {

    /** 完整的SHA-1 UID长度为16进制数。 */
    static final int UID_LENGTH = 40;

    /* SHA-1 哈希值. */

    /** 返回VALS的SHA-1哈希值，可以是字节数组和字符串的任意混合。 */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** 返回VALS中字符串连接的SHA-1哈希值。  */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE 删除 */

    /** 如果FILE存在且不是目录，则删除它。
     *  如果FILE被删除则返回true，否则返回false。
     *  除非FILE指定的目录也包含一个名为.gitlet的目录，
     *  否则拒绝删除FILE并抛出IllegalArgumentException，
     *  */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** 删除名为file的文件(如果它存在且不是一个目录)。
     *  如果FILE被删除则返回true，否则返回false。
     *  除非FILE指定的目录也包含一个名为.gitlet的目录，
     *  否则拒绝删除FILE并抛出IllegalArgumentException，
     *  */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* 读写文件内容 */

    /** 以Byte Array的形式返回FILE的全部内容。
     *  FILE必须为普通文件。
     *  在出现问题时抛出IllegalArgumentException。
     *  */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 以String的形式返回FILE的全部内容。
     *  FILE必须为普通文件。
     *  在出现问题时抛出IllegalArgumentException。
     *  */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** 将CONTENTS中的字节串接的结果写入FILE，根据需要创建或覆盖它。
     *  CONTENTS中的每个对象可以是一个String或Byte Array。
     *  在出现问题时抛出IllegalArgumentException。
     *  */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 返回从FILE读取的T类型对象，将其转换为EXPECTEDCLASS。
     *  在出现问题时抛出IllegalArgumentException。
     *  */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 将OBJ写入FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* 目录 */

    /** 过滤掉除了普通文件之外的所有文件。 */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** 返回目录DIR中所有普通文件的名称的列表，按Java字符串的字典顺序。
     *  如果DIR不表示目录，则返回null。   */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** 返回目录DIR中所有普通文件的名称的列表，按Java字符串的字典顺序。
     *  如果DIR不表示目录，则返回null。  */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* 其他FILE工具 */

    /** 将FIRST和OTHERS的串联返回到一个文件指示符中，
     *  类似于{@link java.nio.file.Paths。 # get (String, String[])方法。
     *  */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** 将FIRST和OTHERS的串联返回到一个文件指示符中，
     *  类似于{@link java.nio.file.Paths。 # get (String, String[])方法。
     *  */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* 序列化工具 */

    /** 返回一个字节数组，其中包含OBJ的序列化内容 */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* 消息和错误报告 */

    /** 返回一个GitletException，它的消息由MSG和ARGS组成，就像String.format方法一样。  */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** 打印由MSG和ARGS组成的消息作为String.format方法，后跟换行符。*/
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }
}
