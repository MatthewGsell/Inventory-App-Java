package com.matt.inventoryapplicationjava.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Category {

     @Valid


     @NotNull(message = "field cannot be empty")
     @NotBlank(message = "field cannot be empty")
     @Size(min = 1, max = 20, message = "Must be between 1 and 20 characters")
     public String name;
     public void setName(String name) {
        this.name = name;
     }
}
