package lk.sjp.pizza.gui;

import lk.sjp.pizza.exception.AuthenticationException;
import lk.sjp.pizza.exception.OutOfStockException;
import lk.sjp.pizza.model.*;
import lk.sjp.pizza.service.PaymentStrategy;
import lk.sjp.pizza.util.DatabaseManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class PizzaWebAppSimulator extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private HashMap<String, User> userDB;
    private HashMap<String, Ingredient> inventoryDB;
    private User currentUser;
    private ArrayList<CustomPizzaItem> shoppingCart;

    private DefaultListModel<CustomPizzaItem> cartListModel;
    private JLabel lblCartTotal;
    private JTextArea txtAdminStockView;
    private JButton btnPhase2GoCart;

    private final Color DARK_BG = new Color(24, 24, 30);
    private final Color CRIMSON_RED = new Color(217, 4, 41);
    private final Color BRAND_ORANGE = new Color(255, 107, 0);
    private final Color GOLD_ACCENT = new Color(255, 183, 3);
    private final Color FRESH_GREEN = new Color(43, 147, 72);
    private final Color CREAM_BG = new Color(253, 248, 242);

    public PizzaWebAppSimulator() {
        userDB = DatabaseManager.loadUsers();
        inventoryDB = DatabaseManager.loadInventory();
        shoppingCart = new ArrayList<>();

        setTitle("Crazy Dish - WOOD-FIRED PIZZA");
        setSize(1020, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createAuthPanel(), "AUTH");
        mainContainer.add(createPhase1GalleryPanel(), "PHASE1");
        mainContainer.add(createPhase2SelectionPanel(), "PHASE2");
        mainContainer.add(createPhase3CartAndCheckoutPanel(), "PHASE3");
        mainContainer.add(createAdminDashboardPanel(), "ADMIN");

        add(mainContainer);
        cardLayout.show(mainContainer, "AUTH");
    }
    class GradientPanel extends JPanel {
        private Color c1, c2;
        public GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        private Color borderColor;
        public RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }
        public void setBorderColor(Color c) { this.borderColor = c; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
        });
        return btn;
    }

    private ImageIcon getPizzaImage(BasePizzaMenu pizza, int width, int height) {
        String filename = "images/margherita.jpg";
        if (pizza == BasePizzaMenu.BBQ_CHICKEN) filename = "images/bbq_chicken.jpg";
        else if (pizza == BasePizzaMenu.PEPPERONI_FEAST) filename = "images/pepperoni.jpg";
        else if (pizza == BasePizzaMenu.VEGGIE_SUPREME) filename = "images/veggie.jpg";

        File imgFile = new File(filename);
        if (imgFile.exists()) {
            ImageIcon raw = new ImageIcon(filename);
            Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    private JPanel createTopBanner(String titleText) {
        JPanel container = new JPanel(new BorderLayout());
        GradientPanel banner = new GradientPanel(DARK_BG, CRIMSON_RED);
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(14, 25, 14, 25));

        JLabel brand = new JLabel("CRAZY DISH WOOD-FIRED PIZZAS   |   " + titleText);
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        banner.add(brand, BorderLayout.WEST);

        JButton btnLogout = createStyledButton("Sign Out", new Color(40, 40, 45), Color.BLACK);
        btnLogout.addActionListener(e -> cardLayout.show(mainContainer, "AUTH"));
        banner.add(btnLogout, BorderLayout.EAST);

        JPanel promoBar = new JPanel();
        promoBar.setBackground(GOLD_ACCENT);
        JLabel promoTxt = new JLabel("HOT OFFER: Customize with 3+ Toppings & Enjoy Fresh Wood-Fired Taste in 25 Mins!");
        promoTxt.setFont(new Font("SansSerif", Font.BOLD, 12));
        promoTxt.setForeground(DARK_BG);
        promoBar.add(promoTxt);

        container.add(banner, BorderLayout.CENTER);
        container.add(promoBar, BorderLayout.SOUTH);
        return container;
    }
    private JPanel createAuthPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CREAM_BG);

        GradientPanel top = new GradientPanel(DARK_BG, CRIMSON_RED);
        top.setLayout(new GridLayout(2, 1));
        top.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel title = new JLabel("CRAZY DISH - WOOD-FIRED PIZZA", SwingConstants.CENTER);
        title.setForeground(GOLD_ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel sub = new JLabel("Craft Your Dream Pizza with Fresh Gourmet Ingredients", SwingConstants.CENTER);
        sub.setForeground(Color.WHITE);
        sub.setFont(new Font("SansSerif", Font.ITALIC, 15));

        top.add(title);
        top.add(sub);
        wrapper.add(top, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(1, 2, 35, 35));
        panel.setBackground(CREAM_BG);
        panel.setBorder(new EmptyBorder(35, 60, 45, 60));

        RoundedPanel loginBox = new RoundedPanel(25, Color.WHITE, new Color(230, 220, 210));
        loginBox.setLayout(new GridLayout(6, 1, 10, 10));
        loginBox.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblL = new JLabel("Sign In");
        lblL.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblL.setForeground(CRIMSON_RED);

        JTextField txtLoginUser = new JTextField();
        JPasswordField txtLoginPass = new JPasswordField();
        JButton btnLogin = createStyledButton("Sign In", BRAND_ORANGE, CRIMSON_RED);

        loginBox.add(lblL);
        loginBox.add(new JLabel("Username:"));
        loginBox.add(txtLoginUser);
        loginBox.add(new JLabel("Password:"));
        loginBox.add(txtLoginPass);
        loginBox.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String u = txtLoginUser.getText().trim();
            String p = new String(txtLoginPass.getPassword());
            try {
                if (!userDB.containsKey(u) || !userDB.get(u).getPassword().equals(p)) {
                    throw new AuthenticationException("Invalid Username or Password!");
                }
                currentUser = userDB.get(u);
                if (currentUser.getRole().equals("ADMIN")) {
                    refreshAdminPanel();
                    cardLayout.show(mainContainer, "ADMIN");
                } else {
                    shoppingCart.clear();
                    updateCartButtonCount();
                    cardLayout.show(mainContainer, "PHASE1");
                }
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        RoundedPanel signupBox = new RoundedPanel(25, Color.WHITE, new Color(230, 220, 210));
        signupBox.setLayout(new GridLayout(10, 1, 5, 5));
        signupBox.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblS = new JLabel("Registration");
        lblS.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblS.setForeground(FRESH_GREEN);

        JTextField txtRegName = new JTextField();
        JTextField txtRegPhone = new JTextField();
        JTextField txtRegUser = new JTextField();
        JPasswordField txtRegPass = new JPasswordField();
        JButton btnRegister = createStyledButton("Create An Account", FRESH_GREEN, new Color(30, 110, 50));

        signupBox.add(lblS);
        signupBox.add(new JLabel("Full Name:"));
        signupBox.add(txtRegName);
        signupBox.add(new JLabel("Phone Number:"));
        signupBox.add(txtRegPhone);
        signupBox.add(new JLabel("Enter Username:"));
        signupBox.add(txtRegUser);
        signupBox.add(new JLabel("Enter Password:"));
        signupBox.add(txtRegPass);
        signupBox.add(btnRegister);

        btnRegister.addActionListener(e -> {
            String name = txtRegName.getText().trim();
            String phone = txtRegPhone.getText().trim();
            String uname = txtRegUser.getText().trim();
            String pass = new String(txtRegPass.getPassword());

            if (name.isEmpty() || uname.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }
             String cleanPhone = phone.replaceAll("\\s+", "");
            if (!cleanPhone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Invalid Phone Number! Please enter exactly 10 digits.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDB.containsKey(uname)) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                return;
            }
            User newUser = new User(uname, pass, name, phone, "CUSTOMER");
            userDB.put(uname, newUser);
            DatabaseManager.saveUsers(userDB);
            currentUser = newUser;
            JOptionPane.showMessageDialog(this, "Welcome to Crazy Dish, " + name + "!");
            cardLayout.show(mainContainer, "PHASE1");
        });

        panel.add(loginBox);
        panel.add(signupBox);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }
    private JPanel createPhase1GalleryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CREAM_BG);
        panel.add(createTopBanner("Signature Pizza Showcase"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
        grid.setBackground(CREAM_BG);
        grid.setBorder(new EmptyBorder(15, 40, 15, 40));

        for (BasePizzaMenu pizza : BasePizzaMenu.values()) {
            RoundedPanel card = new RoundedPanel(25, Color.WHITE, new Color(235, 225, 215));
            card.setLayout(new BorderLayout(8, 8));
            card.setBorder(new EmptyBorder(12, 15, 12, 15));

            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { card.setBorderColor(BRAND_ORANGE); }
                @Override public void mouseExited(MouseEvent e) { card.setBorderColor(new Color(235, 225, 215)); }
            });

            JLabel badge = new JLabel("CHEF'S SPECIAL  |  4.9 / 5.0", SwingConstants.RIGHT);
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));
            badge.setForeground(BRAND_ORANGE);
            card.add(badge, BorderLayout.NORTH);

            ImageIcon pic = getPizzaImage(pizza, 260, 145);
            JLabel imgLbl = new JLabel(pic != null ? pic : new ImageIcon(), SwingConstants.CENTER);
            if (pic == null) imgLbl.setText(pizza.getTitle());

            String html = "<html><center><span style='font-size:14px; color:#18181E;'><b>" + pizza.getTitle() +
                    "</b></span><br><span style='color:#666666; font-size:11px;'>" + pizza.getDescription() + "</span></center></html>";
            JLabel titleLbl = new JLabel(html, SwingConstants.CENTER);

            card.add(imgLbl, BorderLayout.CENTER);
            card.add(titleLbl, BorderLayout.SOUTH);
            grid.add(card);
        }
        panel.add(grid, BorderLayout.CENTER);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nav.setBackground(CREAM_BG);
        nav.setBorder(new EmptyBorder(5, 40, 15, 40));

        JButton btnNext = createStyledButton("Explore Menu & Order Pizzas", BRAND_ORANGE, CRIMSON_RED);
        btnNext.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNext.addActionListener(e -> cardLayout.show(mainContainer, "PHASE2"));

        nav.add(btnNext);
        panel.add(nav, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPhase2SelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CREAM_BG);
        panel.add(createTopBanner("Select Pizzas & Add to Cart"), BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(4, 1, 14, 14));
        listPanel.setBackground(CREAM_BG);
        listPanel.setBorder(new EmptyBorder(10, 40, 10, 40));

        for (BasePizzaMenu pizza : BasePizzaMenu.values()) {
            RoundedPanel row = new RoundedPanel(20, Color.WHITE, new Color(230, 220, 210));
            row.setLayout(new BorderLayout(18, 10));
            row.setBorder(new EmptyBorder(8, 15, 8, 20));

            ImageIcon thumb = getPizzaImage(pizza, 115, 78);
            JLabel picLbl = new JLabel(thumb != null ? thumb : new ImageIcon());

            String infoHtml = "<html><span style='font-size:14px; color:#18181E;'><b>" + pizza.getTitle() + "</b></span> " +
                    "<span style='background:#FFF3CD; color:#856404; font-size:10px;'><b> [HOT & FRESH] </b></span><br>" +
                    "<span style='color:#555;'>" + pizza.getDescription() + "</span><br>" +
                    "<span style='color:#D90429; font-size:13px;'><b>Starting at LKR " + pizza.getBasicPrice() + "</b></span></html>";
            JLabel info = new JLabel(infoHtml);

            JButton btnAdd = createStyledButton("+ Add to Cart", FRESH_GREEN, new Color(30, 110, 50));
            btnAdd.addActionListener(e -> {
                shoppingCart.add(new CustomPizzaItem(pizza));
                updateCartButtonCount();
            });

            row.add(picLbl, BorderLayout.WEST);
            row.add(info, BorderLayout.CENTER);
            row.add(btnAdd, BorderLayout.EAST);
            listPanel.add(row);
        }
        panel.add(listPanel, BorderLayout.CENTER);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(CREAM_BG);
        nav.setBorder(new EmptyBorder(5, 40, 15, 40));

        JButton btnBack = createStyledButton("Back to Gallery", new Color(100, 100, 110), DARK_BG);
        btnPhase2GoCart = createStyledButton("Go to Cart & Customize (0 Items)", BRAND_ORANGE, CRIMSON_RED);

        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "PHASE1"));
        btnPhase2GoCart.addActionListener(e -> {
            refreshCartUI();
            cardLayout.show(mainContainer, "PHASE3");
        });

        nav.add(btnBack, BorderLayout.WEST);
        nav.add(btnPhase2GoCart, BorderLayout.EAST);
        panel.add(nav, BorderLayout.SOUTH);
        return panel;
    }

    private void updateCartButtonCount() {
        if (btnPhase2GoCart != null) {
            btnPhase2GoCart.setText("Go to Cart & Customize (" + shoppingCart.size() + " Items)");
        }
    }
    private JPanel createPhase3CartAndCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CREAM_BG);
        panel.add(createTopBanner("Shopping Cart, Customizer & Checkout"), BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout(20, 15));
        centerWrap.setBackground(CREAM_BG);
        centerWrap.setBorder(new EmptyBorder(10, 40, 10, 40));

        cartListModel = new DefaultListModel<>();
        JList<CustomPizzaItem> cartJList = new JList<>(cartListModel);
        cartJList.setFont(new Font("SansSerif", Font.BOLD, 14));
        cartJList.setFixedCellHeight(38);
        cartJList.setSelectionBackground(new Color(255, 224, 178));
        cartJList.setSelectionForeground(DARK_BG);
        centerWrap.add(new JScrollPane(cartJList), BorderLayout.CENTER);

        RoundedPanel rightPanel = new RoundedPanel(20, Color.WHITE, new Color(230, 220, 210));
        rightPanel.setLayout(new GridLayout(6, 1, 10, 10));
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        rightPanel.setPreferredSize(new Dimension(290, 0));

        JButton btnCustomize = createStyledButton("Customize Selected Pizza", BRAND_ORANGE, CRIMSON_RED);
        JButton btnRemove = createStyledButton("Remove Selected Item", new Color(180, 50, 50), CRIMSON_RED);
        JComboBox<String> cmbPayment = new JComboBox<>(new String[]{"Cash on Delivery", "Credit / Debit Card"});
        lblCartTotal = new JLabel("Total Due: LKR 0.0", SwingConstants.CENTER);
        lblCartTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCartTotal.setForeground(CRIMSON_RED);

        JButton btnPlaceOrder = createStyledButton("Confirm & Print Receipt", FRESH_GREEN, new Color(30, 110, 50));

        rightPanel.add(btnCustomize);
        rightPanel.add(btnRemove);
        rightPanel.add(new JLabel("Select Payment Method:"));
        rightPanel.add(cmbPayment);
        rightPanel.add(lblCartTotal);
        rightPanel.add(btnPlaceOrder);
        centerWrap.add(rightPanel, BorderLayout.EAST);
        panel.add(centerWrap, BorderLayout.CENTER);

        btnCustomize.addActionListener(e -> {
            CustomPizzaItem selected = cartJList.getSelectedValue();
            if (selected == null && !shoppingCart.isEmpty()) {
                cartJList.setSelectedIndex(0);
                selected = cartJList.getSelectedValue();
            }
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
                return;
            }
            openCustomizationDialog(selected);
        });

        btnRemove.addActionListener(e -> {
            int idx = cartJList.getSelectedIndex();
            if (idx != -1) {
                shoppingCart.remove(idx);
                updateCartButtonCount();
                refreshCartUI();
            }
        });

        btnPlaceOrder.addActionListener(e -> {
            if (shoppingCart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
                return;
            }
            try {
                HashMap<String, Integer> req = new HashMap<>();
                for (CustomPizzaItem item : shoppingCart) {
                    for (Ingredient ing : item.getCustomIngredients()) {
                        req.put(ing.getName(), req.getOrDefault(ing.getName(), 0) + 1);
                    }
                }
                for (String ingName : req.keySet()) {
                    Ingredient dbIng = inventoryDB.get(ingName);
                    if (dbIng.getStockQty() < req.get(ingName)) {
                        throw new OutOfStockException("Insufficient stock for: " + ingName);
                    }
                }

                double grandTotal = 0;
                StringBuilder summary = new StringBuilder();
                for (CustomPizzaItem item : shoppingCart) {
                    grandTotal += item.calculateTotal();
                    summary.append(item.getItemName()).append(" (").append(item.getSize()).append("), ");
                }

                PaymentStrategy payment;
            if (cmbPayment.getSelectedIndex() == 1) {
               String cardNo = JOptionPane.showInputDialog(this, "Enter 16-digit Card Number:");
                   if (cardNo == null) return;
    
    
               String cleanCard = cardNo.replaceAll("\\s+", "");
               if (!cleanCard.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Invalid Card Number! Please enter exactly 16 digits.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                  return;
            }
               payment = new CardPayment(cleanCard);
            } else {
               payment = new CashPayment();
            }
                String receiptMsg = payment.processPayment(grandTotal);

                for (String ingName : req.keySet()) {
                    Ingredient dbIng = inventoryDB.get(ingName);
                    dbIng.setStockQty(dbIng.getStockQty() - req.get(ingName));
                }
                DatabaseManager.saveInventory(inventoryDB);

                String orderId = "ORD-" + (System.currentTimeMillis() % 100000);
                DatabaseManager.logSale(orderId, currentUser.getFullName(), summary.toString(), grandTotal, receiptMsg);

                showPrintableReceiptDialog(orderId, new ArrayList<>(shoppingCart), grandTotal, receiptMsg);
                shoppingCart.clear();
                updateCartButtonCount();
                refreshCartUI();
                cardLayout.show(mainContainer, "PHASE1");
            } catch (OutOfStockException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Inventory Alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(CREAM_BG);
        bottom.setBorder(new EmptyBorder(0, 40, 15, 40));
        JButton btnBack = createStyledButton("<- Add More Pizzas", new Color(100, 100, 110), DARK_BG);
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "PHASE2"));
        bottom.add(btnBack);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }
    private void showPrintableReceiptDialog(String orderId, ArrayList<CustomPizzaItem> items, double total, String payInfo) {
        JDialog dialog = new JDialog(this, "Official Invoice - " + orderId, true);
        dialog.setSize(460, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        StringBuilder bill = new StringBuilder();
        bill.append("==============================================\n");
        bill.append("           CRAZY DISH PIZZA SYSTEM            \n");
        bill.append("==============================================\n");
        bill.append("Order ID  : ").append(orderId).append("\n");
        bill.append("Date      : ").append(dateStr).append("\n");
        bill.append("Customer  : ").append(currentUser.getFullName()).append("\n");
        bill.append("Phone     : ").append(currentUser.getPhone()).append("\n");
        bill.append("----------------------------------------------\n");

        int count = 1;
        for (CustomPizzaItem item : items) {
            bill.append(count++).append(". ").append(item.getItemName()).append(" [").append(item.getSize()).append("]\n");
            for (Ingredient ing : item.getCustomIngredients()) {
                bill.append("   + ").append(ing.getName()).append(" (LKR ").append(ing.getPrice()).append(")\n");
            }
            bill.append("   Subtotal: LKR ").append(item.calculateTotal()).append("\n\n");
        }
        bill.append("----------------------------------------------\n");
        bill.append("GRAND TOTAL : LKR ").append(total).append("\n");
        bill.append("PAYMENT     : ").append(payInfo).append("\n");
        bill.append("==============================================\n");

        JTextArea txtArea = new JTextArea(bill.toString());
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtArea.setEditable(false);
        txtArea.setMargin(new Insets(10, 10, 10, 10));
        dialog.add(new JScrollPane(txtArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnPrint = createStyledButton("Print Receipt", BRAND_ORANGE, CRIMSON_RED);
        JButton btnClose = createStyledButton("Close", DARK_BG, Color.BLACK);

        btnPrint.addActionListener(e -> {
            try { txtArea.print(); } catch (PrinterException ex) { JOptionPane.showMessageDialog(dialog, "Print Error: " + ex.getMessage()); }
        });
        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openCustomizationDialog(CustomPizzaItem pizzaItem) {
        if (inventoryDB == null || inventoryDB.isEmpty()) {
            inventoryDB = DatabaseManager.loadInventory();
        }
        JDialog dialog = new JDialog(this, "Customize: " + pizzaItem.getItemName(), true);
        dialog.setSize(430, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        top.setBackground(CREAM_BG);
        JRadioButton rbReg = new JRadioButton("Regular (+ LKR 0)", pizzaItem.getSize().equals("Regular"));
        JRadioButton rbLarge = new JRadioButton("Large (+ LKR 500)", pizzaItem.getSize().equals("Large"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbReg);
        bg.add(rbLarge);
        top.add(new JLabel("Select Size: "));
        top.add(rbReg);
        top.add(rbLarge);
        dialog.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 1));
        center.setBorder(BorderFactory.createTitledBorder("Select Gourmet Extra Toppings"));
        HashMap<String, JCheckBox> boxes = new HashMap<>();
        for (Ingredient ing : inventoryDB.values()) {
            String txt = ing.getName() + " (+ LKR " + ing.getPrice() + ") [Stock: " + ing.getStockQty() + "]";
            JCheckBox cb = new JCheckBox(txt);
            for (Ingredient existing : pizzaItem.getCustomIngredients()) {
                if (existing.getName().equals(ing.getName())) cb.setSelected(true);
            }
            if (ing.getStockQty() <= 0) cb.setEnabled(false);
            boxes.put(ing.getName(), cb);
            center.add(cb);
        }
        dialog.add(new JScrollPane(center), BorderLayout.CENTER);

        JButton btnSave = createStyledButton("Save Customization", FRESH_GREEN, new Color(30, 110, 50));
        btnSave.addActionListener(e -> {
            if (rbLarge.isSelected()) pizzaItem.setSize("Large", 500.0);
            else pizzaItem.setSize("Regular", 0.0);
            pizzaItem.clearIngredients();
            for (String name : boxes.keySet()) {
                if (boxes.get(name).isSelected()) pizzaItem.addIngredient(inventoryDB.get(name));
            }
            refreshCartUI();
            dialog.dispose();
        });
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void refreshCartUI() {
        cartListModel.clear();
        double total = 0;
        for (CustomPizzaItem item : shoppingCart) {
            cartListModel.addElement(item);
            total += item.calculateTotal();
        }
        lblCartTotal.setText("Total Due: LKR " + total);
    }

    private JPanel createAdminDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CREAM_BG);
        panel.add(createTopBanner("ADMIN PORTAL: Inventory & Low-Stock Alert System"), BorderLayout.NORTH);

        txtAdminStockView = new JTextArea();
        txtAdminStockView.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtAdminStockView.setEditable(false);
        txtAdminStockView.setMargin(new Insets(15, 20, 15, 20));
        panel.add(new JScrollPane(txtAdminStockView), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setBackground(Color.WHITE);
        JTextField txtName = new JTextField(12);
        JTextField txtPrice = new JTextField(6);
        JTextField txtQty = new JTextField(5);
        JButton btnUpdate = createStyledButton("Add / Restock Item", FRESH_GREEN, new Color(30, 110, 50));

        controls.add(new JLabel("Ingredient Name:"));
        controls.add(txtName);
        controls.add(new JLabel("Price (LKR):"));
        controls.add(txtPrice);
        controls.add(new JLabel("Stock Qty:"));
        controls.add(txtQty);
        controls.add(btnUpdate);

        btnUpdate.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                int qty = Integer.parseInt(txtQty.getText().trim());
                if (name.isEmpty() || qty < 0) throw new NumberFormatException();
                inventoryDB.put(name, new Ingredient(name, price, qty));
                DatabaseManager.saveInventory(inventoryDB);
                refreshAdminPanel();
                JOptionPane.showMessageDialog(this, "Inventory Updated!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid values!");
            }
        });

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAdminPanel() {
        StringBuilder sb = new StringBuilder("=== CURRENT INGREDIENT STOCK ===\n\n");
        StringBuilder alerts = new StringBuilder();
        for (Ingredient ing : inventoryDB.values()) {
            sb.append(String.format("%-22s | LKR %-8.2f | Stock: %d\n", ing.getName(), ing.getPrice(), ing.getStockQty()));
            if (ing.getStockQty() < 5) {
                alerts.append("[!] LOW STOCK ALERT: ").append(ing.getName()).append(" has only ").append(ing.getStockQty()).append(" units left!\n");
            }
        }
        sb.append("\n========================================\n");
        sb.append(alerts.length() > 0 ? alerts.toString() : "[OK] All ingredient stock levels are healthy.");
        txtAdminStockView.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PizzaWebAppSimulator().setVisible(true));
    }
}
