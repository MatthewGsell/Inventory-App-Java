package com.matt.inventoryapplicationjava.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.matt.inventoryapplicationjava.models.Category;
import com.matt.inventoryapplicationjava.models.Categorytodelete;
import com.matt.inventoryapplicationjava.models.Item;
import com.matt.inventoryapplicationjava.models.Itemtodelete;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import jakarta.validation.Valid;




@Controller
public class InventoryController {

    String uri = "mongodb+srv://testuser:5gftthXvh5Wetv2z@cluster0.ncnydoa.mongodb.net/?retryWrites=true&w=majority";
        
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase database = mongoClient.getDatabase("inventory-app-java");
    MongoCollection<Document> categories = database.getCollection("categories");
	MongoCollection<Document> items = database.getCollection("items");
    ArrayList<Document> categorArrayList = new ArrayList<Document>();
    ArrayList<Document> itemArrayList = new ArrayList<Document>();

    public void getcategoryanditemlists() {
        categorArrayList = new ArrayList<Document>();
        itemArrayList = new ArrayList<Document>();
        FindIterable<Document> cats = categories.find();
        java.util.Iterator<Document> cat = cats.iterator();
        while(cat.hasNext()) {
            categorArrayList.add(cat.next());
        }

        FindIterable<Document> its = items.find();
        java.util.Iterator<Document> it = its.iterator();
        while(it.hasNext()) {
            itemArrayList.add(it.next());
        }
    }


    @GetMapping("/")
    public String mainpage(Model model) {
        getcategoryanditemlists();
        

        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
       
        return "mainpage";
    }
    @GetMapping("/newcategory") 
    public String newcategorypage(Model model) {
        getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
        return "newcategory";
    }
    @PostMapping("/newcategory") 
    public String postnewcategory(@Valid @ModelAttribute Category newcategory, BindingResult result, Model model ) {
        getcategoryanditemlists();

        Document duplicate = categories.find(eq("name", newcategory.name.trim())).first();


        
        if (result.hasErrors() || duplicate != null) {
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
            model.addAttribute("error", "Wrong name format. Must be between 1 and 20 characers and not a duplicate of another category");
            return "newcategory";
        }else {
            categories.insertOne(new Document().append("_id", new ObjectId()).append("name", newcategory.name.trim()));
            getcategoryanditemlists();
    
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
           
            return "mainpage";
        }        
    }
    @GetMapping("/newitem")
    public  String newitempage(Model model) {
        getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
        return "newitem";
    }
    @PostMapping("/newitem")
    public String postnewitem(@Valid @ModelAttribute Item newitem, BindingResult result, Model model) {
        Document duplicate = items.find(eq("name", newitem.name.trim())).first();
        Document doescatexist = categories.find(eq("name", newitem.category.trim())).first();

        if(result.hasErrors() || duplicate != null || doescatexist == null) {
            model.addAttribute("error", "Wrong name format. Must be between 1 and 20 characers have a valid current category and not be a duplicate item");
            getcategoryanditemlists();
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
            return "newitem";
        }
        else {
           

            int quantity = Integer.parseInt(newitem.quantity);

            items.insertOne(new Document().append("_id", new ObjectId()).append("name", newitem.name.trim()).append("category", newitem.category).append("description", newitem.description).append("quantity", quantity));
            getcategoryanditemlists();
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
            return "mainpage";

        }
       

    }


    @GetMapping("/increaseitem")
    public String increaseitem(@RequestParam("name") String itemname, Model model) {
       items.findOneAndUpdate(eq("name", itemname), Updates.inc("quantity", 1));
       getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
       return "mainpage";
      
    }
    @GetMapping("/decreaseitem")
    public String decreaseitem(@RequestParam("name") String itemname, Model model) {
       items.findOneAndUpdate(eq("name", itemname), Updates.inc("quantity",  - 1));
       getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
       return "mainpage";
      
    }
    
    

    @GetMapping("/deletecategory")
    public  String deletecategorypage(Model model) {
        getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
        return "deletecategory";
    }
    @GetMapping("/deleteitem")
    public  String deleteitempage(Model model) {
        getcategoryanditemlists();
        model.addAttribute("categories", categorArrayList);
        model.addAttribute("items", itemArrayList);
        return "deleteitem";
    }
    @PostMapping("/deleteitem") 
    public String deleteitem(@ModelAttribute Itemtodelete item, Model model)  {
        Document isreal = items.find(eq("name", item.name.trim())).first();
        if (isreal != null) {
            items.findOneAndDelete(eq("name", item.name.trim()));
            getcategoryanditemlists();
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
           return "mainpage";
        } else {
            getcategoryanditemlists();
            model.addAttribute("error", "Item cannot be found please make sure the spelling is correct.");model.addAttribute("error", "Item cannot be found please make sure the spelling is correct.");
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
           return "deleteitem";
        }
    }  

    @PostMapping("/deletecategory") 
    public String deletecategory(@ModelAttribute Categorytodelete item, Model model)  {
        Document isreal = categories.find(eq("name", item.name.trim())).first();
        if (isreal != null) {
            categories.findOneAndDelete(eq("name", item.name.trim()));
            getcategoryanditemlists();
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
           return "mainpage";
        } else {
            getcategoryanditemlists();
            model.addAttribute("error", "Category cannot be found please make sure the spelling is correct.");model.addAttribute("error", "Item cannot be found please make sure the spelling is correct.");
            model.addAttribute("categories", categorArrayList);
            model.addAttribute("items", itemArrayList);
           return "deletecategory";
        }
    }  


    @GetMapping("/reloadcategory")
    String reloadcategory(@RequestParam("name") String categoryname, Model model) {
        categorArrayList = new ArrayList<Document>();
        itemArrayList = new ArrayList<Document>();
        FindIterable<Document> cats = categories.find();
        java.util.Iterator<Document> cat = cats.iterator();
        while(cat.hasNext()) {
            categorArrayList.add(cat.next());
        }

        FindIterable<Document> its = items.find(eq("category", categoryname));
        java.util.Iterator<Document> it = its.iterator();
        while(it.hasNext()) {
            itemArrayList.add(it.next());
        }
        model.addAttribute("items", itemArrayList);
        model.addAttribute("categories", categorArrayList);

        return "mainpage";
    }

    








}
