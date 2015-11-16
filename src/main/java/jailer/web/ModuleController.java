package jailer.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ModuleController {

    @RequestMapping("/")
    String sample(Model model) {
    	
        return "sample";
    }
}
