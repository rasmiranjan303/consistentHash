import java.util.TreeMap;

public class Test {

    public static void main(String args[])
    {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        map.put(57,45);
        map.put(83, 25);
        map.put(62,30);
        map.put(73, 45);
        map.put(21,78);
        System.out.println(map.tailMap(60));
    }
}
