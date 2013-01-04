/**
 * 
 */
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

/**
 * @author jguillemotte
 *
 */
@Controller
public class TdrPrecompteController {

    @Autowired
    private TdrService tdrService;

    /**
     * Get the TDR precomptes list
     * @param map
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="tdrprecompte/list")
    public String list(Map<String, Object> map) {
        List<Tdr> tdrs = tdrService.getTdrPrecomptes();
        map.put("tdrPrecomptes", tdrs);
        return "tdrPrecomptes";
    }
 
    /**
     * Get the details of a TDR precompte
     * @param map
     * @param tdrId 
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="tdrprecompte/details/{tdrId}")
    public String details(Map<String, Object> map, @PathVariable("tdrId") long tdrId) {
        Tdr tdr = tdrService.getById(tdrId);
        map.put("tdrPrecompteDetails", tdr);
        return "tdrPrecompteDetails";
    }

    /**
     * Save the details of a TDR
     * @param map
     * @param tdrId 
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/tdrprecompte/save")
    public String save(@Valid @ModelAttribute("tdrPrecompteDetails") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) { // validation check, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
            return "redirect:/tdrprecompte/details/" + tdr.getId();
        }
        // TODO : Compute fields
        /*
        tdr.getTdrTdb().setDotationGlobale(dotationGlobale);
       
        private double dotationGlobale; // computed during conventionnement, > 0 to approve it ; for Client.Dot_Glob_APV_N (should be reliquatanneeprecedente + dotationannuelle)
        private double reliquatAnneePrecedente; // set during conventionnement ; for Client.Dont_Reliquat_N_1
        private double dotationAnnuelle; // set during conventionnement ; for Dont_Dot_N (& Client.Dot_Glob_APV_N) ; = dotationglobale + reliquatanneeprecedente
        
        private double sommeUtilisee; // for Client.Montant_Utilise_N ; = sum of projet.montant for all approved projets
        private double montantDisponible; // = dotationglobale - sommeutilisee
        private double reliquat; // = dotationglobale - sommeutilisee in year N (or N-1 ?)
        private int nbBeneficiairesApv; // for Client.Nb_Benef_N ; = sum of tdr.nbx for every public x ; = sum of projet.nb for all approved projets
        */

        tdrService.update(tdr);
        return "redirect:/tdrprecompte/details/" + tdr.getId();
    }

    /**
     * Save the details of a TDR
     * @param map
     * @param tdrId 
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/tdrprecompte/approve")
    public String approve(@Valid @ModelAttribute("tdrPrecompteDetails") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) { // validation check, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
            return "redirect:/tdrprecompte/details/" + tdr.getId();
        }
        tdrService.update(tdr);
        tdr.getTdrTdb().setStatus("approved");
        tdrService.update(tdr);
        return "redirect:/tdrprecompte/details/" + tdr.getId();
    }    
    
    /**
     * Delete a TDR precompte
     * @param tdrId
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="tdrprecompte/delete/{tdrId}")
    public String delete(@PathVariable("tdrId") long tdrId) {
        tdrService.delete(tdrId);
        return "tdrPrecomptes";
    }
    
    /**
     * prepare the newTdr page
     * @return
     */
    @RequestMapping(method=RequestMethod.GET, value="tdrprecompte/newTdr")
    public String newTdr(Map<String, Object> map) {
        map.put("tdr", new Tdr());
        return "newTdr";
    }

    /**
     * Create a new Test TDR
     * @param tdr
     * @param result
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/tdrprecompte/createNewTestTdr")
    public String createNewTestTdr(@Valid @ModelAttribute("tdr") Tdr tdr, BindingResult result) {
        if (result.hasErrors()) { // validation check, see http://www.mkyong.com/spring-mvc/spring-3-mvc-and-jsr303-valid-example/
            return "redirect:/tdrprecompte/details/" + tdr.getId();
        }
        tdrService.create(tdr);
        return "redirect:/tdrprecompte/list";
    }
    
}
