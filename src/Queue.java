public class Queue<E> {
    private Node<E> front;
    private Node<E> rear;

    private static class Node<E> {
        private E element;
        private Node<E> next;
    }

    public Queue() {
        front = null;
        rear  = null;
    }

    public E dequeue() {
        E element = front.element;
        front = front.next;
        if (isEmpty()){
            rear = null;
        }
        return element;
    }

    public void enqueue(E element) {
        Node<E> temp = rear;
        rear = new Node<>();
        rear.element = element;
        rear.next = null;
        if (isEmpty()) front = rear;
        else {
            temp.next = rear;
        }
    }

    public boolean isEmpty() {
        return front == null;
    }
}
