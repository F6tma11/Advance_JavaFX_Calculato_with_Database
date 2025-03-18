/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import com.fatma.calcolator.common.DBConnection;
import com.fatma.calculator.entity.calculation;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.ConditionalFeature.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author elfrd
 */
public class CalculationController implements Initializable {

    /**
     * Initializes the controller class.
     */
    String query = "";
    DBConnection conn = new DBConnection();
    PreparedStatement statment = null;
    Connection connection = null;
    ResultSet resultSet = null;

    @FXML
    private TableColumn<calculation, Integer> col_id;

    @FXML
    private TableColumn<calculation, String> col_name;

    @FXML
    private TableColumn<calculation, Integer> col_result;

    @FXML
    private TableView<calculation> table_calculation;

    @FXML
    private TextField txt_name;

    @FXML
    private TextField txt_numbers;

    @FXML
    private TextField txt_search;

    private long result;

    private long number;

    private long number2;

    private String operator = "";
    calculation calc = new calculation();

    ObservableList<calculation> calculationsList = FXCollections.observableArrayList();
    ObservableList<calculation> calculationsData = FXCollections.observableArrayList();
//---------------------------------Show and Search about data----------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            connection = conn.connect();
            loadData();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CalculationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CalculationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void searchCalculation() throws SQLException {
        String searchText = txt_search.getText().trim(); // الحصول على النص المدخل للبحث

        String query = "SELECT * FROM processes WHERE name LIKE ? OR result LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, "%" + searchText + "%");
        preparedStatement.setString(2, "%" + searchText + "%");

        ResultSet resultSet = preparedStatement.executeQuery();

        calculationsList.clear(); // تنظيف القائمة قبل إضافة البيانات الجديدة

        while (resultSet.next()) {
            calculation calc = new calculation();
            calc.setId(resultSet.getInt("id"));
            calc.setName(resultSet.getString("name"));
            calc.setResult(resultSet.getInt("result"));
            calculationsList.add(calc);
        }
    }

    private void loadData() throws SQLException {
        txt_search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchCalculation();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        refreshData();
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_result.setCellValueFactory(new PropertyValueFactory<>("result"));
    }

    private void refreshData() throws SQLException {
        calculationsList.clear();
        query = "SELECT * FROM processes";
        statment = connection.prepareStatement(query);
        resultSet = statment.executeQuery();

        while (resultSet.next()) {
            calculation calc = new calculation();
            calc.setId(resultSet.getInt("id"));
            calc.setName(resultSet.getString("name"));
            calc.setResult(resultSet.getInt("result"));
            calculationsList.add(calc);
        }
        table_calculation.setItems(calculationsList);

    }
//--------------------------------------Get Data and Edite--------------------------------
    int index = -1;
    int edit_id;

    @FXML
    private void getSelectedCol() {
        index = table_calculation.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        edit_id = Integer.parseInt(col_id.getCellData(index).toString());
        txt_name.setText(col_name.getCellData(index).toString());
        txt_numbers.setText(col_result.getCellData(index).toString());
    }

    @FXML
    void btn_edit(ActionEvent event) throws SQLException {
        query = "UPDATE processes SET name=? ,result=? WHERE id=?";
        statment = connection.prepareStatement(query);
        statment.setString(1, txt_name.getText().toString());
        statment.setInt(2, Integer.parseInt(txt_numbers.getText().toString()));
        statment.setInt(3, edit_id);
        statment.executeUpdate();
        loadData();
    }

//-----------------------------------Button Event-------------------------------------------    
    @FXML
    void number(ActionEvent event) {
        String number = ((Button) event.getSource()).getText();
        txt_numbers.setText(txt_numbers.getText().toString() + number);
    }

    @FXML
    void operator(ActionEvent event) {
        String operation = ((Button) event.getSource()).getText();
        if (!operation.equals("=")) {
            if (!operator.equals("")) {
                return;
            }
            operator = operation;
            number = Long.parseLong(txt_numbers.getText());
            txt_numbers.setText("");
        } else {
            if (operator.equals("")) {
                return;
            }
            number2 = Long.parseLong(txt_numbers.getText());
            txt_numbers.setText("");

            calculation(number, number2, operator);
            operator = "";
        }

    }

//------------------------------Save Calculation-------------------------------------------------
    @FXML
    void saveCalculation(ActionEvent event) throws SQLException {

        query = "INSERT INTO processes ( name, result) VALUES (?,?)";
        statment = connection.prepareStatement(query);
        statment.setString(1, txt_name.getText().toString());
        statment.setString(2, txt_numbers.getText().toString());
        statment.executeUpdate();
        loadData();

    }

    @FXML
    void write_dot(ActionEvent event) {

    }

    @FXML
    void clear(ActionEvent event) {
        txt_numbers.setText("");
    }
//------------------------------Operations--------------------------------------

    @FXML
    void trigonometricOperation(ActionEvent event) {
        String operation = ((Button) event.getSource()).getText();
        double value = Double.parseDouble(txt_numbers.getText());
        double result = 0;

        switch (operation) {
            case "sin":
                result = Math.sin(Math.toRadians(value));
                break;
            case "cos":
                result = Math.cos(Math.toRadians(value));
                break;
            case "tan":
                result = Math.tan(Math.toRadians(value));
                break;
            case "sinh":
                result = Math.sinh(Math.toRadians(value));
                break;
            case "cosh":
                result = Math.cosh(Math.toRadians(value));
                break;
            case "tanh":
                result = Math.tanh(Math.toRadians(value));
                break;
            case "log":
                result = Math.log10(value);
                break;
            case "√":
                result = Math.sqrt(value);
                break;
        }

        txt_numbers.setText(String.valueOf(result));
    }

    private void calculation(long n1, long n2, String op) {
        switch (op) {
            case "+":
                txt_numbers.setText(n1 + n2 + "");

                break;
            case "-":
                txt_numbers.setText(n1 - n2 + "");
                break;
            case "*":
                txt_numbers.setText(n1 * n2 + "");
                break;
            case "/":
                txt_numbers.setText(n1 / n2 + "");
                break;
            case "%":
                txt_numbers.setText(n1 % n2 + "");
                break;
            case "^":
                txt_numbers.setText(Math.pow(n1, n2) + "");
                break;

        }
    }
}
