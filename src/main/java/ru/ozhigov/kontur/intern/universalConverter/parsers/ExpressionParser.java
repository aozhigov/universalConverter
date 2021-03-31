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
        CopyOnWriteArraySet<String> numKeys = list.numKeys();
        CopyOnWriteArraySet<String> denKeys = list.denKeys();
        HashMap<String, Integer> numCounter = list.numCounter();
        HashMap<String, Integer> denCounter = list.denCounter();

        for (String item : numKeys)
            if (denKeys.contains(item)) {
                int min = Math.min(numCounter.get(item),
                        denCounter.get(item));

                numCounter.put(item, numCounter.get(item) - min);
                denCounter.put(item, denCounter.get(item) - min);

                if (numCounter.get(item) <= 0)
                    numKeys.remove(item);

                if (denCounter.get(item) <= 0)
                    denKeys.remove(item);
            }

        return new ListForFraction<>() {{
                        addAll(getResultSequence(
                                numKeys, numCounter));
                        addDel();
                        addAll(getResultSequence(
                                denKeys, denCounter));
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