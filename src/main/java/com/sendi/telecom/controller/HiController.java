package com.sendi.telecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HQX
 * 2021/2/28 21:09
 */
@Controller
@RequestMapping("/Hi")
@Validated
public class HiController {


    @GetMapping("/man")
    public String man(Model model) {
        model.addAttribute("name", "何青秀");
        return "index.html";
    }
}

