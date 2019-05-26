import cn.itcast.pojo.Emp;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainTest {

    public static void main(String[] args) {


        // getPassWithSalt();

        // stringsTest();


    }

    private static void getPassWithSalt() {
        for (int i = 0; i < 100; i++) {
            String salt = RandomStringUtils.randomAlphanumeric(10);

            String s = DigestUtils.md5DigestAsHex(("123456"+salt).getBytes());
            System.out.println(s);
        }
    }


    private static void pass() {
        for (int i = 0; i < 100; i++) {
            String salt = RandomStringUtils.randomAlphabetic(10);
            System.out.println(salt);

            String s = DigestUtils.md5DigestAsHex(("123456"+salt).getBytes());
            System.out.println(s);

            System.out.println(DigestUtils.md5DigestAsHex("123456wANruPbpAV".getBytes()));
        }


    }

    private static void multiTest() {
        HashMultiset<Object> hashMultiset = HashMultiset.create();
        hashMultiset.add("A",2);
        hashMultiset.add("B",3);
        hashMultiset.add("C");

        for (Object o : hashMultiset) {
            System.out.println(o+" -- " + hashMultiset.count(o));
        }

        Set<Object> set = hashMultiset.elementSet();
        System.out.println(set);

        //key - vlaue , key - value1,value2
        ImmutableMultimap.Builder<Object, Object> builder = ImmutableMultimap.builder();
        builder.putAll("1","A","B","C");
        builder.putAll("2","R","S");
        builder.putAll("3","X");

        ImmutableMultimap<Object, Object> multimap = builder.build();
        System.out.println(multimap);
    }

    private static void listSetTest() {
        ArrayList<String> arrayList = Lists.newArrayList("A", "B", "C", "D", "E");
        System.out.println(arrayList);

        List<String> reverse = Lists.reverse(arrayList);
        System.out.println(reverse);

        List<List<String>> partition = Lists.partition(arrayList, 2);
        System.out.println(partition);


        HashSet<String> set1 = Sets.newHashSet("A", "B", "C", "A");
        System.out.println(set1);

        HashSet<String> set2 = Sets.newHashSet("A", "C", "D");
        System.out.println(set2);

        Sets.SetView<String> difference = Sets.difference(set2, set1);
        System.out.println(difference);

        Sets.SetView<String> union = Sets.union(set1, set2);
        System.out.println(union);

        Sets.SetView<String> intersection = Sets.intersection(set1, set2);
        System.out.println(intersection);
    }

    private static void objectsTest() {
        Emp e1 = new Emp();
        e1.setName("Tom");
        e1.setSalary(2000.0);

        Emp e2 = new Emp();
        e2.setName("Tom");
        e2.setSalary(2000.0);

        boolean b = Objects.equal(e1,e2);
        System.out.println(b);
    }




    private static void stringsTest() {
        String hello = Strings.repeat("hello ", 5);
        System.out.println(hello);

        String prefix = Strings.commonPrefix("cn.itcast.service", "cn.itheima.dao");
        System.out.println(prefix);

        String s = Strings.emptyToNull("");
        System.out.println(s);

        boolean a = Strings.isNullOrEmpty("");
        System.out.println(a);

        //lpad , rpad
        String s1 = Strings.padEnd("156", 10, '0');
        System.out.println(s1);

        String format = Strings.lenientFormat("你好,%s,你的年龄是%s吗?", "Tom", 18);
        System.out.println(format);

        String format1 = String.format("你好,%s,你的年龄是%s吗?", "TOm", "AA");
        System.out.println(format1);
    }
}
