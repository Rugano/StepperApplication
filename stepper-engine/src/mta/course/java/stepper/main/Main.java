package mta.course.java.stepper.main;

import mta.course.java.stepper.ui.MenuUI;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
        MenuUI menu = new MenuUI();
        menu.start();
/*
            FileInputStream a = new FileInputStream("C:\\Users\\gur_S\\OneDrive\\Desktop\\University\\Java\\Stepper_impl\\stepper-design-suggestion\\stepper-engine\\src\\mta\\course\\java\\stepper\\resources\\Stepper.xml");
            Stepper stepper = new Stepper(a);



            boolean stepper_answer = stepper.stepperInitiate();



            System.out.println(stepper.flow_list.get(0));


            stepper.executeAFlow(stepper.flow_list.get(0));

 */
    }
}
