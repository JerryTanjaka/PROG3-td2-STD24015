package school.hei.prog3td2.model;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    // La map est statique pour être accessible par la méthode statique convert
    private static final Map<String, Map<Unit, Map<Unit, Double>>> rules = new HashMap<>();

    // Bloc statique : s'exécute automatiquement au chargement de la classe
    static {
        // Règles selon le tableau du PDF (Base 1 KG)
        addRule("Tomate", Unit.KG, Unit.PCS, 10.0);

        addRule("Laitue", Unit.KG, Unit.PCS, 2.0);

        addRule("Chocolat", Unit.KG, Unit.PCS, 10.0);
        addRule("Chocolat", Unit.KG, Unit.L, 2.5);

        addRule("Poulet", Unit.KG, Unit.PCS, 8.0);

        addRule("Beurre", Unit.KG, Unit.PCS, 4.0);
        addRule("Beurre", Unit.KG, Unit.L, 5.0);
    }

    // Méthode utilitaire pour ajouter une règle et son inverse (ex: KG->PCS et PCS->KG)
    private static void addRule(String ingredient, Unit from, Unit to, double ratio) {
        rules.computeIfAbsent(ingredient, k -> new HashMap<>())
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(to, ratio);

        // Ajout automatique de l'inverse : si 1kg = 10pcs, alors 1pc = 0.1kg
        rules.get(ingredient)
                .computeIfAbsent(to, k -> new HashMap<>())
                .put(from, 1 / ratio);
    }

    /**
     * Convertit une quantité d'un ingrédient d'une unité vers une autre.
     */
    public static double convert(String ingredient, double quantity, Unit from, Unit to) {
        // Si les unités sont identiques, pas de conversion nécessaire
        if (from == to) {
            return quantity;
        }

        Map<Unit, Map<Unit, Double>> ingRules = rules.get(ingredient);

        if (ingRules == null || !ingRules.containsKey(from) || !ingRules.get(from).containsKey(to)) {
            throw new RuntimeException("Conversion impossible pour l'ingrédient '" + ingredient +
                    "' de " + from + " vers " + to);
        }

        double ratio = ingRules.get(from).get(to);
        return quantity * ratio;
    }
}