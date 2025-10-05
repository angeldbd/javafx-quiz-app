
package org.openjfx.javaquiz;

import java.util.jar.JarFile;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Este metodo inicia la app
 * @author angel
 */
public class MenuController {
        @FXML
        private ComboBox<String> comboBoxid;

        @FXML
        private Button iniciarBtn, agregarTema;
       
        @FXML
        private ListView<String> topicsListView;
        
        private List<String> availableTopics; // Lista de temas disponibles
        private List<QuizData> selectedQuizData; // Preguntas de los temas seleccionados
        
        @FXML
        private void initialize(){
                selectedQuizData = new ArrayList<>();
            
            // configuradion selección multiple 
                topicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                
                // Cargar los nombres de los temas (puedes obtenerlos desde los nombres de los archivos JSON)
                availableTopics = loadAvailableTopics();
                topicsListView.setItems(FXCollections.observableArrayList(availableTopics));
                
                // llenar el combo box con los temas disponibles
                comboBoxid.getItems().addAll(
                        "A-BASICS", "B-OOP", "C-INHERITANCE", "D-STATIC", "E-OVERLOADINGOVERRIDING",
                        "F-POLYMORPHISM", "G-ABSTRACTION", "H-PACKAGE", "I-INTERNATIONALIZATION", 
                        "J-SERIALIZATION", "K-REFLECTION", "L-GARBAGECOLLECTION", "M-INNERCLASSES", 
                        "N-STRING", "O-EXCEPTIONHANDLING", "P-MULTI-THREADING", "Q-MIXEDQUESTIONS",
                        "R-JAVACOLLECTION", "S-ADVANCEDMULTI-THREADING", "T-JAVA8", 
                        "U-JAVATRICKYQUESTIONS", "V-JSP", "W-JAVADESIGNPATTERNS", "X-SPRINGQUESTIONS",
                        "Y-HIBERNETE", "Z-MAVEN", "ZA-GIT", "ZB-AWS", "ZC-DOCKER", 
                        "ZD-UNIXSHELL", "ZE-MICROSERVICES"
                );
                
                /*
                        //La logica del combobox
                iniciarBtn.setOnAction(event -> {
                        // obtener temas seleccionados
                        List<String> selectedTopics = (List<String>) topicsListView.getSelectionModel().getSelectedItems();
                        
                        if(selectedTopics.isEmpty()){
                            // Mostrar mensaje de error si no se seleccionó nignún tema
                            System.out.println("Por favor, selecciona al menos un tema.");
                            return;
                        }
                        
                        // Cargar preguntas de los temas seleccionados
                        selectedQuizData = new ArrayList<>();
                        for (String topic : selectedTopics) {
                        QuizData data= QuizLoader.loadQuizData(topic);
                        if(data != null){
                            selectedQuizData.add(data);
                        }
                    }
                        
                        String selectdTopic = comboBoxid.getValue();
                        
                        if(selectdTopic != null){
                                try {
                                        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource("quiz1.fxml"));
                                        Scene scene = new Scene(loader.load());
                                        
                                        QuizController qc = loader.getController();
                                        qc.setData(QuizLoader.loadQuizData(selectdTopic),selectdTopic);
                                        
                                        Stage stage = new Stage();
                                        stage.setScene(scene);
                                        stage.initStyle(StageStyle.TRANSPARENT);
                                        scene.setFill(Color.TRANSPARENT);
                                        stage.show();
                                        
                                        // cerrar menu
                                        Stage current = (Stage) iniciarBtn.getScene().getWindow();
                                        current.close();
                            } catch (Exception e) {
                                        e.printStackTrace();
                            }
                        }
                        
                        
                });
                */
                
                iniciarBtn.setOnAction(event -> startQuiz());
                
        }
        
        @FXML
        private void startQuiz() {
            if (selectedQuizData == null || selectedQuizData.isEmpty()) {
                System.out.println("Por favor, selecciona al menos un tema.");
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource("quiz1.fxml"));
                Scene scene = new Scene(loader.load());
                QuizController qc = loader.getController();
                qc.setQuizData(selectedQuizData); // Usar selectedQuizData
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                stage.show();
                Stage current = (Stage) iniciarBtn.getScene().getWindow();
                current.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Método para obtener los nomtes de los temas (puedes personalizarlo)
                private List<String> loadAvailableTopics(){
                        List<String> topics = new ArrayList<>();
                        List titulos = listJsonFiles2();
                        
                        for (Object titulo : titulos) {
                            topics.add((String)titulo);
                    }
                        return topics;
                }
                
                /* método para agregar tema*/

                @FXML
                private void agregarTema(){
                        List<String> selectedTopics = topicsListView.getSelectionModel().getSelectedItems();
                        if(!selectedTopics.isEmpty()){
                                selectedQuizData = selectedQuizData == null ? new ArrayList<>() : selectedQuizData;
                                for (String topic : selectedTopics) {
                                QuizData data = QuizLoader.loadQuizData(topic);
                                if (data != null && !selectedQuizData.contains(data)) {
                                selectedQuizData.add(data);
                                }
                            }
                                System.out.println("Temas agregados: " + selectedTopics);
                        } else {
                                System.out.println("Selecciona al menos un tema.");
                        }
                }
                
        /**
         * metodo para encontrar el nombre de los archivos en jar no en ide
         */
    public static void listJsonFiles() {
    try {
        // getResource devuelve URL
        URL url = QuizLoader.class.getResource("json/");
        if (url != null) {
            URLConnection conn = url.openConnection();
            if (conn instanceof JarURLConnection) {
                // dentro de un JAR
                JarURLConnection jarConn = (JarURLConnection) conn;
                JarFile jar = jarConn.getJarFile();
                jar.stream()
                   .filter(e -> e.getName().startsWith("json/") && e.getName().endsWith(".json"))
                   .forEach(e -> System.out.println(e.getName()));
            } else {
                // en entorno IDE
                File dir = new File(url.toURI());
                for (File file : dir.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        System.out.println(file.getName());
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    /**
     * metodo para encontrar los nombres de los archivos mas simple
     */
    public static List listJsonFiles2() {
    try {
        URI uri = QuizLoader.class.getResource("json/").toURI();
        Path path = Paths.get(uri);
        /*Files.list(path)
             .filter(Files::isRegularFile)
             .forEach(f -> System.out.println(f.getFileName().toString()));*/
        return  Files.list(path)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString().replace(".json", ""))
                .collect(Collectors.toList());
             
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}

}
