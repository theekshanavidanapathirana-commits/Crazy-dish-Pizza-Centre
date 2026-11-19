# Crazy-dish-Pizza-Centre

Crazy Dish Pizza Centre is a Java Swing desktop application for pizza ordering and topping inventory management. Customers can customize pizzas, place orders, and print receipts. Administrators can update topping prices and stock quantities.

## Features

### Customer

- Register an account and sign in.
- Browse the pizza menu and add or remove pizzas from the cart.
- Choose a Regular or Large pizza and add available toppings.
- View the order total in LKR.
- Check out with cash on delivery or a simulated credit or debit card payment.
- View and print the order receipt.

### Administrator

- View ingredient prices and stock quantities.
- Add or restock ingredients and update their prices.
- See low-stock alerts when an ingredient has fewer than five units remaining.

The application checks topping availability at checkout, updates inventory after a successful order, and records the sale in `sales_report.txt`.

## Technologies Used

| Technology | Purpose |
| --- | --- |
| Java 21 | Application logic |
| Java Swing | Desktop user interface |
| Apache Maven | Project build |
| Local text files | Account, inventory, and sales data |

## Getting Started

Install **JDK 21** and **Apache Maven**, then open a terminal in the repository root and run these commands in order.

```sh
cd CrazyDishPizzaCentre
mvn compile
java -cp target/classes gui.PizzaWebAppSimulator
```

Run the application from `CrazyDishPizzaCentre` so it can find the menu images and local data files. The application opens in a desktop window.

## Trying the Application

- **Customer** — Create an account through the registration form using a 10-digit phone number. Add a pizza to the cart and choose **Cash on Delivery** to try checkout.
- **Administrator** — Sign in with the demo username `admin` and password `admin123` to open the inventory dashboard.

Use sample details when trying the application. Account passwords are stored in plain text, and card payments are simulated.

## Project Structure

| Path | Purpose |
| --- | --- |
| `CrazyDishPizzaCentre/pom.xml` | Maven build configuration |
| `CrazyDishPizzaCentre/src/main/java/gui/` | Swing screens and application entry point |
| `CrazyDishPizzaCentre/src/main/java/model/` | Pizza, ingredient, order, user, and payment classes |
| `CrazyDishPizzaCentre/src/main/java/service/` | Billing and payment interfaces |
| `CrazyDishPizzaCentre/src/main/java/exception/` | Authentication and stock exceptions |
| `CrazyDishPizzaCentre/src/main/java/util/` | Local file storage |
| `CrazyDishPizzaCentre/images/` | Pizza menu images |
| `CrazyDishPizzaCentre/users.txt` | User accounts and roles |
| `CrazyDishPizzaCentre/inventory.txt` | Ingredient prices and stock quantities |
| `CrazyDishPizzaCentre/sales_report.txt` | Sales records |

## UML Diagrams

- [Class Diagram](Diagrams/ClassDiagram.png)
- [Use Case Diagram](Diagrams/UseCase_Diagram.png)
