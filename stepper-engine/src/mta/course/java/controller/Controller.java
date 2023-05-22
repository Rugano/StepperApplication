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

public class Controller {

    private Stepper stepper;

    @FXML
    private Button LoadFileButton;
    @FXML
    private TextField XMLFilePathTextField;
    @FXML
    private VBox FlowDefinitionButtonsVBox;
    @FXML
    private VBox SelectedFlowDetailsVBox;

    public Controller(){
        this.stepper = new Stepper();
    };

    private void clickOneOfTheFlows(ActionEvent event) {
        // Perform the desired action when the button is pressed
        // You can access the button that triggered the event using the event's source
        Button clickedButton = (Button) event.getSource();

        // Example action: Print the text of the clicked button
        System.out.println("Button pressed: " + clickedButton.getText());

        // Perform additional actions based on the clicked button
        TextArea SelectedFlowDetailsTextArea = new TextArea();
        SelectedFlowDetailsVBox.getChildren().clear();
        // ...
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
                XMLFilePathTextField.setStyle("-fx-backgroud-color: red;");
                //put it in red (not valid)
            }
            else {
                filePath = selectedFile.getAbsolutePath();
                XMLFilePathTextField.setText(filePath);
                //put it in regular color (valid)
                XMLFilePathTextField.setStyle("-fx-background-color: blue;");
                XMLFilePathTextField.setStyle("-fx-opacity: 0.9");
                if (processXMLFilePath(filePath))
                    generateButtons();
                else {
                    TextArea SummaryOfStepperTextArea = new TextArea(stepper.summary);
                    FlowDefinitionButtonsVBox.getChildren().add(SummaryOfStepperTextArea);
                }
            }
        }
    }

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

    //Also generating the text area for summary of stepper
    private void generateButtons(){
        FlowDefinitionButtonsVBox.getChildren().clear(); // Clear existing buttons

        for (int i = 0; i < stepper.flow_list.size(); i++) {
            Button button = new Button(stepper.flow_list.get(i).getName());
            button.setOnAction(event -> {
                // Action to be performed when the button is pressed
                button.setOnAction(this::clickOneOfTheFlows);});
            FlowDefinitionButtonsVBox.getChildren().add(button);
        }
    }

}
