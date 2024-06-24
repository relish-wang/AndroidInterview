package wang.relish.algorithm.handler;

import java.util.ArrayList;
import java.util.List;

//循环读取者
public class Looper {

    public static ThreadLocal<Looper> looperThreadLocal;
    private static Looper mMainLooper;

    static {
        looperThreadLocal = new ThreadLocal<>();
    }

    private Message mMessage;
    private MessageQueue mMessageQueue = new MessageQueue();
    private volatile boolean quit = false;

    private Looper() {
    }

    public static Looper getLooper() {
        return looperThreadLocal.get();
    }

    public static Looper getMainLooper() {
        return mMainLooper;
    }

    public static void prepare() {
        if (looperThreadLocal.get() != null) {
            throw new Error("current thread already has prepared!");
        }

        looperThreadLocal.set(new Looper());
    }

    public static void prepareMain() {
        prepare();
        mMainLooper = getLooper();
    }

    public static void loop() {
        //do something
        Looper looper = looperThreadLocal.get();
        if (looper == null) {
            throw new Error("please use prepare() to create looper!");
        }

        while (true) {

            looper.mMessage = looper.mMessageQueue.peek();
            if (looper.mMessage == null /*|| !looper.mMessage.doHandleMessage()*/) {
                continue;
            }

            //开始处理消息
            looper.mMessage = looper.mMessageQueue.poll();
            looper.mMessage.target.handleMessage(looper.mMessage);
            //处理完消息后，放入缓存池
//            Message.addCache(looper.mMessage);
            looper.mMessage = null;
            if (looper.quit) return;
        }
    }

    public void quit() {
        this.quit = true;
    }

    public MessageQueue getMessageQueue() {
        return mMessageQueue;
    }
}