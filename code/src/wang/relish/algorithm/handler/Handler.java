package wang.relish.algorithm.handler;

public abstract class Handler {

    private Message mMessage;
    private Looper mLooper;


    public Handler() {
        Looper looper = Looper.looperThreadLocal.get();
        if (looper == null) {
            throw new Error("looper not exist where current thread !");
        }
        this.mLooper = looper;
    }

    public Handler(Looper looper) {
        if (looper == null) {
            throw new NullPointerException("");
        }
        this.mLooper = looper;
    }

    public void post(Runnable runnable) {
        this.mMessage = Message.obtain();
        mMessage.callback = runnable;
        mMessage.when = 0L;
        mMessage.what = null;
        this.sendMessage(mMessage);
    }

    public void sendMessage(Message message) {
        message.sendWhen = System.currentTimeMillis();
        message.target = this;
        this.mLooper.getMessageQueue().offer(message);
    }

    protected abstract void handleMessage(Message message);

    public Looper getLooper() {
        return mLooper;
    }

    public void setLooper(Looper mLooper) {
        this.mLooper = mLooper;
    }
}