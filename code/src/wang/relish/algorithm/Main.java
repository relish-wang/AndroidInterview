package wang.relish.algorithm;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String[] s1 = new String[]{"Hi", "Hi", "Hi"};
        String[] s2 = new String[s1.length];
        System.arraycopy(s1, 0, s2, 0, s1.length);
        s1[2] = "Hier";
        System.out.println(Arrays.toString(s2));// output: [Hi,Hi,Hier]
        // 他期待的输出:[Hi,Hi,Hier]
        // 实际输出: [Hi,Hi,Hi]
        System.out.println(Arrays.toString(s1));
    }
}
