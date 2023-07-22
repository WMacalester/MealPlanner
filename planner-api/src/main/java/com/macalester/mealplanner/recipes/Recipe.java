package com.macalester.mealplanner.recipes;

import com.macalester.mealplanner.ingredients.Ingredient;
import com.macalester.mealplanner.validator.NameConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Recipe {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NameConstraint
  @Column(unique = true)
  private String name;

  @ManyToMany
  @JoinTable(
      name = "Recipe_Ingredients",
      joinColumns = {@JoinColumn(name = "recipe_id")},
      inverseJoinColumns = {@JoinColumn(name = "ingredient_id")})
  private Set<Ingredient> ingredients;

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Recipe other)) {
      return false;
    }
    return name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return 113 + (this.name == null ? 37 : this.name.hashCode());
  }
}
