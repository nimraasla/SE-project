package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Main extends Application {
    private static final String dbUrl = "jdbc:mysql://localhost:3306/se?useSSL=false&serverTimezone=UTC";
    private static final String username = "root";
    private static final String password = "nimra868";

    @Override
    public void start(Stage primaryStage) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded!");

            try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                System.out.println("Database connected successfully!");

                Database db = new Database();
                User organizer = new Organizer("Nimra", "i221164@nu.edu.pk", "nimra", "organizer", db);

                Role(primaryStage, organizer, db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Role(Stage primaryStage, User org, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Button organizerButton = new Button("Organizer");
        organizerButton.getStyleClass().add("button");
        Button customerButton = new Button("Customer");
        customerButton.getStyleClass().add("button");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 4);
        grid.add(organizerButton, 0, 0);
        grid.add(customerButton, 0, 1);

        organizerButton.setOnAction(e -> OrganizerLogin(primaryStage, org, db));
        customerButton.setOnAction(e -> CustomerLogin(primaryStage, org, db));

        root.getChildren().add(grid);

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Role Selection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void OrganizerLogin(Stage primaryStage, User org, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("email-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Email");
        usernameField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("password-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        grid.add(emailLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 3);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 4);

        Button backButton = createBackButton(() -> Role(primaryStage, org, db));
        StackPane.setAlignment(backButton, Pos.TOP_LEFT); // Position back button at top-left
        StackPane.setMargin(backButton, new Insets(40, 0, 0, 10)); // Add some padding from edges

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            errorLabel.setText("");
            usernameField.setStyle(null);
            passwordField.setStyle(null);

            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty())
                    usernameField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                if (password.isEmpty())
                    passwordField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                errorLabel.setText("Fields should not be empty");
            } else {
                boolean foundUser = db.findOrganizer(username, password);
                if (!foundUser) {
                    errorLabel.setText("Either email is not found or password is incorrect.");
                } else {
                    User or = db.getOrganizer(username, password, db);
                    OrganizerDashBoard(primaryStage, or.getID(), or, db);
                }
            }
        });

        root.getChildren().addAll(grid, backButton); // Add grid first (centered), then back button (top-left)

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void CustomerLogin(Stage primaryStage, User org, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("email-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Email");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("password-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        Hyperlink signupLink = new Hyperlink("Don't have an account? Signup");
        signupLink.setStyle("-fx-text-fill: blue; -fx-font-size: 12;");

        grid.add(emailLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 3);
        grid.add(signupLink, 1, 4);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 5);

        Button backButton = createBackButton(() -> Role(primaryStage, org, db));
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(40, 0, 0, 10));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            errorLabel.setText("");
            usernameField.setStyle(null);
            passwordField.setStyle(null);

            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty())
                    usernameField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                if (password.isEmpty())
                    passwordField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                errorLabel.setText("Fields should not be empty");
            } else {
                boolean foundUser = db.findCustomer(username);
                if (!foundUser) {
                    errorLabel.setText("No email found");
                } else {
                    boolean correctPass = db.findCustomerPassword(password);
                    if (correctPass) {
                        User customer = db.getCustomer(username, password, db);
                        CustomerDashBoard(primaryStage, customer.getID(), customer, db);
                    } else {
                        errorLabel.setText("Incorrect password");
                    }
                }
            }
        });

        signupLink.setOnAction(e -> CustomerSignup(primaryStage, org, db));

        root.getChildren().addAll(grid, backButton);

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Customer Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void CustomerSignup(Stage primaryStage, User org, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("email-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("text-field");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("email-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("password-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field");

        Button signupButton = new Button("Sign Up");
        signupButton.getStyleClass().add("button");

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: blue; -fx-font-size: 12;");

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(signupButton, 1, 3);
        grid.add(loginLink, 1, 4);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 5);

        Button backButton = createBackButton(() -> CustomerLogin(primaryStage, org, db));
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(40, 0, 0, 10));

        signupButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            errorLabel.setText("");
            nameField.setStyle(null);
            emailField.setStyle(null);
            passwordField.setStyle(null);

            boolean hasError = false;
            if (name.isEmpty()) {
                nameField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                hasError = true;
            }
            if (email.isEmpty()) {
                emailField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                hasError = true;
            }
            if (password.isEmpty()) {
                passwordField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                hasError = true;
            }

            if (hasError) {
                errorLabel.setText("All fields are required");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                emailField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                errorLabel.setText("Invalid email format");
                return;
            }

            if (db.findCustomer(email)) {
                emailField.setStyle("-fx-border-color: red; -fx-border-width: 0.5;");
                errorLabel.setText("Email already registered");
                return;
            }

            boolean success = ((Organizer) org).addCustomer(name, email, password);

            if (success) {
                CustomerLogin(primaryStage, org, db);
            } else {
                errorLabel.setText("Failed to create account. Please try again.");
            }
        });

        loginLink.setOnAction(e -> CustomerLogin(primaryStage, org, db));

        root.getChildren().addAll(grid, backButton);

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Customer Signup");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void OrganizerDashBoard(Stage primaryStage, int org_ID, User org, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        // Main layout using VBox
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40)); // Padding to keep content away from edges
        mainLayout.setAlignment(Pos.TOP_CENTER); // Align content to the top center

        // Slideshow for Ongoing Events
        Label ongoingLabel = new Label("Ongoing Events");
        ongoingLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333333;");

        ArrayList<Event> ongoingEvents = db.getOngoingEvents(org_ID, db);
        HBox slideshowBox = new HBox();
        slideshowBox.setAlignment(Pos.CENTER);
        slideshowBox.setPrefHeight(200); // Fixed height for the slideshow
        slideshowBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox slideContent = new VBox(5); // To hold the image and event name
        slideContent.setAlignment(Pos.CENTER);

        ImageView slideshowImage = new ImageView();
        slideshowImage.setPreserveRatio(true); // Preserve the aspect ratio of the image
        slideshowImage.setSmooth(true); // Enable smoothing to improve quality
        slideshowImage.setCache(true); // Cache the image for better rendering performance

        Label eventNameLabel = new Label(); // Label to display the event name
        eventNameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333333;");

        slideContent.getChildren().addAll(slideshowImage, eventNameLabel);
        slideshowBox.getChildren().add(slideContent);

        // Preload images and determine the maximum width
        ArrayList<Image> preloadedImages = new ArrayList<>();
        double maxImageWidth = 0; // To store the maximum width of the images
        for (Event event : ongoingEvents) {
            try {
                Image image = new Image(event.getImageUrl(), true); // Load in background
                preloadedImages.add(image);
                // Update the maximum width
                if (image.getWidth() > maxImageWidth) {
                    maxImageWidth = image.getWidth();
                }
                System.out.println("Image for event " + event.getName() + ": width=" + image.getWidth() + ", height=" + image.getHeight());
            } catch (Exception ex) {
                System.out.println("Error preloading image for event: " + event.getName());
                preloadedImages.add(new Image("file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/placeholder.jpg"));
            }
        }

        // If no events, use the placeholder image to determine the width
        if (preloadedImages.isEmpty()) {
            Image placeholder = new Image("file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/placeholder.jpg");
            preloadedImages.add(placeholder);
            maxImageWidth = placeholder.getWidth();
            System.out.println("Placeholder image: width=" + placeholder.getWidth() + ", height=" + placeholder.getHeight());
        }

        // Set the slideshowBox width to the maximum image width
        slideshowBox.setPrefWidth(maxImageWidth);
        slideshowBox.setMaxWidth(maxImageWidth);
        slideshowImage.setFitWidth(maxImageWidth); // Match the image width to its native width
        // Let the height adjust based on the aspect ratio (due to setPreserveRatio(true))

        // Debugging: Check the size of ongoingEvents
        System.out.println("Number of ongoing events: " + ongoingEvents.size());
        for (Event event : ongoingEvents) {
            System.out.println("Event: " + event.getName() + ", Image URL: " + event.getImageUrl() + ", Date: " + event.getDate());
        }

        if (!ongoingEvents.isEmpty()) {
            // Ensure the slideshow cycles through the ongoing events
            Timeline slideshowTimeline = new Timeline();
            slideshowTimeline.setCycleCount(Timeline.INDEFINITE);
            final int[] currentIndex = {0};

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), e -> {
                try {
                    Event currentEvent = ongoingEvents.get(currentIndex[0]);
                    System.out.println("Displaying event: " + currentEvent.getName());

                    // Use preloaded image to avoid blinking
                    Image image = preloadedImages.get(currentIndex[0]);
                    slideshowImage.setImage(image);

                    // Update the event name label
                    eventNameLabel.setText(currentEvent.getName() + " (" + currentEvent.getDate() + ")");

                    // Move to the next event
                    currentIndex[0] = (currentIndex[0] + 1) % ongoingEvents.size();
                } catch (Exception ex) {
                    System.out.println("Exception in slideshow: " + ex.getMessage());
                    ex.printStackTrace();
                    slideshowImage.setImage(new Image("file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/placeholder.jpg"));
                    eventNameLabel.setText("Error loading event");
                }
            });

            slideshowTimeline.getKeyFrames().add(keyFrame);
            slideshowTimeline.play();

            // Set the initial slide
            try {
                Event firstEvent = ongoingEvents.get(0);
                slideshowImage.setImage(preloadedImages.get(0));
                eventNameLabel.setText(firstEvent.getName() + " (" + firstEvent.getDate() + ")");
            } catch (Exception ex) {
                System.out.println("Exception setting initial slide: " + ex.getMessage());
                slideshowImage.setImage(new Image("file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/placeholder.jpg"));
                eventNameLabel.setText("Error loading initial event");
            }
        } else {
            System.out.println("No ongoing events. Showing placeholder.");
            slideshowImage.setImage(new Image("file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/placeholder.jpg"));
            eventNameLabel.setText("No ongoing events.");
        }

        // Add the slideshow to the main layout
        mainLayout.getChildren().addAll(ongoingLabel, slideshowBox);

        // Position the main layout at the top of the StackPane
        StackPane.setAlignment(mainLayout, Pos.TOP_CENTER);

        Button backButton = createBackButton(() -> OrganizerLogin(primaryStage, org, db));
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(40, 0, 0, 10));

        root.getChildren().addAll(mainLayout, backButton); // Removed backgroundImage since CSS handles it

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Organizer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    void CustomerDashBoard(Stage primaryStage, int cust_ID, User customer, Database db) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        Label placeholderLabel = new Label("Customer Dashboard - Under Construction");
        placeholderLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Button backButton = createBackButton(() -> CustomerLogin(primaryStage, customer, db));
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(40, 0, 0, 10));

        root.getChildren().addAll(placeholderLabel, backButton);

        Scene scene = new Scene(root, 960, 540);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/application/role.css").toExternalForm());

        primaryStage.setTitle("Customer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createBackButton(Runnable onClick) {
        Button backButton = new Button();
        // Enhanced styling to remove rectangle
        backButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-border-width: 0; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0;" // Remove padding to fit only the image
        );
        backButton.setMinSize(20, 20); // Match the image size
        backButton.setPrefSize(20, 20); // Ensure button size matches image
        backButton.setOnAction(e -> onClick.run());

        try {
            String imagePath = "file:///C:/Users/ACG/OneDrive/Desktop/SE_pro2/src/application/left-arrow2.png";
            Image image = new Image(imagePath);
            ImageView leftArrow = new ImageView(image);
            leftArrow.setFitWidth(40);
            leftArrow.setFitHeight(40);
            leftArrow.setVisible(true);
            backButton.setGraphic(leftArrow);
            System.out.println("Image loaded successfully from: " + imagePath);
            System.out.println("Image width: " + image.getWidth() + ", height: " + image.getHeight());
        } catch (Exception e) {
            System.out.println("Warning: Could not load left-arrow.png, using text instead.");
            e.printStackTrace();
            backButton.setText("← Back");
        }

        return backButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}