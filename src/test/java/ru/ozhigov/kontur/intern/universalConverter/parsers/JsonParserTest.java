package ru.ozhigov.kontur.intern.universalConverter.parsers;

import org.junit.Test;
import ru.ozhigov.kontur.intern.universalConverter.exceptions.BadRequestException;
import ru.ozhigov.kontur.intern.universalConverter.utils.Tuple;

public class JsonParserTest {

    @Test
    public void testParse() throws BadRequestException {
        assert new Tuple<>("pr", "kv").equals(
                JsonParser.parseRequest("{\"from\":\"pr\", \"to\":\"kv\"}"));
        assert new Tuple<>("pr", "kv").equals(
                JsonParser.parseRequest("{\"to\":\"kv\", \"from\":\"pr\"}"));
    }

    @Test(expected = BadRequestException.class)
    public void testException() throws BadRequestException {
        JsonParser.parseRequest("\"from\":\"pr\", \"to\":\"kv\"}");
        JsonParser.parseRequest("{\"ready\":\"pr\", \"to\":\"kv\"}");
    }
}
