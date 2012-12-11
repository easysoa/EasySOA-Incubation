package fr.axxx.dps.apv.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TdrController {

    @RequestMapping(method=RequestMethod.GET, value="/tdrs")
    public String listTdrs() {
        return "tdrs";
    }
    
}
