package ru.ozhigov.kontur.intern.universalConverter.parsers;

import org.junit.Before;
import org.junit.Test;
import ru.ozhigov.kontur.intern.universalConverter.utils.ListForFraction;

import java.util.concurrent.CopyOnWriteArraySet;

import static ru.ozhigov.kontur.intern.universalConverter.parsers.ExpressionParser.parse;

public class ExpressionParserTest {
    ListForFraction<String> checkList1;
    ListForFraction<String> checkList2;
    ListForFraction<String> checkList3;

    @Before
    public void setUp() {
        checkList1 = new ListForFraction<>() {{
            add("rv");
            addDel();
            add("x");
        }};
        checkList2 = new ListForFraction<>() {{add("rv");}};
        checkList3 = new ListForFraction<>() {{
            addDel();
            add("rv");
        }};
    }

    @Test
    public void testExpressionParser() {
        assert checkListForFraction(parse("rv/x"), checkList1);
        assert checkListForFraction(parse("rv / x"), checkList1);
        assert checkListForFraction(parse("rv * x / x"), checkList2);
        assert checkListForFraction(parse("rv * rv / x * rv"), checkList1);
        assert checkListForFraction(parse("rv * x / x * rv"), new ListForFraction<>());
        assert checkListForFraction(parse("   rv  "), checkList2);
        assert checkListForFraction(parse("   rv * rv   /  rv"), checkList2);
        assert checkListForFraction(parse("   rv        /  rv    *  rv"), checkList3);
    }

    private boolean checkListForFraction(ListForFraction<String> first, ListForFraction<String> second) {
        if (first.getFirstSize() == second.getFirstSize() && first.getSecondSize() == second.getSecondSize()) {
            return checkHashset(first.getLeftPartKeys(), second.getLeftPartKeys()) && checkHashset(first.getRightPartKeys(), second.getRightPartKeys());
        }

        return false;
    }

    private boolean checkHashset(CopyOnWriteArraySet<String> first, CopyOnWriteArraySet<String> second) {
        for (String item : first) {
            if (!second.contains(item)) { return false; }
        }
        return true;
    }
}
