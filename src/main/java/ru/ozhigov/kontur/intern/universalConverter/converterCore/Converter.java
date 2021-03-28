package ru.ozhigov.kontur.intern.universalConverter.converterCore;

import ru.ozhigov.kontur.intern.universalConverter.exceptions.*;
import ru.ozhigov.kontur.intern.universalConverter.utils.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
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
                new ListForFraction<>(), new ListForFraction<>())), 15);
    }

    private double converter(ListForFraction<String> from,
                             ListForFraction<String> to,
                             double multiplier,
                             ListForFraction<String> fromPrev,
                             ListForFraction<String> toPrev)
            throws NotFoundException, BadRequestException {
        if (from == fromPrev && to == toPrev)
            throw new NotFoundException(to.toString());

        if (from.size() == to.size()
                && from.getFirstSize() == to.getFirstSize()
                && from.getSecondSize() == to.getSecondSize()){
            return convertProduct(from.getLeftPartCounter(),
                    to.getLeftPartCounter(), multiplier)
                    / convertProduct(from.getRightPartCounter(),
                    to.getRightPartCounter(), 1);
        }

        if (to.size() == 0) {
            if (from.getIdxDel() == -1
                    || from.getFirstSize() != from.getSecondSize())
                throw new NotFoundException(to.toString());
            return convertProduct(from.getLeftPartCounter(),
                    from.getRightPartCounter(), 1);
        }

        if (from.size() == 0) {
            if (to.getIdxDel() == -1
                    || to.getFirstSize() != to.getSecondSize())
                throw new NotFoundException(to.toString());
            return 1 / convertProduct(to.getLeftPartCounter(),
                    to.getRightPartCounter(), 1);
        }

        Tuple<ListForFraction<String>, Double> fromReduce =
                reduceFraction(from);
        Tuple<ListForFraction<String>, Double> toReduce =
                reduceFraction(to);

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
            ListForFraction<String> list) throws BadRequestException {
        HashMap<String, Integer> from = list.getLeftPartCounter();
        HashMap<String, Integer> to = list.getRightPartCounter();

        if (from.size() == 0 && to.size() == 0)
            throw new BadRequestException(from.toString());

        return coreConvert(from, to, 1, false);
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

        ListForFraction<String> result = (isBadRequest)
                ? new ListForFraction<>()
                : new ListForFraction<>() {{
                    addAll(fromKeys);
                    addDel();
                    addAll(toKeys);
                }};

        return new Tuple<>(result, multiplier);
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
