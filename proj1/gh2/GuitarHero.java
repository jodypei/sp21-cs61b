package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/** 完整版
 *  客户端，使用合成器包仿真弹拨吉他弦的声音
 * @author 陈国检
 */

public class GuitarHero {
    public static final GuitarString[] string = new GuitarString[37];
    public static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static int index;

    public static void init() {
        for (int i = 0; i < 37; i += 1) {
            int factor = (i - 24) / 12;
            string[i] = new GuitarString(440 * Math.pow(2, factor));
        }
    }

    public static void main(String[] args) {
        /* 创建37个琴弦 */
        init();

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (keyboard.indexOf(key) != -1) {
                    index = keyboard.indexOf(key);
                    string[index].pluck();
                }
            }

            /* 计算样本的叠加 */
            double sample = 0;
            for (int i = 0; i < 37; i += 1) {
                sample += string[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < 37; i += 1) {
                string[i].tic();
            }
        }
    }
}
