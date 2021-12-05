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
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import javafx.util.Callback;
import javafx.scene.control.SelectionMode;

import javafx.event.ActionEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * FXML Controller class
 * @author TODO
 * Classe metier de l'application
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
    private static final String NOHEADER_CLASS = "noheader";
    
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
    
    private static final String[] just = {"just-red", "just-blue"};
    private static final String[] red_blue_colum = {"red-column", "blue-column"};
    public static Map<String, int[]> route = new HashMap<String, int[]>();
    public static boolean route_is_trace = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gridpaneGeneral.setGridLinesVisible(true);
        gridpaneGeneral.setHgap(20);
        //gridpaneGeneral.setVgap(20);
        
        // Initialiser l'entrée, les rotors et le reflecteurs avec les données
        DataInit(false, false);
        
        // On ajoute la classe css qui va permettre que les entetes des tableviews ne s'affichent pas
        reflecteur.getStyleClass().add(NOHEADER_CLASS);
        rotor1.getStyleClass().add(NOHEADER_CLASS);
        rotor2.getStyleClass().add(NOHEADER_CLASS);
        rotor3.getStyleClass().add(NOHEADER_CLASS);    
        entree.getStyleClass().add(NOHEADER_CLASS);
        
        // On desactive les champs de texte d'encryption et de decryption, ainsi que les bouttons assonciés, jusqu'à ce
        // que l'utilisateur configure la machine avec une clé valide
        helpButtons(true);
    } 
    
    /**
     * Initialiser l'entrée, les rotors et le reflecteurs avec les données
     * @param re_init
     * @param with_tmp 
     * 
     * si re_init est vrai, on change juste les valeurs des elements des tableview; sinon (on vient de lancer l'application) 
     * on fait la liaison de données entre les valeurs dans nos tableaux et les cellules des tableaux.
     * 
     * si with_tmp est vrai, on fait la mise à jour avec les valeurs temporaires stockées dans les tableaux temporaires (qui
     * changent au cours de l'evolution d'une session), sinon on fait la mise à jour avec les valeurs de base.
     */
    void DataInit(boolean re_init, boolean with_tmp) {

        // Permettre qu'on puisse selectionne une seule cellule dans chaque tableview (pas tres necessaire dans notre travail)
        boolean enable_selection = true;
        reflecteur.getSelectionModel().setCellSelectionEnabled(enable_selection);
        rotor1.getSelectionModel().setCellSelectionEnabled(enable_selection);
        rotor1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rotor2.getSelectionModel().setCellSelectionEnabled(enable_selection);
        rotor3.getSelectionModel().setCellSelectionEnabled(enable_selection);
        entree.getSelectionModel().setCellSelectionEnabled(enable_selection);
        
        // On initiale chaque composant avec ses données
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
    
    /*
    public void initRotorReflecteur(TableView<Label[]> tableview, String[][] data, boolean re_init){ 
        if(re_init){
            tableview.getItems().clear();
        }else{
            for(int i=0; i<L;i++){
                TableColumn<Label[], String> column = new TableColumn();
                column.setText(Integer.toString(i));
                tableview.getColumns().addAll(column);
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Label[], String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Label[], String> p) {
                        Label[] x = p.getValue();
                        
                        if (x != null && x.length>0) {
                            J = (J + 1)%L;
                            return new SimpleStringProperty(x[J].getText());
                        } else {
                            return new SimpleStringProperty("N/A");
                        }
                    }
                });
            }
            J = -1;
        }
        int L1 = data.length, L2 = data[0].length;
        Label[][] a = new Label[L1][L2];
        for(int i=0; i<L1;i++){ for(int j=0; j<L2;j++){
                a[i][j] = new Label(data[i][j]);
        }}
        tableview.getItems().addAll(Arrays.asList(a)); 
    }
    */
    
    /**
     * Liaison de données entres les arrays et les tablesview
     * @param tableview
     * @param data
     * @param re_init 
     * 
     * Lorque re_init est vrai, on vide juste le tableview et on le recharge avec les nouvelles données
     */
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
    
    /**
     * Desactiver / Activer les bouttons et les zones de textes d'encryption & de decryption
     * @param b 
     */
    void helpButtons(boolean b) {
        text2EncryptTextfield.setDisable(b);
        text2DecryptTextfield.setDisable(b);
        encryptButton.setDisable(b);
        decryptButton.setDisable(b);
        etapeSuivanteButton.setDisable(b);
    }
    
    /**
     * Calcule le a modulo b 
     * En effet, a%b en java retourne le reste de la division de a par b, sans qui n'est euclidien que pour des entiers positifs
     * Pour palier à ce probleme nous utilisons la formule mathematique a mod b = (a%b + b)%b
     * @param a
     * @param b
     * @return a mod b
     * @see https://www.developpez.net/forums/d851304/java/general-java/langage/modulo-negatif-operateur/
     * @see https://stackoverflow.com/a/42131603/11814682
     */
    int modulo(int a, int b){
        return (a%b+b)%b;
    }
    
    /**
     * 
     * @param array
     * @param step
     * @return tableau initial, donc les valeurs ont été rotées en fonction de la valeur de step 
     *         array[i] = array[i+step]  
     */
    public String[] rotateArray(String[] array, String step){
        String[] s = step.contains("+") ? step.split("\\+") : step.split("-");
        String signe = String.valueOf(step.charAt(0)); // + ou -
        int step_int = Integer.parseInt(s[1]); // valeur du pas
        if ((!signe.equals("+") & !signe.equals("-")) ||  step_int < 0){
            throw new RuntimeException("Invalid value for step");
        }
        if (signe.equals("-")){
            step_int = -step_int;
        }  
        int l = array.length;
        String[] tmp = new String[l];
        for(int i=0; i<l;i++){
            tmp[i] = array[modulo(i+step_int, l)]; 
        }
        return tmp;
    }
    
    /**
     * Fait roter les rotors en fonctions des valeurs initiales de la clé (configuration initiale)
     * Les elements de la CLE est sous la forme ("Rx", "G", "+7") par exemple, ou x est le numero du rotor concerné
     */
    void rotationInitiale() {
        for (String[] cle_rotor : CLE) {
            switch (cle_rotor[0]) {
                case "R1":
                    data_rotors1_tmp[0] = rotateArray(data_rotors1[0], cle_rotor[2]);
                    data_rotors1_tmp[1] = rotateArray(data_rotors1[1], cle_rotor[2]);
                    initRotorReflecteur(rotor1, data_rotors1_tmp, true);
                    break;
                case "R2":
                    data_rotors2_tmp[0] = rotateArray(data_rotors2[0], cle_rotor[2]);
                    data_rotors2_tmp[1] = rotateArray(data_rotors2[1], cle_rotor[2]);
                    initRotorReflecteur(rotor2, data_rotors2_tmp, true);
                    break;
                case "R3":
                    data_rotors3_tmp[0] = rotateArray(data_rotors3[0], cle_rotor[2]);
                    data_rotors3_tmp[1] = rotateArray(data_rotors3[1], cle_rotor[2]);
                    initRotorReflecteur(rotor3, data_rotors3_tmp, true);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Configuration initiale du rotor (lorsqu'on clique sur le boutton configurer le rotor)
     * @param event 
     */
    @FXML
    void configureCleRotors(ActionEvent event) {
        // Si on venait de faire une encryption/decryption, decolorier les cases concernées
        if(route_is_trace){
            trace_or_drop_route(route,  true);
            route_is_trace = false;
        }
        String sample = "(R3,G,+7)(R1,D,-6)(R2,D,+5)";
        // TODO
        //sample = "(R3,G,+0)(R1,D,-0)(R2,D,+0)";
        sample = "(R1,G,+0)(R2,D,-0)(R3,D,+0)";
        if (configRotorsButton.getText().equals("Ré-Configurer Rotors")){
            configRotorsButton.setText("Configurer Rotors");
            cleTextfield.setDisable(false);
            cleTextfield.setText("Clé. Exemple : "+sample);
            // On desactive les bouttons et champs de texte d'encryption de decryption
            helpButtons(true);
            // On remet les valeurs initiales dans les tableview, en reinitialisant
            DataInit(true, false); // with_tmp = false
            // on ressort de la fonction
            return;
        } 
        String cle  = cleTextfield.getText();
        if(cle.isEmpty()){
            cleTextfield.setText("Impossible, clé vide, Exemple : "+sample);
            return;
        }
        // regex de forme de la clé 
        //String pat = "/^(\\(R[1-3],[GD],[-\\+][0-9]+\\)){3}$/";
        String pat = "\\(R[1-3],[GD],[-\\+][0-9]+\\)\\(R[1-3],[GD],[-\\+][0-9]+\\)\\(R[1-3],[GD],[-\\+][0-9]+\\)";
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(cle);
        if(!matcher.find()){
            // La clé n'est pas sous une forme valide
            cleTextfield.setText("Clé Invalide, Exemple : "+sample);
            return;
        }
        // La clé est sous une forme valide
        String[][] CLE_tmp = new String[3][3];
        String[] s = cle.split("\\)\\("); // (R3,G,+7  et  R1,D,-6  et   R2,D,+5)
        String[] c = new String[3];
        c = s[0].split("\\(")[1].split(","); // R3 G +7
        for(int i = 0; i < c.length; i++){CLE_tmp[0][i] = c[i]+"";}
        c = s[1].split(","); // R1 D -6
        for(int i = 0; i < c.length; i++){CLE_tmp[1][i] = c[i]+"";}
        c = s[2].split("\\)")[0].split(","); // R2 D +5
        for(int i = 0; i < c.length; i++){CLE_tmp[2][i] = c[i]+"";}
        
        // Verifier qu'on a tout les 3 R (R1, R2, R3)
        char x = CLE_tmp[0][0].charAt(1), y = CLE_tmp[1][0].charAt(1), z = CLE_tmp[2][0].charAt(1);
        if(x == y || x == z || y == z){
            cleTextfield.setText("Clé Invalide, Exemple : "+ sample);
            return;
        }
        
        // Verifier si les step sont corrects (entre -26 et 26)
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
        
        // La clé est correcte
        for(int i = 0; i < CLE_tmp.length; i++){
            for(int j = 0; j < CLE_tmp[i].length; j++){
                CLE[i][j] = CLE_tmp[i][j];
            }
        }
        cleTextfield.setText(cle);
        cleTextfield.setDisable(true);
        configRotorsButton.setText("Ré-Configurer Rotors");
        // On active les champs de texte et les bouttons d'encrytion
        helpButtons(false);
        // Et on fait une rotation initiale avec la clé qu'on vient d'obtenir
        rotationInitiale();
        // On desactive le boutton etape suivante
        etapeSuivanteButton.setDisable(true);
       
    }
    
    /**
     * Ajoute un entier à tout les elements d'un tableau (en s'assurant qu'on ne depasse pas les bornes)
     * @param r
     * @param p
     * @return 
     */
    int[] add_int_to_array(int[] r, int p){
        int L_1 = r.length;
        int[] result = new int[L_1]; 
        for (int i = 0; i<L_1; i++){
            result[i] = modulo(r[i] + p, L);
        }
        return result;
    }
    
    /**
     * Fait tourner un des rotors à gauche ou à droite en fonction de la valeur conténue dans la CLE pour ce rotor
     * On modifie les routes en consequence (puisque les elements à colorier se deplace aussi)
     * @param ordre 
     */
    void tourner(int ordre) {
        N[ordre] = modulo(N[ordre]+1, L);
        String name_rotor = CLE[ordre][0]; // "R1" ou "R2" ou "R3"
        // step vaut -1 si on doit tourner à gauche (G) et +1 sinon (D)
        String step = CLE[ordre][1].equals("G") ? "-1" :"+1";
        int step_int = CLE[ordre][1].equals("G") ? -1 : 1;
        switch (name_rotor) {
            case "R1":
                data_rotors1_tmp[0] = rotateArray(data_rotors1_tmp[0], step);
                data_rotors1_tmp[1] = rotateArray(data_rotors1_tmp[1], step);
                initRotorReflecteur(rotor1, data_rotors1_tmp, true);
                route.put("rotor1", add_int_to_array(route.get("rotor1"), step_int));  
                break;
            case "R2":
                data_rotors2_tmp[0] = rotateArray(data_rotors2_tmp[0], step);
                data_rotors2_tmp[1] = rotateArray(data_rotors2_tmp[1], step);
                initRotorReflecteur(rotor2, data_rotors2_tmp, true);
                route.put("rotor2", add_int_to_array(route.get("rotor2"), step_int));
                break;
            case "R3":
                data_rotors3_tmp[0] = rotateArray(data_rotors3_tmp[0], step);
                data_rotors3_tmp[1] = rotateArray(data_rotors3_tmp[1], step);
                initRotorReflecteur(rotor3, data_rotors3_tmp, true);
                route.put("rotor3", add_int_to_array(route.get("rotor3"), step_int));
                break;
            default:
                break;
        }
        //if(ordre < 2 & N[ordre] == L-1){
        //    N[ordre+1] = (N[ordre+1]+1)%L;
        //}
     
    }
        
    /**
     * faire une rotation apres une encryption ou decryption :
     * - si le rotor qui doit faire la 1er rotation a fait un tour sur lui meme, faire tourner second rotor 
     * - si le rotor qui doit faire la 2nd rotation a fait un tour sur lui meme, faire tourner le dernier rotor
     */
    void rotation() {
        int ordre = 0;
        tourner(ordre);
        // si le rotor qui doit faire la 1er rotation a fait un tour sur lui meme, faire tourner second rotor 
        if(N[ordre] == L-1){
            ordre = ordre + 1; //1
            tourner(ordre);
            // si le rotor qui doit faire la 2nd rotation a fait un tour sur lui meme, faire tourner le dernier rotor
            if(N[ordre] == L-1){
                ordre = ordre + 1; //2
                tourner(ordre);
            }
        }
        // Une fois que c'est fait, tracer la route avec ses nouvelles valeurs
        trace_or_drop_route(route,  false);
    }
    
    /**
     * convertir un step en entier : "-6" devient -6 tout simplement
     * @param step
     * @return 
     */
    int split_step(String step) {
        String[] s = step.contains("+") ? step.split("\\+") : step.split("-");
        String signe = String.valueOf(step.charAt(0)); //+ or -
        int step_int = Integer.parseInt(s[1]); // step/pas, un entier positif
        if (signe.equals("-")){
            step_int = -step_int;  
        } 
        return step_int;
    }
    
    /**
     * tracer ou supprimer la route deja tracer 
     * il s'agit juste, de le cas du traçage, de colorier les chiffres de la route de depart en rouge et ceux de 
     * la route d'arrivée en bleu
     * @param route
     * @param drop 
     */
    void trace_or_drop_route(Map<String, int[]> route,  boolean drop){
        if(drop){
            for(int j=0;j<2;j++){
                entree.getColumns().get(route.get("entree")[j]).getStyleClass().remove(just[j]);
                rotor1.getColumns().get(route.get("rotor1")[j]).getStyleClass().remove(red_blue_colum[j]);
                rotor2.getColumns().get(route.get("rotor2")[j]).getStyleClass().remove(red_blue_colum[j]);
                rotor3.getColumns().get(route.get("rotor3")[j]).getStyleClass().remove(red_blue_colum[j]);
                reflecteur.getColumns().get(route.get("reflecteur")[j]).getStyleClass().remove(just[j]);
            }
        }else{
            for(int j=0;j<2;j++){
                entree.getColumns().get(route.get("entree")[j]).getStyleClass().add(just[j]);
                rotor1.getColumns().get(route.get("rotor1")[j]).getStyleClass().add(red_blue_colum[j]);
                rotor2.getColumns().get(route.get("rotor2")[j]).getStyleClass().add(red_blue_colum[j]);
                rotor3.getColumns().get(route.get("rotor3")[j]).getStyleClass().add(red_blue_colum[j]);
                reflecteur.getColumns().get(route.get("reflecteur")[j]).getStyleClass().add(just[j]);
                route_is_trace = true;
            }
        }
    }
    
    /**
     * Faire un aller (entree > rotor1 > rotor2 > rotor3 > reflecteur) et 
     * un retour (reflecteur > rotor3 > rotor2 > rotor1 > entree)
     * @param text
     * @return resultat de l'encryption/decrytion
     */
    String aller_retour(String text){ 
        if(route_is_trace){
            trace_or_drop_route(route,  true);
            route_is_trace = false;
        }
  
        int i, v, line;
        
        // entree
        i = Arrays.asList(alphabet[0]).indexOf(text);
        int[] route_entree = {i, -1};
        line = 1-0;
        // rotor 1, 1er colonne
        v = split_step(data_rotors1_tmp[line][i]); 
        int[] route_rotor1 = {i, -1};
        i = modulo(i+v, L);
        // rotor 2, 1er colonne
        v = split_step(data_rotors2_tmp[line][i]);
        int[] route_rotor2 = {i, -1};
        i = modulo(i+v, L); 
        // rotor 3, 1er colonne
        v = split_step(data_rotors3_tmp[line][i]);
        int[] route_rotor3 = {i, -1};
        i = modulo(i+v, L); 
        // reflecteur
        v = split_step(data_reflecteur[0][i]);
        int[] route_reflecteur = {i, -1};
        i = modulo(i+v, L); 
        route_reflecteur[1] = i;
        line = 1-1;
        // rotor 3, 2e colonne
        v = split_step(data_rotors3_tmp[line][i]);
        rotor3.getColumns().get(i).getStyleClass().add("blue-column");
        route_rotor3[1] = i;
        i = modulo(i+v, L); 
        // rotor 2, 2e colonne
        v = split_step(data_rotors2_tmp[line][i]);
        route_rotor2[1] = i;
        i = modulo(i+v, L); 
        // rotor 1, 2e colonne
        v = split_step(data_rotors1_tmp[line][i]);
        route_rotor1[1] = i;
        i = modulo(i+v, L); 
        // entree
        route_entree[1] = i;
        
        route.put("entree", route_entree);
        route.put("rotor1", route_rotor1);
        route.put("rotor2", route_rotor2);
        route.put("rotor3", route_rotor3);
        route.put("reflecteur", route_reflecteur);
        /*
        for(int j=0;j<2;j++){
            System.out.println(route.get("entree")[j]);
            System.out.println(route.get("rotor1")[j]);
            System.out.println(route.get("rotor2")[j]);
            System.out.println(route.get("rotor3")[j]);
            System.out.println(route.get("reflecteur")[j]);
                
        }
        //*/
        route_is_trace = true;
        // Rotation
        rotation();
        // Resultat de l'encryption/decryption
        return alphabet[0][i];
    }
    
    /**
     * Cette fonction est appélé lorsqu'on clique sur le boutton encrypter 
     * @param event 
     */
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
        // On desactive le boutton et le champ de texte d'encryption
        text2EncryptTextfield.setText(s);
        text2EncryptTextfield.setDisable(true);
        encryptButton.setDisable(true);
        // On active le boutton etape suivante
        etapeSuivanteButton.setDisable(false);
    }

    /**
     * Cette fonction est appélé lorsqu'on clique sur le boutton decrypter 
     * @param event 
     */
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
        // On desactive le boutton et le champ de texte de decryption
        String s = aller_retour(text);
        text2DecryptTextfield.setText(s);
        text2DecryptTextfield.setDisable(true);
        decryptButton.setDisable(true);
        // On active le boutton etape suivante
        etapeSuivanteButton.setDisable(false);
    }
    
    /**
     * Cette fonction est appélé lorsqu'on clique sur le boutton etape suivante
     * @param event 
     */
    @FXML
    private void etapeSuivant(ActionEvent event) {
        if(route_is_trace){
            trace_or_drop_route(route,  true);
            route_is_trace = false;
        }
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
    
    /**
     * 
     * @param event 
     */
    //*
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
    //*/
}
