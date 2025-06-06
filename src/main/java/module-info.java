module com.finance.finance_labpbo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.finance.finance_labpbo to javafx.fxml;
    exports com.finance.finance_labpbo;
}