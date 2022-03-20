package gh2;

import deque.ArrayDeque;
import deque.Deque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** 常数. 不要修改. 防止你好奇, 最后的关键词意思是它们在运行时不能被修改
     *  后面的lecture将会讨论这些 */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* 存储声音数据的缓存 */
    private Deque<Double> buffer;

    /* 在给定频率下创建一个吉他字符串  */
    public GuitarString(double frequency) {
        long temp = Math.round(SR / frequency);
        int capacity = (int) temp;
        double zero = 0;
        buffer = new ArrayDeque<>();
        for (int i = 0; i < capacity; i += 1) {
            buffer.addLast(zero);
        }
    }


    /* 用白噪音替换缓存来弹出吉他字符串 */
    public void pluck() {
        int capacity = buffer.size();
        while (buffer.size() > 0) {
            double last = buffer.removeLast();
        }
        for (int i = 0; i < capacity; i += 1) {
            double r = Math.random() - 0.5;
            buffer.addFirst(r);
        }
    }

    /* 通过对Karplus-Strong算法进行一次迭代，一步一步推进仿真 */
    public void tic() {
        double second = buffer.get(1);
        System.out.println("Second: " + second);
        double front = buffer.removeFirst();
        double newDouble = DECAY * 0.5 * (front + second);

        System.out.println("newDouble: " + newDouble);
        buffer.addLast(newDouble);
    }

    /* 返回缓存队列头部的double数字 */
    public double sample() {
        System.out.println("front: " + buffer.get(0));
        return buffer.get(0);
    }
}
