package com.macalester.mealplanner.filter;

import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final RecipeRepository recipeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Recipe> findAllRecipesWithCriteria(FilterRequest request){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteriaQuery = criteriaBuilder.createQuery(Recipe.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<Recipe> root = criteriaQuery.from(Recipe.class);
        if (request.getName() != null){
            if (!request.getName().matches("^[a-zA-Z ]+$")) {
                throw new IllegalArgumentException("Recipe name contained an illegal character");
            }
            Predicate namePredicate = criteriaBuilder
                    .like(root.get("name"), "%"+request.getName()+"%");
            predicates.add(namePredicate);
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
