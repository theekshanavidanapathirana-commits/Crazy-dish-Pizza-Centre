package model;

public enum BasePizzaMenu {
    MARGHERITA("Classic Margherita", 1200.0, "🍕", "Rich tomato sauce with melted mozzarella"),
    BBQ_CHICKEN("Smoky BBQ Chicken", 1650.0, "🍗", "Grilled chicken, onions & tangy BBQ glaze"),
    PEPPERONI_FEAST("Pepperoni Feast", 1800.0, "🥩", "Double pepperoni with extra cheese"),
    VEGGIE_SUPREME("Garden Veggie Supreme", 1400.0, "🫑", "Bell peppers, mushrooms, olives & onions");

    private final String title;
    private final double basicPrice;
    private final String icon;
    private final String description;

    BasePizzaMenu(String title, double basicPrice, String icon, String description) {
        this.title = title;
        this.basicPrice = basicPrice;
        this.icon = icon;
        this.description = description;
    }

    public String getTitle() {
        return title; 
    }
    public double getBasicPrice() {
        return basicPrice; 
    }
    public String getIcon() { 
        return icon; 
    }
    public String getDescription() {
        return description;
    }
}
