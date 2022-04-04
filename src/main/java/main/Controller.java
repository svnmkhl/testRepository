package main;


import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping("/admin")
    public String index() {
        return "index";
    }

}
