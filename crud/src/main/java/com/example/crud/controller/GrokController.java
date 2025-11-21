package com.example.crud.controller;

import com.example.grok.service.GrokParserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// import io.thekraken.grok.api.exception.GrokException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Map;

@Controller
public class GrokController {

@Autowired
private GrokParserService service;

@Autowired
private ObjectMapper objectMapper;

@GetMapping({"/","/grok"})
public String form(Model model) {
    model.addAttribute("exampleLog", "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /index.html HTTP/1.1\" 200 512");
    model.addAttribute("examplePattern", "%{IP:client} %{WORD:ident} %{WORD:user} \\[%{HTTPDATE:timestamp}\\] \"%{WORD:method} %{DATA:request} HTTP/%{NUMBER:version}\" %{NUMBER:status} %{NUMBER:bytes}");
    return "grok-form";
}

@PostMapping("/grok/parse")
public String parse(@RequestParam("pattern") String pattern,
    @RequestParam("log") String log,
    Model model) throws JsonProcessingException {
        Map<String, Object> result;
        try {
            java.lang.reflect.Method m = service.getClass().getMethod("parse", String.class, String.class);
            Object r = m.invoke(service, pattern, log);
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) r;
            result = casted;
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            result = java.util.Collections.singletonMap("error", e.toString());
        }
        String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        model.addAttribute("pattern", pattern);
        model.addAttribute("log", log);
        model.addAttribute("resultJson", pretty);
        return "grok-form";
    }
}