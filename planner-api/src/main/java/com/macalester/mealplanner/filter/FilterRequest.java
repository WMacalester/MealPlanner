package com.macalester.mealplanner.filter;

import com.macalester.mealplanner.recipes.DietType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterRequest {
    String name;
    DietType dietType;
}
