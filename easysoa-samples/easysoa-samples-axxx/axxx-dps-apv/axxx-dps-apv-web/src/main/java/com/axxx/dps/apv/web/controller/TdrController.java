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
import com.axxx.dps.apv.model.TdrTdb;
import com.axxx.dps.apv.service.TdrService;

@Controller
public class TdrController {
    
    @Autowired
    private TdrService tdrService;

    @RequestMapping("tdr/list")
    public String list(Map<String, Object> map) {
        //List<Tdr> tdrs = tdrService.list();
        List<Tdr> tdrs = tdrService.getTdrs();
        map.put("tdrs", tdrs);
        map.put("tdr", new Tdr()); // for form
        return "tdrs";
    }

    @RequestMapping(method=RequestMethod.POST, value="/tdr/add")
    public String add(@Valid @ModelAttribute("tdr") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) { // validation check, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
            return "tdrs";
        }
        tdrService.create(tdr);
        return "redirect:list";
    }

    /**
     * Save the details of a TDR
     * @param map
     * @param tdrId 
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/tdr/save")
    public String save(@Valid @ModelAttribute("tdr") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) { // validation check, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
            return "redirect:/tdr/details/" + tdr.getId();
        }
        TdrTdb tdrTdb = tdr.getTdrTdb();
        tdrTdb.setDotationGlobale(tdrTdb.getReliquatAnneePrecedente() + tdrTdb.getDotationAnnuelle());
        tdrTdb.setSommeUtilisee(0); // already 0 for tdr precompte
        tdrTdb.setMontantDisponible(tdrTdb.getDotationGlobale() - tdrTdb.getSommeUtilisee()); // = dotationglobale - sommeutilisee
        tdrService.update(tdr);
        tdrService.publish(tdr);
        return "redirect:/tdr/details/" + tdr.getId();
    }    
    
    @RequestMapping(method=RequestMethod.GET, value="tdr/delete/{tdrId}")
    public String delete(@PathVariable("tdrId") long tdrId) {
        tdrService.delete(tdrId);
        return "redirect:/tdr/list";
    }

    @RequestMapping(method=RequestMethod.GET, value="tdr/details/{tdrId}")
    public String details(Map<String, Object> map, @PathVariable("tdrId") long tdrId) {
        Tdr tdr = tdrService.getById(tdrId);
        map.put("tdr", tdr);
        return "tdrDetails";
    }
    
}
