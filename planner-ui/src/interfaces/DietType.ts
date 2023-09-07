export enum DietType {
  MEAT = "MEAT",
  VEGETARIAN = "VEGETARIAN",
  VEGAN = "VEGAN",
}

const dietTypeDisplayEmoji = {
  [DietType.MEAT]: "🥩",
  [DietType.VEGETARIAN]: "🥦",
  [DietType.VEGAN]: "🌱",
};

const dietTypeDisplayText = {
  [DietType.MEAT]: "Carnivore",
  [DietType.VEGETARIAN]: "Vegetarian",
  [DietType.VEGAN]: "Vegan",
};

export function getDietTypeDisplayText(dietType: DietType): string {
  return dietTypeDisplayText[dietType];
}

export function getDietTypeDisplayEmoji(dietType: DietType): string {
  return dietTypeDisplayEmoji[dietType];
}
