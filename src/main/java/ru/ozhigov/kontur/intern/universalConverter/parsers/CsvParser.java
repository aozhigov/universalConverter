package ru.ozhigov.kontur.intern.universalConverter.parsers;

import java.io.*;
import java.util.*;

public class CsvParser {
    private final BufferedReader bufferFile;
    private final String separator;

    public CsvParser(String path, String separator) throws IOException {
        this.separator = separator;
        bufferFile = new BufferedReader(new FileReader(new File(path)));
    }

    private List<String> nextLine() throws IOException {
        String line = bufferFile.readLine();

        if (line == null)
            bufferFile.close();

        return line != null
                ? Arrays.asList(line.split(separator))
                : null;
    }

    public HashMap<String, HashMap<String, Double>> getMapConverter()
            throws IOException {
        HashMap<String, HashMap<String, Double>> mapConverter = new HashMap<>();
        List<String> temp = removeSpaceList(nextLine());

        while (temp != null) {
            if (!mapConverter.containsKey(temp.get(0)))
                mapConverter.put(temp.get(0), new HashMap<>());
            mapConverter.get(temp.get(0)).put(
                    temp.get(1), Double.parseDouble(temp.get(2)));
            mapConverter.get(temp.get(0)).put(temp.get(0), 1.0);

            if (!mapConverter.containsKey(temp.get(1))) {
                String temp_str = temp.get(1);
                mapConverter.put(temp_str, new HashMap<>() {{put(temp_str, 1.0);}});
            }

            mapConverter.get(temp.get(1)).put(
                    temp.get(0), 1 / Double.parseDouble(temp.get(2)));
            temp = removeSpaceList(nextLine());
        }
        mapConverter.put("1", new HashMap<>());
        mapConverter.get("1").put("1", 1.0);
        mapConverter.get("1").put("", 1.0);
        mapConverter.put("", new HashMap<>());
        mapConverter.get("").put("1", 1.0);
        mapConverter.get("").put("", 1.0);

        return mapConverter;
    }

    public static List<String> removeSpaceList(List<String> list){
        if (list == null)
            return null;

        ArrayList<String> answer = new ArrayList<>();
        for (String item : list)
            answer.add(removeSpaceString(item));

        return answer;
    }

    public static String removeSpaceString(String item){
        StringBuilder temp = new StringBuilder();

        for (char ch : item.toCharArray()) {
            if (ch == ' ')
                continue;
            temp.append(ch);
        }

        return temp.toString();
    }
}
