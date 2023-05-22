package mta.course.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mta.course.java.stepper.stepper.Stepper;

import java.io.File;

public class DemoController {

    private Stepper stepper;

    @FXML
    private Button LoadFileButton;
    @FXML
    private TextField XMLFilePathTextField;
    @FXML
    private VBox FlowDefinitionButtons;

    public DemoController(){
        this.stepper = new Stepper();
    };

    private boolean processXMLFilePath(String filePath) {
        // TODO: Add your code here to process the XML file path
        // For example, you can print the file path
        boolean answer;
        try {
            answer = this.stepper.start(filePath);
        } catch (Exception e){
            System.out.println("Do whatever there's an exception.." +e.getMessage());
            return false;
        }
        return answer;
    }

    @FXML
    void clickMeButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");

        Stage stage = (Stage) LoadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        String filePath;
        if (selectedFile != null) {
            if(!selectedFile.getName().endsWith(".xml")) {
                filePath = selectedFile.getAbsolutePath();
                XMLFilePathTextField.setText(filePath);
                XMLFilePathTextField.setStyle("-fx-background-color: red;");
                //put it in red (not valid)
            }
            else {
                filePath = selectedFile.getAbsolutePath();
                XMLFilePathTextField.setText(filePath);
                //put it in regular color (valid)
                XMLFilePathTextField.setStyle("-fx-background-color: white;");
            }
            // Call a method to process the XML file path
            //I think i need to do this only if user presses a button
            processXMLFilePath(filePath);
        }
    }

    private void generateButtons(){
        FlowDefinitionButtons.getChildren().clear(); // Clear existing buttons

        for (int i = 0; i < stepper.flow_list.size(); i++) {
            Button button = new Button(stepper.flow_list.get(i).getName());
            // Set any additional properties or event handlers for the buttons as needed
            FlowDefinitionButtons.getChildren().add(button);
        }
        TextArea SummaryOfStepperTextArea = new TextArea(stepper.summary);
        FlowDefinitionButtons.getChildren().add(SummaryOfStepperTextArea);
    }



}