module com.finance.finance_lab_pbo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.finance.finance_lab_pbo to javafx.fxml;
    exports com.finance.finance_lab_pbo;
}