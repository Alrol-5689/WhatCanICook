import json
from pathlib import Path

INPUT_FILE = Path("FoodData_Central_foundation_food_json_2025-12-18.json")
OUTPUT_FILE = Path("ingredients.json")
MAX_INGREDIENTS = None

NUTRIENT_NAMES = {
    "carbs_100g": {
        "Carbohydrate, by difference",
        "Carbohydrate",
    },
    "protein_100g": {
        "Protein",
    },
    "fat_100g": {
        "Total lipid (fat)",
        "Total fat (NLEA)",
        "Fatty acids, total saturated",
    },
    "fiber_100g": {
        "Fiber, total dietary",
        "Total dietary fiber (AOAC 2011.25)",
        "Dietary fiber",
    },
}


def normalize_name(name: str) -> str:
    name = " ".join(name.split()).strip()
    if "," in name:
        name = name.split(",", 1)[0].strip()
    return name


def get_amount_by_names(food_nutrients: list[dict], valid_names: set[str]) -> float | None:
    for nutrient_entry in food_nutrients:
        nutrient = nutrient_entry.get("nutrient") or {}
        nutrient_name = nutrient.get("name")
        if nutrient_name in valid_names:
            amount = nutrient_entry.get("amount")
            if isinstance(amount, (int, float)):
                return round(float(amount), 2)
    return None


def build_ingredient(food: dict) -> dict | None:
    description = food.get("description")
    if not isinstance(description, str) or not description.strip():
        return None

    name = normalize_name(description)
    if len(name) < 2 or len(name) > 80:
        return None

    food_nutrients = food.get("foodNutrients") or []
    if not isinstance(food_nutrients, list):
        return None

    carbs = get_amount_by_names(food_nutrients, NUTRIENT_NAMES["carbs_100g"])
    protein = get_amount_by_names(food_nutrients, NUTRIENT_NAMES["protein_100g"])
    fat = get_amount_by_names(food_nutrients, NUTRIENT_NAMES["fat_100g"])
    fiber = get_amount_by_names(food_nutrients, NUTRIENT_NAMES["fiber_100g"])

    if all(value is None for value in (carbs, protein, fat, fiber)):
        return None

    return {
        "name": name,
        "carbs_100g": carbs,
        "protein_100g": protein,
        "fat_100g": fat,
        "fiber_100g": fiber,
    }


def load_foods() -> list[dict]:
    with INPUT_FILE.open("r", encoding="utf-8") as file:
        data = json.load(file)

    if isinstance(data, list):
        return data

    if isinstance(data, dict):
        for key in ("FoundationFoods", "foundationFoods", "foods"):
            foods = data.get(key)
            if isinstance(foods, list):
                return foods

    raise ValueError("No se encontró una lista de alimentos válida en el JSON")


def main() -> None:
    if not INPUT_FILE.exists():
        print(f"No se encontró el archivo: {INPUT_FILE}")
        return

    try:
        foods = load_foods()
    except (json.JSONDecodeError, ValueError) as error:
        print(f"Error leyendo el JSON: {error}")
        return

    ingredients: list[dict] = []
    seen_names: set[str] = set()
    total_foods = 0

    for food in foods:
        total_foods += 1
        ingredient = build_ingredient(food)
        if ingredient is None:
            continue

        normalized_key = ingredient["name"].lower()
        if normalized_key in seen_names:
            continue

        seen_names.add(normalized_key)
        ingredients.append(ingredient)

        if MAX_INGREDIENTS is not None and len(ingredients) >= MAX_INGREDIENTS:
            break

    with OUTPUT_FILE.open("w", encoding="utf-8") as file:
        json.dump(ingredients, file, ensure_ascii=False, indent=2)

    print(f"Alimentos leídos: {total_foods}")
    print(f"Ingredientes guardados: {len(ingredients)}")
    print(f"Archivo generado: {OUTPUT_FILE.resolve()}")


if __name__ == "__main__":
    main()