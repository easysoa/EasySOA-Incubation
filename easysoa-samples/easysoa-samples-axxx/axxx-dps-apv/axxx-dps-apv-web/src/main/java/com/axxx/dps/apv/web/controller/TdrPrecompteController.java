/**
 * 
 */
package com.axxx.dps.apv.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("tdrprecompte/list")
    public String list(Map<String, Object> map) {
        List<Tdr> tdrs = tdrService.getTdrPrecomptes();
        map.put("tdrPrecomptes", tdrs);
        //map.put("tdr", new Tdr()); // for form
        return "tdrPrecomptes";
    }
 
    @RequestMapping("tdrprecompte/details")
    public String details(Map<String, Object> map) {
        // TODO get the TDR precompte details
        map.put("tdrPrecompteDetails", "");
        return "tdrPrecompteDetails";
    }
}
