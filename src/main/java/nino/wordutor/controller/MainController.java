package nino.wordutor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * zk springboot中需要使用controller来实现页面的跳转
 */
@Controller
public class MainController {

    /**
     * 单词录入页面
     * @return
     */
    @GetMapping("/input")
    public String input() {
        return "input";
    }

    /**
     * 背单词
     * @return
     */
    @GetMapping("/recite")
    public String recite() {
        return "recite";
    }

    /**
     * 造句练习
     * @return
     */
    @GetMapping("/exercise")
    public String exercise() {
        return "exercise";
    }

    /**
     *  拼写单词
     * @return
     */
    @GetMapping("/spell")
    public String spell() {
        return "spell";
    }

    /**
     *  列出全部
     * @return
     */
    @GetMapping("/list")
    public String list() {
        return "list";
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }

}
