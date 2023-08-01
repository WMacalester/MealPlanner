package com.macalester.mealplanner.recipes;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
  boolean existsByName(String name);

  @Query("SELECT r FROM Recipe r WHERE r NOT IN :recipesToCheck")
  Set<Recipe> findAllNotInCollection(@Param("recipesToCheck") Collection<Recipe> recipesToCheck);
}
