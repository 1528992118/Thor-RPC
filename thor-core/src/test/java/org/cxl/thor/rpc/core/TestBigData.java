package org.cxl.thor.rpc.core;

public class TestBigData {

    public static final String data ="1.hashMap:\n" +
            "  1.1 hashMap : 数组加链表\n" +
            "      Node<K,V>[] tab\n" +
            "\t    static class Node<K,V> implements Map.Entry<K,V> {\n" +
            "        final int hash;\n" +
            "        final K key;\n" +
            "        V value;\n" +
            "        Node<K,V> next;\n" +
            "\t    ...\n" +
            "\t  }\n" +
            "  1.2 hash算法优化（1.8以后）：\n" +
            "    static final int hash(Object key) {\n" +
            "        int h;\n" +
            "        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);\n" +
            "    }\n" +
            "     让高低16位进行异或，让他的低16同时保持了高低16位的特征，尽量避免hash值后续出现冲突\n" +
            "  1.3 寻址算法的优化：用与运算代替取模，提升性能\n" +
            "      int bucket = (n - 1) & hash;  \n" +
            "\t   n 代表hash数组容量，必须是2^n，不然会出现分布不均匀\n" +
            "  1.4 hash碰撞：\n" +
            "      默认采用链表，即当多个key寻址得到的bucket位置相同时，会在该数组index上产生链表【O(n)】,\n" +
            "      默认链表长度超过8时，转化为红黑树【O(logn)】\t  \n" +
            "  1.5 resize:\n" +
            "      当数组长度< 数组长度 * 负载因子（默认0.75）的时候，进行扩容，假设需要容纳1000个元素，需要初始容量为 0.75 * size > 1000，2048（2^n）\n" +
            "\t  \n" +
            "\t  a.jdk1.7之前采用头插法，先准备一个 2*oldSize长度的数组，再进行rehash，重新定位每个元素的新位置\n" +
            "\t \n" +
            "\t  \n" +
            "\t  \n" +
            "\t   ";

}
