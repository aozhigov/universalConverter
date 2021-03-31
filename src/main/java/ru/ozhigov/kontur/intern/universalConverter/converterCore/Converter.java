package ru.ozhigov.kontur.intern.universalConverter.converterCore;

import ru.ozhigov.kontur.intern.universalConverter.exceptions.*;
import ru.ozhigov.kontur.intern.universalConverter.parsers.ExpressionParser;
import ru.ozhigov.kontur.intern.universalConverter.utils.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class Converter {
    HashMap<String, HashMap<String, Double>> mapConverter;

    public Converter(HashMap<String, HashMap<String, Double>> mapConverter){
        this.mapConverter = mapConverter;
    }

    public double parseSimpleValue(String from, String to,
                                   HashSet<String> visited)
            throws BaseException {
        if (!mapConverter.containsKey(from))
            throw new BadRequestException(from);

        if (!mapConverter.get(from).containsKey(to))
            for (String item : mapConverter.get(from).keySet()){
                if (visited.contains(item)) continue;
                visited.add(item);

                return mapConverter.get(from).get(item)
                        * parseSimpleValue(item, to, visited);
            }

        if (mapConverter.get(from).containsKey(to))
            return mapConverter.get(from).get(to);

        throw new NotFoundException(to);
    }

    public String converter(ListForFraction<String> from,
                            ListForFraction<String> to,
                            double multiplier)
            throws NotFoundException, BadRequestException {
        return rounded(Double.toString(converter(from, to, multiplier,
                null, null)), 15);
    }

    private double converter(ListForFraction<String> from,
                             ListForFraction<String> to,
                             double multiplier,
                             ListForFraction<String> fromPrev,
                             ListForFraction<String> toPrev)
            throws NotFoundException, BadRequestException {
        if (fromPrev != null && toPrev != null
                && from.equals(fromPrev) && to.equals(toPrev))
            throw new NotFoundException(to.toString());

        if (from.size() == to.size()
                && from.numSize() == to.numSize()
                && from.denSize() == to.denSize()){
            return convertProduct(from.numCounter(),
                    to.numCounter(), multiplier)
                    / convertProduct(from.denCounter(),
                    to.denCounter(), 1);
        }

        if (to.size() == 0) {
            if (from.getIdxDel() == -1
                    || from.numSize() != from.denSize())
                throw new NotFoundException(to.toString());
            return convertProduct(from.numCounter(),
                    from.denCounter(), 1);
        }

        if (from.size() == 0) {
            if (to.getIdxDel() == -1
                    || to.numSize() != to.denSize())
                throw new NotFoundException(to.toString());
            return 1 / convertProduct(to.numCounter(),
                    to.denCounter(), 1);
        }

        Tuple<ListForFraction<String>, Double> fromReduce =
                reduceFraction(expandVar(from, multiplier));
        Tuple<ListForFraction<String>, Double> toReduce =
                reduceFraction(expandVar(to, 1));

        return converter(fromReduce.getKey(),
                toReduce.getKey(),
                toReduce.getValue() / fromReduce.getValue(),
                from, to);
    }

    public double convertProduct(HashMap<String, Integer> from,
                                 HashMap<String, Integer> to,
                                 double multiplier)
            throws BadRequestException {
        return coreConvert(from, to, multiplier, true).getValue();
    }

    public Tuple<ListForFraction<String>, Double> reduceFraction(
            Tuple<ListForFraction<String>, Double> value) throws BadRequestException {
        HashMap<String, Integer> from = value.getKey().numCounter();
        HashMap<String, Integer> to = value.getKey().denCounter();

        if (from.size() == 0 && to.size() == 0)
            throw new BadRequestException(from.toString());

        return coreConvert(from, to, value.getValue(), false);
    }

    private Tuple<ListForFraction<String>, Double> coreConvert(
            HashMap<String, Integer> from,
            HashMap<String, Integer> to,
            double multiplier,
            boolean isBadRequest) throws BadRequestException {
        CopyOnWriteArraySet<String> fromKeys = new CopyOnWriteArraySet<>(
                from.keySet());
        CopyOnWriteArraySet<String> toKeys = new CopyOnWriteArraySet<>(
                to.keySet());

        for (String item : fromKeys){
            if (!mapConverter.containsKey(item))
                if (isBadRequest)
                    throw new BadRequestException(from.toString());
                else continue;

            boolean isReduce = false;
            for (String item1: toKeys)
                try {
                    double coefficient = parseSimpleValue(
                            item, item1, new HashSet<>());
                    int min = Math.min(from.get(item), to.get(item1));
                    multiplier *= Math.pow(coefficient, min);

                    from.put(item, from.get(item) - min);
                    to.put(item1, to.get(item1) - min);

                    if (from.get(item) <= 0)
                        fromKeys.remove(item);

                    if (to.get(item1) <= 0)
                        toKeys.remove(item1);

                    isReduce = true;
                } catch (BaseException e){
                    continue;
                }

            if (!isReduce) continue;
        }

        ListForFraction<String> result = new ListForFraction<>();
        if (!isBadRequest){
            addVarCounter(result, from);
            result.addDel();
            addVarCounter(result, to);
        }

        return new Tuple<>(result, multiplier);
    }

    private Tuple<ListForFraction<String>, Double> expandVar(
            ListForFraction<String> value, double coefficient){
        ArrayList<String> numerator = new ArrayList<>();
        ArrayList<String> denominator = new ArrayList<>();

        for (int i = 0; i < value.size(); i++){
            String from = value.get(i);
            int fromParseSize = ExpressionParser.parse(from).size();

            if (mapConverter.containsKey(from))
                for (String to : mapConverter.get(from).keySet()){
                    ListForFraction<String> toParse = ExpressionParser.parse(to);

                    if (toParse.size() > fromParseSize) {
                        HashMap<String, Integer> num = value.numCounter();
                        HashMap<String, Integer> den = value.denCounter();

                        if (i < value.idxDel && num.get(from) > 0)
                            coefficient *= recalculate(
                                    num, from, to,
                                    numerator, denominator, toParse);
                        else if (den.get(from) > 0)
                            coefficient /= recalculate(
                                    num, from, to,
                                    denominator, numerator, toParse);
                    }
                }
        }

        ListForFraction<String> result = new ListForFraction<>();
        addVarCounter(result, value.numCounter());
        result.addAll(numerator);
        result.addDel();
        result.addAll(denominator);
        addVarCounter(result, value.denCounter());

        return new Tuple<>(result, coefficient);
    }

    private void addVarCounter(ArrayList<String> whom,
                               HashMap<String, Integer> counter){
        for (String item: counter.keySet()) {
            int count = counter.get(item);
            while(count-- > 0)
                whom.add(item);
        }
    }

    private double recalculate(HashMap<String, Integer> map, String from, String to,
                             ArrayList<String> numerator, ArrayList<String> denominator,
                             ListForFraction<String> toParse){
        map.put(from, map.get(from) - 1);
        addVarCounter(numerator, toParse.numCounter());
        addVarCounter(denominator, toParse.denCounter());
        return mapConverter.get(from).get(to);
    }

    private String rounded(String coefficient, int countFrac) {
        if (coefficient.matches("\\.")) {
            String wholePart = coefficient.split("\\.")[0];
            String fracPart = coefficient.split("\\.")[1];

            if (wholePart.length() > 1) {
                DecimalFormat format = new DecimalFormat(createFormat(
                        countFrac - wholePart.length()));
                return format.format(Double.parseDouble(coefficient));
            } else
                if (fracPart.length() > countFrac - 1) {
                    DecimalFormat format = new DecimalFormat(
                            "#.##############");
                    return format.format(Double.parseDouble(coefficient));
            }
        }
        return coefficient;
    }

    private String createFormat(int roundPower) {
        return "#." + "#".repeat(Math.max(0, roundPower));
    }
}
