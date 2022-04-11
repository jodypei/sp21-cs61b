package gitlet;

/** 指示Gitlet错误的一般异常。
 *  对于致命错误，.getmessage()的结果是将要打印的错误消息。
 *  @author P. N. Hilfinger
 */
class GitletException extends RuntimeException {


    /** 一个没有消息的GitletException。 */
    GitletException() {
        super();
    }

    /** 一个GitletException MSG作为它的消息。 */
    GitletException(String msg) {
        super(msg);
    }

}
