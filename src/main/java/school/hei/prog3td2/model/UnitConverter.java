package school.hei.prog3td2.model;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    private final Map<String, Map<Unit, Map<Unit, Double>>> rules = new HashMap<>();

    public UnitConverter() {
        addRule("Tomate", Unit.KG, Unit.PCS, 10.0);
        addRule("Laitue", Unit.KG, Unit.PCS, 2.0);
        addRule("Chocolat", Unit.KG, Unit.PCS, 10.0);
        addRule("Chocolat", Unit.KG, Unit.L, 2.5);
        addRule("Poulet", Unit.KG, Unit.PCS, 8.0);
        addRule("Beurre", Unit.KG, Unit.PCS, 4.0);
        addRule("Beurre", Unit.KG, Unit.L, 5.0);
    }

    private void addRule(String ingredient, Unit from, Unit to, double ratio) {
        rules.computeIfAbsent(ingredient, k -> new HashMap<>())
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(to, ratio);

        rules.get(ingredient)
                .computeIfAbsent(to, k -> new HashMap<>())
                .put(from, 1 / ratio);
    }

    public double convert(String ingredient, double quantity, Unit from, Unit to) {
        if (from == to) return quantity;
        Map<Unit, Map<Unit, Double>> ingRules = rules.get(ingredient);
        if (ingRules == null || !ingRules.containsKey(from) || !ingRules.get(from).containsKey(to)) {
            throw new RuntimeException("Conversion impossible pour " + ingredient + " : " + from + " -> " + to);
        }
        return quantity * ingRules.get(from).get(to);
    }
}