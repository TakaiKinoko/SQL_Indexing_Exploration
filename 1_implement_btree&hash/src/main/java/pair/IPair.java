package pair;

/**
 * Interface IPair
 * @author Fang Cabrera 
 * Description: <Key, Value> pairs
 */

public interface IPair<K,V> {

    public K getKey();

    public V getValue();

    public boolean remove();

    public boolean update(V value);
}
