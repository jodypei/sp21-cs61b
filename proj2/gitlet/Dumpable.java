package gitlet;

import java.io.Serializable;

/** 描述可转储对象的接口。
 *  @author P. N. Hilfinger
 */
interface Dumpable extends Serializable {
    /** 在System.out上打印关于这个对象的有用信息。 */
    void dump();
}
