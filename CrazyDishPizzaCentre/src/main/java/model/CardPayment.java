package model;
import service.PaymentStrategy;

public class CardPayment implements PaymentStrategy {
    // stored card number used to identify the last 4 digits on receipts
    private String cardNumber;
    public CardPayment(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public String processPayment(double amount) {
        return "Paid LKR " + amount + " via Credit/Debit Card (Ending: " + 
               (cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "XXXX") + ")";
    }
}
