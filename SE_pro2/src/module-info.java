module SE_pro2 {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql; // This includes ObservableList
	
	opens application to javafx.graphics, javafx.fxml;
}
