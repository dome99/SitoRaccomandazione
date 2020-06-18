package classes;

public class Pair<K, V> {

    private final K attr;
    private final V value;

    public static <K, V> Pair<K, V> createPair(K attr, V value) {
        return new Pair<K, V>(attr, value);
    }

    public Pair(K attr, V value) {
        this.attr = attr;
        this.value = value;
    }

    public K getAttr() {
        return attr;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "classes.Pair{" +
                "attr=" + attr +
                ", value=" + value +
                '}';
    }
}
