package gitlet;

import java.io.File;

/** 一个调试类，它的主程序可以如下调用:
 *      java gitlet.DumpObj FILE...
 *  每个文件都是由Utils.writeObject (或任何包含序列化对象的文件)生成的文件。
 *  这将简单地读取FILE，反序列化它，并对结果对象调用dump方法。
 *  对象必须实现 gitlet.Dumpable 接口。
 *  例如，你可以这样定义你的类:
 *
 *        import java.io.Serializable;
 *        import java.util.TreeMap;
 *        class MyClass implements Serializeable, Dumpable {
 *            ...
 *            @Override
 *            public void dump() {
 *               System.out.printf("size: %d%nmapping: %s%n", _size, _mapping);
 *            }
 *            ...
 *            int _size;
 *            TreeMap<String, String> _mapping = new TreeMap<>();
 *        }
 *
 *  如上所示，你的dump方法应该从你的类对象中打印有用的信息。
 *  @author P. N. Hilfinger
 */
public class DumpObj {

    /** 对files中的每个文件的内容进行反序列化和应用转储。 */
    public static void main(String... files) {
        for (String fileName : files) {
            Dumpable obj = Utils.readObject(new File(fileName),
                                            Dumpable.class);
            obj.dump();
            System.out.println("---");
        }
    }
}

