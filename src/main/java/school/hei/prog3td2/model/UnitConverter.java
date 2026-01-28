package school.hei.prog3td2.model;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    // Variable d'instance (pas static)
    private final Map<String, Map<UnitType, Map<UnitType, Double>>> rules = new HashMap<>();

    public UnitConverter() {
        // TOMATE : 1 KG = 10 PCS
        add("Tomate", UnitType.KG, UnitType.PCS, 10.0);

        // LAITUE : 1 KG = 2 PCS
        add("Laitue", UnitType.KG, UnitType.PCS, 2.0);

        // CHOCOLAT : 1 KG = 10 PCS / 1 KG = 2.5 L
        add("Chocolat", UnitType.KG, UnitType.PCS, 10.0);
        add("Chocolat", UnitType.KG, UnitType.L, 2.5);

        // POULET : 1 KG = 8 PCS
        add("Poulet", UnitType.KG, UnitType.PCS, 8.0);

        // BEURRE : 1 KG = 4 PCS / 1 KG = 5.0 L
        add("Beurre", UnitType.KG, UnitType.PCS, 4.0);
        add("Beurre", UnitType.KG, UnitType.L, 5.0);
    }

    private void add(String ingredient, UnitType from, UnitType to, double ratio) {
        rules
                .computeIfAbsent(ingredient, k -> new HashMap<>())
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(to, ratio);

        // Inverse automatiquement (ex: si 1 KG = 10 PCS, alors 1 PCS = 0.1 KG)
        rules.get(ingredient)
                .computeIfAbsent(to, k -> new HashMap<>())
                .put(from, 1 / ratio);
    }

    public double convert(String ingredient, double quantity, UnitType from, UnitType to) {
        if (from == to) return quantity;

        Map<UnitType, Map<UnitType, Double>> ingredientRules = rules.get(ingredient);

        if (ingredientRules == null ||
                !ingredientRules.containsKey(from) ||
                !ingredientRules.get(from).containsKey(to)) {
            throw new RuntimeException("Conversion impossible pour " + ingredient + " : " + from + " → " + to);
        }

        return quantity * ingredientRules.get(from).get(to);
    }
}