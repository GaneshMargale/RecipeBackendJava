package com.recipeproject.recipesystem.service;

import com.recipeproject.recipesystem.model.Recipe;
import com.recipeproject.recipesystem.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public boolean deleteRecipeById(int id){
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isPresent()) {
            recipeRepository.deleteById(id);
            return true; // Successfully deleted
        } else {
            return false; // Recipe not found, deletion failed
        }
    };

    @Override
    public Recipe updateRecipe(Recipe recipe) {
        Optional<Recipe> existingRecipe = recipeRepository.findById(recipe.getId());

        if (existingRecipe.isPresent()) {
            Recipe updatedRecipe = existingRecipe.get();
            updatedRecipe.setName(recipe.getName());
            updatedRecipe.setIngredients(recipe.getIngredients());
            updatedRecipe.setDescription(recipe.getDescription());

            return recipeRepository.save(updatedRecipe);
        } else {
            throw new NotFoundException("Recipe not found with ID: " + recipe.getId());
        }
    }
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }



    @Override
    public Recipe getRecipeById(int id) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        return optionalRecipe.orElse(null);
    }
}
