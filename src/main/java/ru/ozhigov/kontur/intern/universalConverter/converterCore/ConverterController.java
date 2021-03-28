package ru.ozhigov.kontur.intern.universalConverter.converterCore;

import ru.ozhigov.kontur.intern.universalConverter.exceptions.*;
import ru.ozhigov.kontur.intern.universalConverter.parsers.*;
import ru.ozhigov.kontur.intern.universalConverter.utils.Tuple;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ConverterController {
    private final Converter converter;

    public ConverterController(ApplicationArguments args) throws IOException {
        this.converter = new Converter(
                new CsvParser(args.getSourceArgs()[0], ",")
                        .getMapConverter());
    }

    @PostMapping(value = "/convert", consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> convert(@RequestBody String request) {
        try {
            Tuple<String, String> parseRequest = JsonParser.parseRequest(
                    request);
            String coefficient = converter.converter(
                    ExpressionParser.parse(parseRequest.getKey()),
                    ExpressionParser.parse(parseRequest.getValue()), 1);
            return new ResponseEntity<>(coefficient, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

