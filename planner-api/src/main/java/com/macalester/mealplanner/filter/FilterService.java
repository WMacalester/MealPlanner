package com.macalester.mealplanner.filter;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.recipes.Recipe;
import com.macalester.mealplanner.recipes.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
            String requestName = request.getName().trim().toLowerCase();

            if (!requestName.matches("^[a-z ]+$")) {
                throw new IllegalArgumentException("Recipe name contained an illegal character");
            }

            final String namePattern = "%"+requestName+"%";

            Predicate recipeNamePredicate = criteriaBuilder
                    .like(root.get("name"), namePattern);

//            Create a subquery to find Recipe IDs with Ingredients matching the name
            Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
            Root<Ingredient> subqueryRoot = subquery.from(Ingredient.class);
            subquery.select(subqueryRoot.get("recipes").get("id"));

            Predicate ingredientNamePredicate = criteriaBuilder
                    .like(subqueryRoot.get("name"), namePattern);

            subquery.where(ingredientNamePredicate);

            predicates.add(criteriaBuilder.or(
                    recipeNamePredicate,
                    criteriaBuilder.in(root.get("id")).value(subquery)
            ));
        }

        if (request.getDietType() != null){
            Predicate dietTypePredicate = criteriaBuilder.equal(root.get("dietType"), request.getDietType());
            predicates.add(dietTypePredicate);
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
