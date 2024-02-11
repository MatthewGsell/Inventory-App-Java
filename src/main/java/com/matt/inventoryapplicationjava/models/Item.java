package com.matt.inventoryapplicationjava.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Valid






public class Item {
    @NotNull(message = "field cannot be empty")
    @NotBlank(message = "field cannot be empty")
    @Size(min = 1, max = 20, message = "Must be between 1 and 20 characters")
    public String name;
    @NotNull(message = "field cannot be empty")
    @NotBlank(message = "field cannot be empty")
    public String category;
    public String description;
    public String quantity = "0";

    public void setName(String name) {
        this.name = name;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setQuantity(String quantity) {
        if (quantity != "") {
            this.quantity = quantity;
        }
       
        
     
    }
}
