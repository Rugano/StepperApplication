package mta.course.java.stepper.ui;
import mta.course.java.stepper.stepper.Stepper;

import javax.xml.bind.JAXBException;
import java.util.Scanner;

public class MenuUI {
    private final Scanner scanner;
    private Stepper stepper;

    public MenuUI() {
        scanner = new Scanner(System.in);
        this.stepper = new Stepper();
        MenuUI menu = this;


    }

    public void start() throws JAXBException {
        boolean good = false;
        int answer;
        boolean initiated = false;
        String xml_file_path = null;
        while (true) {
            displayMenu();
            int choice = -1;
            if (scanner.hasNextInt())
                choice = scanner.nextInt();
            else {
                System.out.println("Please enter a number");
                scanner.nextLine();
                continue;
            }
            scanner.nextLine(); // clear input buffer
            switch (choice) {
                case 1:
                    //HERE I need to construct the stepper and Initiate!
                    System.out.println("Please enter the XML file path");
                    xml_file_path = scanner.nextLine();
                    if (!xml_file_path.endsWith(".xml")) {
                        System.out.println("File must end with .xml. Please try again.");
                        continue;
                    }
                    else {
                        initiated = stepper.start(xml_file_path);
                        continue;
                    }
                case 2:
                    // Display the list of available flows
                    if (initiated) {
                        System.out.println("Available flows:\n");
                        while(true){
                            System.out.println("0. Go back to main menu");
                            System.out.println(stepper.showFlows());
                            answer = -1;
                            if (scanner.hasNextInt())
                                answer = scanner.nextInt();
                            else {
                                System.out.println("Please enter a number");
                                scanner.nextLine();
                                continue;
                            }
                            if (answer > stepper.flow_list.size() || answer < 0){
                                System.out.println("Please enter a valid number");
                                continue;
                            }
                            else if (answer == 0)
                                break;
                            else
                                System.out.println(stepper.flow_list.get(answer - 1));
                        }
                    } else {
                        System.out.println("Please initiate the stepper first");
                        continue;
                    }
                    continue;
                case 3:
                    //ACTUAL EXECUTION (need to open a menu here (There might be more than 1 flow...)
                    //show a menu and then execute the flow that was asked for...
                    if (initiated) {
                        while (true) {
                            System.out.println("0. Go back to main menu");
                            System.out.println(stepper.showFlows());
                            answer = -1;
                            if (scanner.hasNextInt())
                                answer = scanner.nextInt();
                            else {
                                System.out.println("Please enter a number");
                                scanner.nextLine();
                                continue;
                            }
                            if (answer > stepper.flow_list.size() || answer < 0) {
                                System.out.println("Please enter a valid number");
                                continue;
                            } else if (answer == 0)
                                break;
                            else
                                //execute
                                    stepper.executeAFlow(stepper.flow_list.get(answer-1));
                            continue;
                        }
                    }
                    else
                        System.out.println("Please initiate the stepper first");
                    continue;
                case 4:
                    while (true) {
                        System.out.println("0. Go back to main menu");
                        stepper.showPast();
                        answer = -1;
                        if (scanner.hasNextInt())
                            answer = scanner.nextInt();
                        else {
                            System.out.println("Please enter a number");
                            scanner.nextLine();
                            continue;
                        }
                            if (answer == 0){
                                break;
                            }
                            else if(answer > stepper.db.list.size() || answer < 0){
                                System.out.println("Please enter a valid number");
                                continue;
                            }
                            else{
                                stepper.db.print4(stepper.db.list.get(answer -1).getKey()); //Printing info through UUID
                                //Check if meant for UUID or just a number..
                                continue;
                            }
                    }
                            continue;
                case 5:
                    stepper.showStats();
                            break;
                case 6:
                    System.out.println("Exiting program...");
                    return;
                default:
                     System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    private void displayMenu() {
        System.out.println("Please select an option:");
        System.out.println("1. Enter the full path of the XML file");
        System.out.println("2. Show Flows"); //Opens a list of all of the flows!
        System.out.println("3. Execute Flows"); //Opens a list of all the flows
        System.out.println("4. Show information about the past"); //Change
        System.out.println("5. Statistics");
        System.out.println("6. Exit");
    }
}
