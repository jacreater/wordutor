package nino.wordutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/input")
    public String input() {
        return "input";
    }

    @GetMapping("/recite")
    public String recite() {
        return "recite";
    }

    @GetMapping("/spell")
    public String spell() {
        return "spell";
    }


}
