package ru.ozhigov.kontur.intern.universalConverter.parsers;

import ru.ozhigov.kontur.intern.universalConverter.utils.ListForFraction;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ExpressionParser {
    private static final HashSet<String> delimiters = new HashSet<>() {{
        add(" ");
        add("\n");
        add("\t");
        add("\r");
    }};
    private static final HashSet<String> operators = new HashSet<>() {{
        add("*");
        add("/");
    }};

    public static ListForFraction<String> parse(String fraction) {
        ListForFraction<String> parseStr = new ListForFraction<>();
        StringTokenizer token = new StringTokenizer(
                fraction, " */", true);
        String currentSymbol;

        while (token.hasMoreTokens()) {
            currentSymbol = CsvParser.removeSpaceString(token
                    .nextToken()
                    .toLowerCase()
                    .trim());

            if (delimiters.contains(currentSymbol)
                    || currentSymbol.equals(""))
                continue;

            if (operators.contains(currentSymbol)) {
                if (currentSymbol.equals("/"))
                    parseStr.addDel();
                continue;
            }

            parseStr.add(currentSymbol);
        }
        return simplification(parseStr);
    }

    private static ListForFraction<String> simplification(
            ListForFraction<String> list) {
        CopyOnWriteArraySet<String> leftPartKeys = list.getLeftPartKeys();
        CopyOnWriteArraySet<String> rightPartKeys = list.getRightPartKeys();
        HashMap<String, Integer> leftPartCounter = list.getLeftPartCounter();
        HashMap<String, Integer> rightPartCounter = list.getRightPartCounter();

        for (String item : leftPartKeys)
            if (rightPartKeys.contains(item)) {
                int min = Math.min(leftPartCounter.get(item),
                        rightPartCounter.get(item));

                leftPartCounter.put(item, leftPartCounter.get(item) - min);
                rightPartCounter.put(item, rightPartCounter.get(item) - min);

                if (leftPartCounter.get(item) <= 0)
                    leftPartKeys.remove(item);

                if (rightPartCounter.get(item) <= 0)
                    rightPartKeys.remove(item);
            }

        return new ListForFraction<>() {{
                        addAll(getResultSequence(
                                leftPartKeys, leftPartCounter));
                        addDel();
                        addAll(getResultSequence(
                                rightPartKeys, rightPartCounter));
                    }};
    }

    private static ArrayList<String> getResultSequence(
            CopyOnWriteArraySet<String> keys,
            HashMap<String, Integer> map) {
        ArrayList<String> result = new ArrayList<>();

        for (String item : keys) {
            int count = map.get(item);
            while (count-- > 0)
                result.add(item);
        }

        return result;
    }
}