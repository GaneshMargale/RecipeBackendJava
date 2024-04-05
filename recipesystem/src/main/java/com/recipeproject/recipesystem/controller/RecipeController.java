package com.recipeproject.recipesystem.controller;

import com.recipeproject.recipesystem.model.Recipe;
import com.recipeproject.recipesystem.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recipe")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/add")
    public String add(@RequestBody Recipe recipe){
        recipeService.saveRecipe(recipe);
        return "New Recipe Added";
    }

    @GetMapping("/getAll")
    public List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable int id) {
        boolean deleted = recipeService.deleteRecipeById(id);

        if (deleted) {
            return ResponseEntity.ok("Recipe successfully deleted");
        } else {
            return ResponseEntity.status(404).body("Recipe not found");
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        Recipe existingRecipe = recipeService.getRecipeById(id);

        if (existingRecipe == null) {
            return ResponseEntity.notFound().build();
        }

        if (updates.containsKey("name")) {
            existingRecipe.setName((String) updates.get("name"));
        }
        if (updates.containsKey("ingredients")) {
            existingRecipe.setIngredients((String) updates.get("ingredients"));
        }
        if (updates.containsKey("description")) {
            existingRecipe.setDescription((String) updates.get("description"));
        }

        Recipe updatedRecipe = recipeService.updateRecipe(existingRecipe);

        return ResponseEntity.ok(updatedRecipe);
    }
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadRecipe(@PathVariable int id) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }

        File recipeFile = null;
        try {
            recipeFile = File.createTempFile("recipe_" + id, ".txt");
            try (FileOutputStream fos = new FileOutputStream(recipeFile)) {
                String recipeContent = createRecipeContent(recipe);
                fos.write(recipeContent.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recipe_" + id + ".txt");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");

            FileInputStream fis = new FileInputStream(recipeFile);
            InputStreamResource resource = new InputStreamResource(fis);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } finally {
            if (recipeFile != null && !recipeFile.delete()) {
                System.err.println("Failed to delete temporary file: " + recipeFile.getAbsolutePath());
            }
        }
    }

    private String createRecipeContent(Recipe recipe) {
        StringBuilder content = new StringBuilder();
        content.append("Recipe ID: ").append(recipe.getId()).append("\n");
        content.append("Name: ").append(recipe.getName()).append("\n");
        content.append("Ingredients: ").append(recipe.getIngredients()).append("\n");
        content.append("Description: ").append(recipe.getDescription()).append("\n");
        return content.toString();
    }
}
