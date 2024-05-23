package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int front;
    private int rear;

    private static final int INITIAL_CAPABILITY = 8;
    private static final double USAGE_FACTOR = 0.25;

    public ArrayDeque() {
        items = (T[]) new Object[INITIAL_CAPABILITY];
        size = 0;
        front = 0;
        rear = 1;
    }

    private int minusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int currentIndex = plusOne(front);
        for (int i = 0; i < size; i++) {
            newItems[i] = items[currentIndex];
            currentIndex = plusOne(currentIndex);
        }
        items = newItems;
        front = capacity - 1;
        rear = size;
    }

    //1. add
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[front] = item;
        front = minusOne(front);
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[rear] = item;
        rear = plusOne(rear);
        size++;
    }

    //2. delete
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        front = plusOne(front);
        T item = items[front];
        items[front] = null;
        size--;
        if (items.length >= 16 && size < items.length * USAGE_FACTOR) {
            resize(items.length / 2);
        }
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        rear = minusOne(rear);
        T item = items[rear];
        items[rear] = null;
        size--;
        if (items.length >= 16 && size < items.length * USAGE_FACTOR) {
            resize(items.length / 2);
        }
        return item;
    }

    //3. search
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(plusOne(front) + index) % items.length];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    //4. special
    public void printDeque() {
        int currentIndex = plusOne(front);
        for (int i = 0; i < size; i++) {
            System.out.print(items[currentIndex] + " ");
            currentIndex = plusOne(currentIndex);
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayDeque<?> that = (ArrayDeque<?>) o;

        if (this.size != that.size) return false;

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(that.get(i))) {
                return false;
            }
        }

        return true;
    }

}
