package ru.ozhigov.kontur.intern.universalConverter.parsers;

import ru.ozhigov.kontur.intern.universalConverter.exceptions.*;
import ru.ozhigov.kontur.intern.universalConverter.utils.Tuple;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class JsonParser {
    public static Tuple<String, String> parseRequest(String jsonIn)
            throws BadRequestException {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(jsonIn);
            return new Tuple<>((String) json.get("from"),
                    (String) json.get("to"));
        }
        catch (ParseException e) {
            throw new BadRequestException("Exception in JSON");
        }
    }
}
