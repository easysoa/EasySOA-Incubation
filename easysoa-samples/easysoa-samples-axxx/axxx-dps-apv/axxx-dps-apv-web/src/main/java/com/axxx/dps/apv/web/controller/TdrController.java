package com.axxx.dps.apv.web.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.service.TdrService;

@Controller
public class TdrController {
    
    @Autowired
    private TdrService tdrService;

    @RequestMapping("tdr/list")
    public String list(Map<String, Object> map) {
        List<Tdr> tdrs = tdrService.list();
        map.put("tdrs", tdrs);
        map.put("tdr", new Tdr()); // for form
        return "tdrs";
    }

    @RequestMapping(method=RequestMethod.POST, value="/tdr/add")
    public String add(@Valid @ModelAttribute("tdr") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) {
            return "tdrs";
        }
        tdrService.create(tdr);
        return "redirect:list";
    }

    @RequestMapping(method=RequestMethod.GET, value="tdr/delete/{tdrId}")
    public String delete(@PathVariable("tdrId") long tdrId) {
        tdrService.delete(tdrId);
        return "redirect:/tdr/list";
    }
    
}
