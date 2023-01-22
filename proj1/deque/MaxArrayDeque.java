package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    private Comparator<Item> comparator;
    public MaxArrayDeque(Comparator<Item> c) {
        comparator = c;
    }

    public Item max() {
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

    public Item max(Comparator<Item> c) {
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof MaxArrayDeque) {
            MaxArrayDeque<?> anotherMad = (MaxArrayDeque<?>) o;
            if (anotherMad.size() != super.size()) {
                return false;
            }
            for (int i = 0; i < super.size(); i += 1) {
                if (anotherMad.get(i) != super.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

