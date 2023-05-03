package com.macalester.mealplanner.ingredients;

import com.macalester.mealplanner.recipes.Recipe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ingredient {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank(message = "Name must not be blank")
  @Column(unique = true)
  private String name;

  @ManyToMany private List<Recipe> recipes;

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Ingredient other)) {
      return false;
    }
    return name.equals(other.name);
  }

  @Override
  public int hashCode() {
    return 59 + (this.name == null ? 43 : this.name.hashCode());
  }
}
