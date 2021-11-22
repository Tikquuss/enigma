/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enigma;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.layout.GridPane;
import java.util.Arrays;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import javafx.util.Callback;

import javafx.event.ActionEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * FXML Controller class
 *
 * @author Notsawo
 */
public class ViewController implements Initializable {
    /**
     * Initializes the controller class.
     */ 
    @FXML
    private GridPane gridpaneGeneral;
    
    @FXML
    private Button configRotorsButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private Button etapeSuivanteButton;

    @FXML
    private TextField text2EncryptTextfield;

    @FXML
    private TextField text2DecryptTextfield;

    @FXML
    private TextField cleTextfield;

    @FXML
    private TableView<String[]> reflecteur;

    @FXML
    private TableView<String[]> rotor3;

    @FXML
    private TableView<String[]> rotor2;

    @FXML
    private TableView<String[]> rotor1;

    @FXML
    private TableView<String[]> entree;
    
    public static int J = -1;
    public static final int L = 26;
    private static final String NOHEADER_CLASS = "noheader" ;

    public static final String[][] data_reflecteur = {{"+25", "+23", "+21", "+19", "+17", "+15", "+13", "+11", "+9", "+7", "+5", "+3", "+1", "-1", "-3", "-5", "-7", "-9", "-11", "-13", "-15", "-17", "-19", "-21", "-23", "-25"}};
    public static final String[][] data_rotors3 = {{"+12", "-1", "+23", "+10", "+2", "+14", "+5", "-5", "+9", "-2", "-13", "+10", "-2", "-8", "+10", "-6", "+6", "-16", "+2", "-1", "-17", "-5", "-14", "-9", "-20", "-10"}, {"+1", "+16", "+5", "+17", "+20", "+8", "-2", "+2", "+14", "+6", "+2", "-5", "-12", "-10", "+9", "+10", "+5", "-9", "+1", "-14", "-2", "-10", "-6", "+13", "-10", "-23"}};
    //public static String[][] data_rotors3_tmp = data_rotors3;
    public static String[][] data_rotors3_tmp = new String[2][L];
    public static final String[][] data_rotors2 = {{"+25", "+7", "+17", "-3", "+13", "+19", "+12", "+3", "-1", "+11", "+5", "-5", "-7", "+10", "-2", "+1", "-2", "+4", "-17", "-8", "-16", "-18", "-9", "-1", "-22", "-16"},{"+3", "+17", "+22", "+18", "+16", "+7", "+5", "+1", "-7", "+16", "-3", "+8", "+2", "+9", "+2", "-5", "-1", "-13", "-12", "-17", "-11", "-4", "+1", "-10", "-19", "-25"}};
    //public static String[][] data_rotors2_tmp = data_rotors2;
    public static String[][] data_rotors2_tmp = new String[2][L];
    public static final String[][] data_rotors1 = {{"+17", "+4", "+19", "+21", "+7", "+11", "+3", "-5", "+7", "+9", "-10", "+9", "+17", "+6", "-6", "-2", "-4", "-7", "-12", "-5", "+3", "+4", "-21", "-16", "-2", "-21"},{"+10", "+21", "+5", "-17", "+21", "-4", "+12", "+16", "+6", "-3", "+7", "-7", "+4", "+2", "+5", "-7", "-11", "-17", "-9", "-6", "-9", "-19", "+2", "-3", "-21", "-4"}};
    //public static String[][] data_rotors1_tmp =  data_rotors1;
    public static String[][] data_rotors1_tmp = new String[2][L];
    public static String[][] alphabet = {{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}};
   
    public static String[][] CLE = new String[3][3]; // {{"R3", "G", "+7"}, {"R1", "D", "-6"}, {"R2", "D", "+5"}} for (R3, G, +7)(R1, D, -6)(R2, D, +5)
    public static int[] N = {0,0,0}; // Nombre de fois que le rotor tourne, dans leur ordre d'apparition dans la clé
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gridpaneGeneral.setGridLinesVisible(true);
        //gridpaneGeneral.setHgap(20);
        //gridpaneGeneral.setVgap(20);
        
        DataInit(false, false);
        reflecteur.getStyleClass().add(NOHEADER_CLASS);
        rotor1.getStyleClass().add(NOHEADER_CLASS);
        rotor2.getStyleClass().add(NOHEADER_CLASS);
        rotor3.getStyleClass().add(NOHEADER_CLASS);    
        entree.getStyleClass().add(NOHEADER_CLASS);
       
        helpButtons(true);
    } 
    
    void DataInit(boolean re_init, boolean with_tmp) {
        /*
        String[][] data1 = new String[1][L];
        for(int i=0; i<L;i++){
            data1[0][i] = "0"+Integer.toString(i);
        }
        data_reflecteur = data1;
        
        String[][] data2 = new String[2][L];
        for(int i=0; i<L;i++){
            data2[0][i] = "0"+Integer.toString(i);
            data2[1][i] = "1"+Integer.toString(i);
        }
        data_rotors1 = data2;
        data_rotors2 = data2;
        data_rotors3 = data2;
        */
        initRotorReflecteur(reflecteur, data_reflecteur, re_init);
        if (with_tmp){
            initRotorReflecteur(rotor1, data_rotors1_tmp, re_init);
            initRotorReflecteur(rotor2, data_rotors2_tmp, re_init);
            initRotorReflecteur(rotor3, data_rotors3_tmp, re_init);        
        }else {
            initRotorReflecteur(rotor1, data_rotors1, re_init);
            initRotorReflecteur(rotor2, data_rotors2, re_init);
            initRotorReflecteur(rotor3, data_rotors3, re_init); 
        }
        initRotorReflecteur(entree, alphabet, re_init);    
    }
    
    public void initRotorReflecteur(TableView<String[]> tableview, String[][] data, boolean re_init){
        if(re_init){
            tableview.getItems().clear();
        }else{
            for(int i=0; i<L;i++){
                TableColumn<String[],String> column = new TableColumn();
                column.setText(Integer.toString(i));
                tableview.getColumns().addAll(column);
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                        String[] x = p.getValue();
                        if (x != null && x.length>0) {
                            J = (J + 1)%L;
                            return new SimpleStringProperty(x[J]);
                        } else {
                            return new SimpleStringProperty("N/A");
                        }
                    }
                });
            }
            J = -1;
        }
        tableview.getItems().addAll(Arrays.asList(data)); 
    }
    
    void helpButtons(boolean b) {
        text2EncryptTextfield.setDisable(b);
        text2DecryptTextfield.setDisable(b);
        encryptButton.setDisable(b);
        decryptButton.setDisable(b);
        etapeSuivanteButton.setDisable(b);
    }
        
    public String[] rotateArray(String[] array, String step){
        String[] s = step.contains("+") ? step.split("\\+") : step.split("-");
        String signe = String.valueOf(step.charAt(0));
        int step_int = Integer.parseInt(s[1]);
        if ((!signe.equals("+") & !signe.equals("-")) ||  step_int < 0){
            throw new RuntimeException("");
        }
        //https://www.developpez.net/forums/d851304/java/general-java/langage/modulo-negatif-operateur/
        int l = array.length;
        if (signe.equals("-")){
            step_int = -step_int;
            step_int = step_int + l;
        }        
        String[] tmp = new String[l];
        for(int i=0; i<l;i++){
            tmp[i] = array[(i+step_int)%l];
        }
        return tmp;
    }
    
    void rotationInitiale() {
        for (String[] CLE1 : CLE) {
            switch (CLE1[0]) {
                case "R1":
                    data_rotors1_tmp[0] = rotateArray(data_rotors1[0], CLE1[2]);
                    data_rotors1_tmp[1] = rotateArray(data_rotors1[1], CLE1[2]);
                    initRotorReflecteur(rotor1, data_rotors1_tmp, true);
                    break;
                case "R2":
                    data_rotors2_tmp[0] = rotateArray(data_rotors2[0], CLE1[2]);
                    data_rotors2_tmp[1] = rotateArray(data_rotors2[1], CLE1[2]);
                    initRotorReflecteur(rotor2, data_rotors2_tmp, true);
                    break;
                case "R3":
                    data_rotors3_tmp[0] = rotateArray(data_rotors3[0], CLE1[2]);
                    data_rotors3_tmp[1] = rotateArray(data_rotors3[1], CLE1[2]);
                    initRotorReflecteur(rotor3, data_rotors3_tmp, true);
                    break;
                default:
                    break;
            }
        }
    }
    
    
    @FXML
    void configureCleRotors(ActionEvent event) {
        String sample = "(R3,G,+7)(R1,D,-6)(R2,D,+5)";
        //sample = "(R3,G,+0)(R1,D,-0)(R2,D,+0)";
        sample = "(R1,G,+0)(R2,D,-0)(R3,D,+0)";
        if ("Ré-Configurer Rotors".equals(configRotorsButton.getText())){
            configRotorsButton.setText("Configurer Rotors");
            cleTextfield.setDisable(false);
            cleTextfield.setText("Clé. Exemple : "+sample);
            helpButtons(true);
            DataInit(true, false);
            return;
        } 
        String cle  = cleTextfield.getText();
        //cle = "(R3,G,+7)(R1,D,-6)(R2,D,+5)";
        // (R3, G, +7) (R1, D, -6) (R2, D, +5)
        if(cle.isEmpty()){
            cleTextfield.setText("Impossible, clé vide, Exemple : "+sample);
            return;
        }
        String pat = "\\(R[1-3],[GD],[-\\+][0-9]+\\)\\(R[1-3],[GD],[-\\+][0-9]+\\)\\(R[1-3],[GD],[-\\+][0-9]+\\)";
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(cle);
        if(!matcher.find()){
            cleTextfield.setText("Clé Invalide, Exemple : "+sample);
            return;
        }
        
        String[][] CLE_tmp = new String[3][3];
        String[] s = cle.split("\\)\\(");
        String[] c = new String[3];
        c = s[0].split("\\(")[1].split(","); // R3 G +7
        for(int i = 0; i < c.length; i++){CLE_tmp[0][i] = c[i];}
        c = s[1].split(","); // R1 D -6
        for(int i = 0; i < c.length; i++){CLE_tmp[1][i] = c[i];}
        c = s[2].split("\\)")[0].split(","); // R2 D +5
        for(int i = 0; i < c.length; i++){CLE_tmp[2][i] = c[i];}
        
        // Verifier qu'on a tout les 3 R (R1, R2, R3)
        char x = CLE_tmp[0][0].charAt(1), y = CLE_tmp[1][0].charAt(1), z = CLE_tmp[2][0].charAt(1);
        if(x == y || x == z || y == z){
            cleTextfield.setText("Clé Invalide, Exemple : "+ sample);
            return;
        }
        
        String xx;
        for(int i = 0; i < CLE_tmp.length; i++){
            xx = CLE_tmp[i][2];
            xx = xx.contains("+") ? xx.split("\\+")[1] : xx.split("-")[1];
            // -26 <= xx < +26
            if(Math.abs(Integer.parseInt(xx)) > 26){
                cleTextfield.setText("Clé Invalide, Exemple : "+sample);
                return;
            }
        }
        
        for(int i = 0; i < CLE_tmp.length; i++){
            for(int j = 0; j < CLE_tmp[i].length; j++){
                CLE[i][j] = CLE_tmp[i][j];
            }
        }
        
        cleTextfield.setText(cle);
        cleTextfield.setDisable(true);
        configRotorsButton.setText("Ré-Configurer Rotors");
        helpButtons(false);
        rotationInitiale();
        etapeSuivanteButton.setDisable(true);
       
    }
    
    void tourner(int ordre) {
        N[ordre] = (N[ordre]+1)%L;
        String r = CLE[ordre][0];
        String step = CLE[ordre][1].equals("G") ? "-1" :"+1";
        switch (r) {
            case "R1":
                data_rotors1_tmp[0] = rotateArray(data_rotors1[0], step);
                data_rotors1_tmp[1] = rotateArray(data_rotors1[1], step);
                initRotorReflecteur(rotor1, data_rotors1_tmp, true);
                break;
            case "R2":
                data_rotors2_tmp[0] = rotateArray(data_rotors2[0], step);
                data_rotors2_tmp[1] = rotateArray(data_rotors2[1], step);
                initRotorReflecteur(rotor2, data_rotors2_tmp, true);
                break;
            case "R3":
                data_rotors3_tmp[0] = rotateArray(data_rotors3[0], step);
                data_rotors3_tmp[1] = rotateArray(data_rotors3[1], step);
                initRotorReflecteur(rotor3, data_rotors3_tmp, true);
                break;
            default:
                break;
        }
        //if(ordre < 2 & N[ordre] == L-1){
        //    N[ordre+1] = (N[ordre+1]+1)%L;
        //}
     
    }
        
    void rotation() {
        int ordre = 0;
        tourner(ordre);
        if(N[ordre] == L-1){
            ordre = ordre + 1; //1
            tourner(ordre);
            if(N[ordre] == L-1){
                ordre = ordre + 1; //2
                tourner(ordre);
            }
        }
    }
    
    
    int split_step(String step) {
        String[] s = step.contains("+") ? step.split("\\+") : step.split("-");
        String signe = String.valueOf(step.charAt(0));
        int step_int = Integer.parseInt(s[1]);
        if (signe.equals("-")){
            step_int = -step_int;  
        } 
        return step_int;
    }
        
    String aller_retour(String text){
        int i, v, line;
        i = Arrays.asList(alphabet[0]).indexOf(text);
        //entree.getItems().get(i)
        line = 0;
        v = split_step(data_rotors1_tmp[1-line][i]);
        i = (i+v)%L;
        v = split_step(data_rotors2_tmp[1-line][i]);
        i = (i+v)%L;
        v = split_step(data_rotors3_tmp[1-line][i]);
        i = (i+v)%L;
        v = split_step(data_reflecteur[0][i]);
        i = (i+v)%L;
        line = 1;
        v = split_step(data_rotors3_tmp[1-line][i]);
        i = (i+v)%L;
        v = split_step(data_rotors2_tmp[1-line][i]);
        i = (i+v)%L;
        v = split_step(data_rotors1_tmp[1-line][i]);
        i = (i+v)%L;
        rotation();
        return alphabet[0][i];
    }
    
    @FXML
    private void encryptext(ActionEvent event) {
        String text  = text2EncryptTextfield.getText();
        if(text.isEmpty()){
            text2EncryptTextfield.setText("Text Invalide");
            return;
        }
        if(text.length() > 1){
            text2EncryptTextfield.setText("Un seul Caractere");
            return;
        }
        if(Arrays.asList(alphabet[0]).indexOf(text) == -1){
            text2EncryptTextfield.setText("Caracter Invalide");
            return;
        }
        String s = aller_retour(text);
        text2EncryptTextfield.setText(s);
        text2EncryptTextfield.setDisable(true);
        encryptButton.setDisable(true);
        etapeSuivanteButton.setDisable(false);
        
    }

    @FXML
    private void decryptext(ActionEvent event) {
        String text  = text2DecryptTextfield.getText();
        if(text.isEmpty()){
            text2DecryptTextfield.setText("Text Invalide");
            return;
        }
        if(text.length() > 1){
            text2DecryptTextfield.setText("Un seul Caractere");
            return;
        }
        if(Arrays.asList(alphabet[0]).indexOf(text) == -1){
            text2DecryptTextfield.setText("Caracter Invalide");
            return;
        }
        String s = aller_retour(text);
        text2DecryptTextfield.setText(s);
        text2DecryptTextfield.setDisable(true);
        decryptButton.setDisable(true);
        etapeSuivanteButton.setDisable(false);
    }

    @FXML
    private void etapeSuivant(ActionEvent event) {
        if(text2EncryptTextfield.isDisable()){
            text2EncryptTextfield.setDisable(false);
            encryptButton.setDisable(false);
        }
        if(text2DecryptTextfield.isDisable()){
            text2DecryptTextfield.setDisable(false);
            decryptButton.setDisable(false);
        }
        etapeSuivanteButton.setDisable(true);
    }
    
    @FXML
    void disableOtherButton(ActionEvent event) {
        TextField textfield = (TextField)event.getSource();
        String id = textfield.getId();
        if(id == null ? cleTextfield.getId() == null : id.equals(cleTextfield.getId())){
            text2EncryptTextfield.setDisable(true);
            text2DecryptTextfield.setDisable(true);
        }
        if(id == null ? text2EncryptTextfield.getId() == null : id.equals(text2EncryptTextfield.getId())){
            cleTextfield.setDisable(true);
            text2DecryptTextfield.setDisable(true);
        }
        if(id == null ? text2DecryptTextfield.getId() == null : id.equals(text2DecryptTextfield.getId())){
            cleTextfield.setDisable(true);
            text2EncryptTextfield.setDisable(true);
        }
    }
}
