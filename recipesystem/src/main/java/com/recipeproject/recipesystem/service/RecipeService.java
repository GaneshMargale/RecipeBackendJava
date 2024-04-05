package com.recipeproject.recipesystem.service;

import com.recipeproject.recipesystem.model.Recipe;

import java.util.List;

public interface RecipeService {
    public Recipe saveRecipe(Recipe recipe);
    public List<Recipe> getAllRecipes();
    public boolean deleteRecipeById(int id);
    Recipe updateRecipe(Recipe recipe);;
    public Recipe getRecipeById(int id);
}
