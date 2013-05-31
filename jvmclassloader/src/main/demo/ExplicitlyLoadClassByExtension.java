package demo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java program to demonstrate How ClassLoader works in Java,
 * in particular about visibility principle of ClassLoader.
 *
 * @author Javin Paul
 */

public class ExplicitlyLoadClassByExtension {

    public static void main(String args[]) {
        try {
            //printing ClassLoader of this class

            System.out.println("-------------");
            System.out.println("ExplicitlyLoadClassByExtension.getClass().getClassLoader() : "
                    + ExplicitlyLoadClassByExtension.class.getClassLoader());

            //trying to explicitly load this class again using Extension class loader
            System.out.println("ExplicitlyLoadClassByExtension.getClass().getClassLoader().getParent() : "
                    + ExplicitlyLoadClassByExtension.class.getClassLoader().getParent());
            Class.forName("demo.ExplicitlyLoadClassByExtension", true
                    ,  ExplicitlyLoadClassByExtension.class.getClassLoader().getParent());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExplicitlyLoadClassByExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}