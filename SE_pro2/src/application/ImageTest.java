package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ImageTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        String imageUrl = "file:///C://Users//ACG///OneDrive//Desktop//SE_pro2//src//application//charity.png/";
        Image image = new Image(imageUrl, true);
        ImageView imageView = new ImageView(image);

        if (image.isError()) {
            System.out.println("Error loading image: " + image.getException());
        } else {
            System.out.println("Image loaded successfully: " + image.getWidth() + "x" + image.getHeight());
        }

        Scene scene = new Scene(new javafx.scene.layout.StackPane(imageView), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
