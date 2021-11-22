/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enigma;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Notsawo
 */
public class main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        AnchorPane root = new AnchorPane();
        
        FXMLLoader loader = new FXMLLoader();
        try{
            loader.setLocation(getClass().getResource("/enigma/view.fxml"));
            root = (AnchorPane) loader.load();
        }catch(IOException e){
           e.printStackTrace();
        }
        
        // Largeur : width, Hauteur : heigth
        Scene scene = new Scene(root, 930, 440);
        
        primaryStage.setTitle("Enigma");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
    */
    public static void main(String[] args) {
        launch(args);
    }
    
}
