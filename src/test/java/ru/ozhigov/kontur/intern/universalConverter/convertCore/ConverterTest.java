package ru.ozhigov.kontur.intern.universalConverter.convertCore;

import ru.ozhigov.kontur.intern.universalConverter.converterCore.Converter;
import org.junit.Before;
import org.junit.Test;
import ru.ozhigov.kontur.intern.universalConverter.parsers.*;

import java.io.IOException;
import java.util.HashSet;

public class ConverterTest {
        CsvParser csv;
        Converter convert;

        @Before
        public void setUp() throws IOException {
            csv = new CsvParser(
                    "./src/main/resources/valueConverter.csv",
                    ",");
            convert = new Converter(csv.getMapConverter());
        }

        @Test
        public void test() throws Exception {
            assert 3600 ==  convert.parseSimpleValue("ч", "с", new HashSet<>());
            assert converterMethod("м/с", "км/ч") == 3.6;
            assert converterMethod("м", "км") == 0.001;
            assert converterMethod("км", "м") == 1000;
            assert converterMethod("см", "км") == 0.00001;
            assert converterMethod("км", "см") == 100000;
            assert converterMethod("км*ч", "с*м") == 3600000;
            assert converterMethod("см*м/м", "мм") == 10;
            assert converterMethod("м/м", "") == 1;
            assert converterMethod("м", "км*с/ч") == 1.0 / 3600000;
            assert converterMethod("км/м", "") == 1000;
            assert converterMethod("", "км/м") == 1.0 / 1000;
            assert converterMethod("ч/с", "") == 3600;
            assert converterMethod("см*см", "мм*мм") == 100;
            assert converterMethod("1/с", "1/мин") == 60;
        }

        private double converterMethod(String from, String to) throws Exception {
            return Double.parseDouble(convert.converter(ExpressionParser.parse(from),
                    ExpressionParser.parse(to), 1));
        }
    }

