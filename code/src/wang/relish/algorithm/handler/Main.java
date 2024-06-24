package wang.relish.algorithm.handler;

public class Main {
    public static void main(String[] args) {
        Looper.prepareMain();

        sendMessageInOtherThread();

        //主线程创建完毕，开始接听new Loop消息
        Looper.loop();
    }

    public static void sendMessageInOtherThread() {

        new Thread(() -> {

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    if (message.callback != null) {
                        message.callback.run();
                    }
                }
            };


            Message message = Message.obtain();
            message.callback = () -> System.out.println(Thread.currentThread().getName() + "   handler1  我是消息1---" + System.currentTimeMillis());
            message.when = 1000L;

            Message message2 = Message.obtain();
            message2.callback = () -> System.out.println(Thread.currentThread().getName() + "   handler1  我是消息2---" + System.currentTimeMillis());
            message2.when = 3000L;

            Message message3 = Message.obtain();
            message3.callback = () -> {
                System.out.println(Thread.currentThread().getName() + "   handler1  我是消息3---我要销毁主程序" + System.currentTimeMillis());
                Looper.getMainLooper().quit();
            };
            message3.when = 7000L;

            handler.sendMessage(message);
            handler.sendMessage(message2);
            handler.sendMessage(message3);
            System.out.println(Thread.currentThread().getName() + "   handler1 发送时间---" + System.currentTimeMillis());

        }).start();


        new Thread(() -> {

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    if (message.callback != null) {
                        message.callback.run();
                    }
                }
            };


            Message message = Message.obtain();
            message.callback = () -> System.out.println(Thread.currentThread().getName() + "   handler2  我是消息1---" + System.currentTimeMillis());
            message.when = 4000L;

            Message message2 = Message.obtain();
            message2.callback = () -> System.out.println(Thread.currentThread().getName() + "   handler2  我是消息2---" + System.currentTimeMillis());
            message2.when = 2000L;

            Message message3 = Message.obtain();
            message3.callback = () -> {
                System.out.println(Thread.currentThread().getName() + "   handler2  我是消息3---我要销毁主程序" + System.currentTimeMillis());
                Looper.getMainLooper().quit();
            };
            message3.when = 6000L;

            handler.sendMessage(message);
            handler.sendMessage(message2);
            handler.sendMessage(message3);
            System.out.println(Thread.currentThread().getName() + "   handler2 发送时间---" + System.currentTimeMillis());

        }).start();
    }
}