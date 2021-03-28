package ru.ozhigov.kontur.intern.universalConverter.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class ListForFractionTest {
    ListForFraction<Integer> list1;
    ListForFraction<Integer> list2;
    ListForFraction<Integer> list3;
    ListForFraction<Integer> list4;

    @Before
    public void setUp() {
        list1 = new ListForFraction<>() {{
            add(1);
            addDel();
            add(2);
        }};
        list2 = new ListForFraction<>() {{
            add(1);
            add(2);
        }};
        list3 = new ListForFraction<>() {{
            add(1);
            add(2);
            add(3);
            addDel();
            add(4);
        }};
        list4 = new ListForFraction<>() {{
            addDel();
            add(1);
        }};
    }

    @Test
    public void testList(){
        assert list1.getIdxDel() == 1;
        assert list1.getFirstSize() == 1;
        assert list1.getSecondSize() == 1;
        assert (new HashMap<>() {{put(1, 1);}})
                .equals(list1.getLeftPartCounter());
        assert (new HashMap<>() {{put(2, 1);}})
                .equals(list1.getRightPartCounter());

        assert list2.getIdxDel() == -1;
        assert list2.getFirstSize() == 2;
        assert list2.getSecondSize() == 0;
        assert (new HashMap<>() {{put(1, 1); put(2, 1);}})
                .equals(list2.getLeftPartCounter());
        assert (new HashMap<>())
                .equals(list2.getRightPartCounter());

        assert list3.getIdxDel() == 3;
        assert list3.getFirstSize() == 3;
        assert list3.getSecondSize() == 1;
        assert (new HashMap<>() {{put(1, 1); put(2, 1); put(3, 1);}})
                .equals(list3.getLeftPartCounter());
        assert (new HashMap<>() {{put(4, 1);}})
                .equals(list3.getRightPartCounter());

        assert list4.getIdxDel() == 0;
        assert list4.getFirstSize() == 0;
        assert list4.getSecondSize() == 1;
        assert (new HashMap<>())
                .equals(list4.getLeftPartCounter());
        assert (new HashMap<>() {{put(1, 1);}})
                .equals(list4.getRightPartCounter());
    }
}
