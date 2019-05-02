package com.javaproject.javatask.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaTaskController {

    @RequestMapping("/first")
    public String firstPage(@RequestParam(value = "stringparam", required = false) String stringparam) {
        if(stringparam == null) {
            stringparam = "no param passed";
        }
        return "First page, " + stringparam + ".";
    }
}
