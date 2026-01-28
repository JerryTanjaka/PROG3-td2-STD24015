package school.hei.prog3td2.model;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    private final Map<String, Map<UnitType, Map<UnitType, Double>>> rules = new HashMap<>();

    public UnitConverter() {
        addRule("Tomate", UnitType.KG, UnitType.PCS, 10.0);
        addRule("Laitue", UnitType.KG, UnitType.PCS, 2.0);
        addRule("Chocolat", UnitType.KG, UnitType.PCS, 10.0);
        addRule("Chocolat", UnitType.KG, UnitType.L, 2.5);
        addRule("Poulet", UnitType.KG, UnitType.PCS, 8.0);
        addRule("Beurre", UnitType.KG, UnitType.PCS, 4.0);
        addRule("Beurre", UnitType.KG, UnitType.L, 5.0);
    }

    private void addRule(String ingredient, UnitType from, UnitType to, double ratio) {
        rules.computeIfAbsent(ingredient, k -> new HashMap<>())
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(to, ratio);

        rules.get(ingredient)
                .computeIfAbsent(to, k -> new HashMap<>())
                .put(from, 1 / ratio);
    }

    public double convert(String ingredient, double quantity, UnitType from, UnitType to) {
        if (from == to) return quantity;
        Map<UnitType, Map<UnitType, Double>> ingRules = rules.get(ingredient);
        if (ingRules == null || !ingRules.containsKey(from) || !ingRules.get(from).containsKey(to)) {
            throw new RuntimeException("Conversion impossible pour " + ingredient + " : " + from + " -> " + to);
        }
        return quantity * ingRules.get(from).get(to);
    }
}