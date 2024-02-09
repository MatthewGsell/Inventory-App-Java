package com.matt.inventoryapplicationjava;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;





@Controller
public class InventoryController {

    @GetMapping
    ("/test")
    public static String test(Model testmodel) {
       
        System.out.println("CFVJKSDVSDLFV");
        String teststring = "my guy";
        testmodel.addAttribute("teststring", teststring);
        return "test"; 
    }
}
