package cn.turbosu.wordworld;

/**
 * Created by TurboSu on 16/11/30.
 */
public class WordList {
    static int num=8;
    static int now=0;

    static Word[] list={
            new Word("dog","狗 n."),
            new Word("cat","猫 n."),
            new Word("bird","鸟 n."),
            new Word("bear","熊 n."),
            new Word("eat","吃 n."),
            new Word("cry","哭 n."),
            new Word("again","再次 adv."),
            new Word("swim","游泳 v."),
    };
    static Word getNext(){
        int p=now;
        now=(now+1)%num;
        return list[p];
    }
}
