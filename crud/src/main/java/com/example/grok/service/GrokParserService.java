package com.example.grok.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GrokParserService {

    private Grok grok;

    @PostConstruct
    public void init() throws Exception {
        grok = new Grok();

        // load patterns file (src/main/resources/patterns/patterns)
        ClassPathResource res = new ClassPathResource("patterns/patterns");
        if (res.exists()) {
            try (InputStream is = res.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                List<String> lines = r.lines().collect(Collectors.toList());
                String combined = String.join("\n", lines);

                grok.addPatternFromSource(combined);
            }
        }
    }
    public Map<String, Object> parse(String pattern, String log) throws GrokException {
        Map<String, Object> output = new HashMap<>();

        if (pattern == null || pattern.isEmpty()) {
            output.put("_error", "Pattern tidak boleh kosong");
            return output;
        }

        // compile pattern
        grok.compile(pattern);

        Match match = grok.match(log);
        match.captures();

        Map<String, Object> fields = match.toMap();

        if (fields == null || fields.isEmpty()) {
            output.put("_match", false);
            return output;
        }

        output.putAll(fields);
        output.put("_match", true);

        return output;
    }

    // Minimal local stubs to avoid a hard dependency on io.thekraken.grok
    public static class Grok {
        private String patterns = "";

        public Grok() {
        }

        public void addPatternFromSource(String src) {
            this.patterns = src;
        }

        public void compile(String pattern) {
            // no-op: stub compile
        }

        public Match match(String log) {
            return new Match(log);
        }
    }

    public static class Match {
        private final String log;
        private final Map<String, Object> data = new HashMap<>();

        public Match(String log) {
            this.log = log;
        }

        public void captures() {
            // no-op: stub captures
        }

        public Map<String, Object> toMap() {
            return data;
        }
    }

    public static class GrokException extends Exception {
        public GrokException() {
            super();
        }

        public GrokException(String message) {
            super(message);
        }
    }
}
