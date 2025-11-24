import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Zullaikha Zulzahrin
 */
public class Compile2 extends Application {
    
    Stage window;

    private Stage OpenCartStage;
    private ListView<foods> menuList;
    private ListView<foods> cartList;
    private Label totalLabel;
    private ObservableList<foods> menuItems;
    private ObservableList<foods> cartItems;

    private Stage OpenPaymentStage;
    private TextField textField;
    private TextField textField_1;
    private TextArea textArea;

    private Stage receiptStage;
    private Stage ratingStage;


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Login Form");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        // Set background color
        BackgroundFill backgroundFill = new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        grid.setBackground(background);

        Text welcomeTxt = new Text("SpiceFood Ordering System");
        welcomeTxt.setFont(Font.font("Tahoma", FontWeight.LIGHT, 25));
        grid.add(welcomeTxt, 0, 0, 2, 1);

        Label lblUser = new Label("Username");
        grid.add(lblUser, 0, 1);

        TextField txtUser = new TextField();
        txtUser.setPromptText("Username");
        grid.add(txtUser, 1, 1);

        Label lblPassword = new Label("Password");
        grid.add(lblPassword, 0, 2);

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Password");
        grid.add(pwBox, 1, 2);

        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> handleLogin(txtUser.getText(), pwBox.getText()));
        grid.add(loginBtn, 1, 3);

        Scene scene = new Scene(grid, 400, 300);
        window.setScene(scene);
        window.show();
    }

    private void handleLogin(String username, String password) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        OpenCartStage();
    }

    private void OpenCartStage() {
        
        OpenCartStage = new Stage();
        OpenCartStage.setTitle("Add To Cart!");

        BorderPane borderpane = new BorderPane();
        borderpane.setPadding(new Insets(10, 50, 10, 50));

        menuList = new ListView<>();
        cartList = new ListView<>();
        totalLabel = new Label("Total: RM 0.00");

        menuItems = FXCollections.observableArrayList(
                new foods("Nasi Lemak", 18.00),
                new foods("Sushi", 20.00),
                new foods("Butter Chicken", 13.00),
                new foods("Brownies", 9.00),
                new foods("Watermelon Juice", 7.00)
        );

        cartItems = FXCollections.observableArrayList();

        Button btn1 = new Button();
        btn1.setText("Add To Cart");
        btn1.setOnAction(e -> showItemDetails());

        Button btn2 = new Button();
        btn2.setText("Checkout");
        btn2.setOnAction(e -> notification("Checkout", "Order placed successfully!"));

        menuList.setCellFactory(param -> new ListCell<foods>() {
            protected void updateItem(foods item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - RM" + item.getPrice());
                }
            }
        });

        cartList.setCellFactory(param -> new ListCell<foods>() {
            protected void updateItem(foods item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - RM" + item.getPrice() + " - " + item.getQuantity() + "x");
                }
            }
        });

        menuList.setItems(menuItems);
        cartList.setItems(cartItems);

        HBox buttons = new HBox();
        buttons.getChildren().addAll(btn1, btn2);

        VBox cart = new VBox();
        cart.getChildren().addAll(cartList, totalLabel);

        borderpane.setLeft(menuList);
        borderpane.setCenter(buttons);
        borderpane.setRight(cart);

        Scene scene = new Scene(borderpane, 800, 300);
        OpenCartStage.setScene(scene);
        OpenCartStage.show();

        window.close();
    }

    private void showItemDetails() {
        foods selectedItem = menuList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Stage itemDetailsStage = new Stage();
            itemDetailsStage.initModality(Modality.APPLICATION_MODAL);
            itemDetailsStage.setTitle("Item Details");

            VBox itemDetailsLayout = new VBox(10);
            itemDetailsLayout.setPadding(new Insets(20, 50, 50, 50));

            Label nameLabel = new Label("Name: " + selectedItem.getName());
            Label priceLabel = new Label("Price: RM" + selectedItem.getPrice());

            Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
            Label quantityLabel = new Label("Quantity:");

            Button btn1 = new Button("Add to Cart");
            btn1.setOnAction(e -> {
                int quantity = quantitySpinner.getValue();
                selectedItem.setQuantity(quantity);
                cartItems.add(selectedItem);
                updateTotal();
                itemDetailsStage.close();
            });

            itemDetailsLayout.getChildren().addAll(nameLabel, priceLabel, quantityLabel, quantitySpinner, btn1);

            Scene itemDetailsScene = new Scene(itemDetailsLayout, 300, 200);
            itemDetailsStage.setScene(itemDetailsScene);
            itemDetailsStage.showAndWait();
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        totalLabel.setText("Total: RM" + String.format("%.2f", total));
    }

    private void notification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        cartItems.clear();
        updateTotal();

        OpenPaymentStage();
    }

    private static class foods {
        private final String name;
        private final double price;
        private int quantity;

        public foods(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    private void OpenPaymentStage() {
        
        OpenPaymentStage = new Stage();
        OpenPaymentStage.setTitle("Order Form");

        Label lblFillInDetails = new Label("Payment");
        lblFillInDetails.setFont(new javafx.scene.text.Font("Serif", 16));

        Label lblName = new Label("Name");

        textField = new TextField();
        textField.setPrefWidth(176);

        Label lblAddress = new Label("Tel No");

        textField_1 = new TextField();
        textField_1.setPrefWidth(176);

        Label lblAdress_1 = new Label("Address");

        textArea = new TextArea();
        textArea.setPrefSize(236, 85);

        Label lblAdress_2 = new Label("Select Payment");

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add("Online Banking");
        comboBox.getItems().add("Touch 'n Go eWallet");
        comboBox.getItems().add("Credit/debit card");

        Button btnCancel = new Button("Cancel");

        Button btnConfirm = new Button("Confirm");

        VBox container = new VBox();
        container.getChildren().addAll(
                lblFillInDetails, lblName, textField, lblAddress, textField_1, lblAdress_1,
                textArea, lblAdress_2, comboBox, btnCancel, btnConfirm
        );
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setSpacing(10);

        StackPane root = new StackPane();
        root.getChildren().add(container);

        Scene scene = new Scene(root, 420, 500);
        OpenPaymentStage.setScene(scene);
        OpenPaymentStage.show();

        OpenCartStage.close();
        
        btnCancel.setOnAction(e -> OpenCartStage.show());
        btnConfirm.setOnAction(e -> checkout());
    }

    private void checkout() {
        generateReceipt();
    }

    private void generateReceipt() {
        
        VBox receiptLayout = new VBox(10);
        receiptLayout.setPadding(new Insets(20));

        Label receiptHeader = new Label("Receipt");
        receiptLayout.getChildren().add(receiptHeader);

        // Add item details to receipt
        for (foods item : cartItems) {
            Label itemLabel = new Label(item.getName() + "\t x" + item.getQuantity() + "\t RM" + String.format("%.2f", item.getPrice() * item.getQuantity()));
            receiptLayout.getChildren().add(itemLabel);
        }

        // Add total to receipt
        double total = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        totalLabel = new Label("\nTotal: RM" + String.format("%.2f", total));
        receiptLayout.getChildren().add(totalLabel);

        // Show the receipt in a separate window
        showReceiptWindow(receiptLayout);
        
    }

    private void showReceiptWindow(VBox receiptLayout) {
        
        OpenPaymentStage.close();
        
        receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Receipt");

        VBox finalLayout = new VBox(receiptLayout);

        Button rateButton = new Button("Rate Your Experience");
        rateButton.setOnAction(e -> askForRating());

        finalLayout.getChildren().add(rateButton);

        Scene receiptScene = new Scene(finalLayout, 300, 450);
        receiptStage.setScene(receiptScene);
        receiptStage.showAndWait();
        
    }

    private void askForRating() {
        
        receiptStage.close();
        
        VBox ratingLayout = new VBox(10);
        ratingLayout.setPadding(new Insets(20));

        Label ratingLabel = new Label("Rate Your Experience (1-5):");
        TextField ratingField = new TextField();
        Button submitButton = new Button("Submit");
        ratingLayout.getChildren().addAll(ratingLabel, ratingField, submitButton);

        showRatingWindow(ratingLayout, ratingField, submitButton);

    }

    private void showRatingWindow(VBox ratingLayout, TextField ratingField, Button submitButton) {
        ratingStage = new Stage();

        ratingStage.initModality(Modality.APPLICATION_MODAL);
        ratingStage.setTitle("Rate Your Experience");

        // Handle submission when the button is clicked
        submitButton.setOnAction(e -> {
            handleRatingSubmission(ratingField.getText(), ratingStage);
        });

        Scene ratingScene = new Scene(ratingLayout, 300, 150);
        ratingStage.setScene(ratingScene);
        ratingStage.showAndWait();  
        
    }

    private void handleRatingSubmission(String rating, Stage ratingStage) {
        
        try {
            
            int userRating = Integer.parseInt(rating);
            if (isValidRating(userRating)) {
                notification("Thank You", "Thank you for your rating: " + userRating);
                ratingStage.close();
            } 
            else {
                notification("Invalid Rating", "Please enter a rating between 1 and 5.");
            }
        } 
        catch (NumberFormatException e) {
            notification("Invalid Rating", "Please enter a valid numeric rating.");
        }
        
    }

    private boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
        
    }
}
