package wang.relish.algorithm.handler;

//优先级队列
public class MessageQueue {


    //队列大小
    private int size;

    //队列头
    private Node head;

    //队列尾
    private Node tail;


    public MessageQueue() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    /**
     * 维持的有序列表节点
     **/
    private static class Node {
        Node pre;
        Node next;
        Message message;

        public Node(Message message) {
            this.message = message;
        }
    }


    private boolean insertToLinkTail(Node node) {

        if (this.head == null) {
            this.head = this.tail = node;
            return true;
        }

        //顺序插入
        Node p = head;
        while (p != null) {
            if (p.message.when > node.message.when) {
                break;
            }
            p = p.next;
        }
        if (p == head) {
            //头部
            p.pre = node;
            node.next = p;
            head = node;
            return true;
        } else if (p == null) {
            //尾部
            tail.next = node;
            node.pre = tail;
            tail = node;
            return true;
        } else {
            //中间
            Node pre = p.pre;
            pre.next = node;
            node.next = p;
            node.pre = pre;
            p.pre = node;
            return true;
        }
    }

    private Node removeFromLinkHead() {

        if (head == null) return null;

        Node p = head;
        Node newHead = head.next;
        if (newHead == null) {
            head = tail = null;
        } else {
            newHead.pre = null;
            head = newHead;
        }
        return p;
    }


    /**
     * 消息api
     **/

    public boolean offer(Message message) {
        synchronized (this) {
            if (this.insertToLinkTail(new Node(message))) {
                this.size++;
                return true;
            }
            return false;
        }
    }

    public Message poll() {
        synchronized (this) {
            Node node = this.removeFromLinkHead();

            if (node == null) {
                return null;
            }
            this.size--;
            return node.message;
        }
    }

    public Message peek() {
        synchronized (this) {
            if (head == null) return null;
            return head.message;
        }
    }

    public int getSize() {
        synchronized (this) {
            return this.size;
        }
    }
}