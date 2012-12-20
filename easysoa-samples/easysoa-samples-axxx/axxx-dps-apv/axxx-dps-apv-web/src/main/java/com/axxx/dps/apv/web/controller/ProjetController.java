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
import org.springframework.web.bind.annotation.RequestParam;

import com.axxx.dps.apv.model.Projet;
import com.axxx.dps.apv.model.Tdr;
import com.axxx.dps.apv.service.ProjetService;
import com.axxx.dps.apv.service.TdrService;

@Controller
public class ProjetController {
    
    @Autowired
    private ProjetService projetService;
    
    @Autowired
    private TdrService tdrService;

    @RequestMapping("projet/list")
    public String list(@RequestParam(value="tdrId", required=false) Long tdrId, Map<String, Object> map) {
        List<Projet> projets;
        if (tdrId != null && tdrId != 0) {
            
            Tdr tdr = tdrService.getById(tdrId);
            if (tdr != null) {
                //projets = projetService.listByTdr(tdrId);
                projets = tdr.getProjets(); // NO no session
                //projets = tdrService.getProjets(tdr);
            } else {
                throw new RuntimeException("no tdr for tdrId " +  tdrId); // TODO better
            }
        } else {
            projets = projetService.list();
        }
        map.put("projets", projets);
        map.put("projet", new Projet()); // for form
        return "projets";
    }

    @RequestMapping(method=RequestMethod.POST, value="/projet/add")
    public String add(@Valid @ModelAttribute("projet") Projet projet, BindingResult result) {
        if (result.hasErrors()) {
            return "projets";
        }
        projetService.create(projet);
        return "redirect:list";
    }

    @RequestMapping(method=RequestMethod.GET, value="projet/delete/{projetId}")
    public String delete(@PathVariable("projetId") long projetId) {
        projetService.delete(projetId);
        return "redirect:/projet/list";
    }
    
}
