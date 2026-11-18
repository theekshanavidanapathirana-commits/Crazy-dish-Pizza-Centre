package model;
import java.util.ArrayList;

public class CustomPizzaItem extends OrderItem {
    private String size;
    private double sizeExtra;
    private ArrayList<Ingredient> customIngredients;

    public CustomPizzaItem(BasePizzaMenu basePizza) {
        super(basePizza.getTitle(), basePizza.getBasicPrice());
        this.size = "Regular";
        this.sizeExtra = 0.0;
        this.customIngredients = new ArrayList<>();
    }

    public void setSize(String size, double extraCost) {
        this.size = size;
        this.sizeExtra = extraCost;
    }

    public void clearIngredients() {
        this.customIngredients.clear();
    }

    public void addIngredient(Ingredient ing) {
        this.customIngredients.add(ing);
    }

    public ArrayList<Ingredient> getCustomIngredients() {
        return customIngredients;
    }

    public String getSize() { return size; }

    @Override
    public double calculateTotal() {
        double total = getBasePrice() + sizeExtra;
        for (Ingredient ing : customIngredients) {
            total += ing.getPrice();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getItemName()).append(" [").append(size).append("] - LKR ").append(calculateTotal());
        if (!customIngredients.isEmpty()) {
            sb.append(" (+ ");
            for (Ingredient i : customIngredients) sb.append(i.getName()).append(", ");
            sb.setLength(sb.length() - 2);
            sb.append(")");
        }
        return sb.toString();
    }
}
