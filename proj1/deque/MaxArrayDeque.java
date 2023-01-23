package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max() {
        if (super.size() == 0) {
            return null;
        }
        int maxDex = 0;
        for (int i = 0; i < super.size(); i += 1) {
            if (comparator.compare(super.get(maxDex), super.get(i)) < 0) {
                maxDex = i;
            }
        }
        return super.get(maxDex);
    }

    public T max(Comparator<T> c) {
        if (super.size() == 0) {
            return null;
        }
        int maxDex = 0;
        for (int i = 0; i < super.size(); i += 1) {
            if (c.compare(super.get(maxDex), super.get(i)) < 0) {
                maxDex = i;
            }
        }
        return super.get(maxDex);
    }
}


