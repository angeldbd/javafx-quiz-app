# API Documentation 📚

> Guía completa de uso de los servicios principales de JavaQuiz.

## Tabla de Contenidos

- [QuizService](#quizservice)
- [TopicService](#topicservice)
- [TimerService](#timerservice)
- [ResultService](#resultservice)
- [Flujo Completo de Ejemplo](#flujo-completo-de-ejemplo)
- [Manejo de Errores](#manejo-de-errores)

---

## QuizService

Servicio central que gestiona toda la lógica del quiz. Controla el flujo de preguntas, respuestas y estadísticas.

### Constructor

```java
QuizService quiz = new QuizService();
```

### Inicialización

#### Con lista de preguntas

```java
List<Question> questions = // obtener preguntas...
try {
    quiz.initialize(questions);
} catch (InvalidQuizDataException e) {
    LOGGER.severe("Error inicializando quiz: " + e.getMessage());
}
```

**Validaciones:**
- Lista no puede ser `null`
- Lista no puede estar vacía

#### Con múltiples tópicos

```java
List<QuizData> multipleQuizzes = topicService.loadTopics(
    Arrays.asList("OOP", "Collections", "Multithreading")
);

try {
    quiz.initializeMultiple(multipleQuizzes);
} catch (InvalidQuizDataException e) {
    // Manejo de error
}
```

### Obtener Pregunta Actual

```java
Question current = quiz.getCurrentQuestion();

if (current != null) {
    System.out.println("Pregunta: " + current.getQn());
    System.out.println("Tópico: " + current.getTopic());
    System.out.println("Opciones: " + current.getOptions());
}
```

**Retorna `null` si:**
- Quiz no está inicializado
- Se pasó el final del quiz

### Validar Respuesta

```java
String userAnswer = "A";

try {
    boolean isCorrect = quiz.checkAnswer(userAnswer);
    
    // Registrar la respuesta
    quiz.registerAnswer(isCorrect);
    
    if (isCorrect) {
        System.out.println("¡Correcto!");
    } else {
        System.out.println("Incorrecto. La respuesta era: " + 
                          quiz.getCurrentQuestion().getA());
    }
} catch (IllegalArgumentException e) {
    System.out.println("Respuesta inválida: " + e.getMessage());
}
```

**Excepciones:**
- `IllegalArgumentException`: Si answer es `null`
- `IllegalStateException`: Si no hay pregunta actual

### Navegación

#### Siguiente pregunta

```java
quiz.goNext();

// Verificar si terminó
if (quiz.isFinished()) {
    System.out.println("Quiz completado");
} else {
    Question next = quiz.getCurrentQuestion();
}
```

#### Pregunta anterior

```java
quiz.goPrevious();
// No hace nada si ya estás en la primera pregunta
```

#### Verificar si pregunta fue respondida

```java
if (quiz.isCurrentQuestionAnswered()) {
    System.out.println("Esta pregunta ya fue respondida");
} else {
    System.out.println("Aún no has respondido esta pregunta");
}
```

### Mezclar Preguntas

```java
try {
    quiz.shuffle();
    System.out.println("Preguntas mezcladas. Recomenzar desde índice 0");
} catch (InvalidQuizDataException e) {
    LOGGER.severe("Error al mezclar: " + e.getMessage());
}
```

### Reiniciar Quiz

```java
try {
    quiz.reset();
    // Reseteado: índice=0, contadores=0, respuestas=vacío
} catch (InvalidQuizDataException e) {
    System.out.println("No se puede resetear quiz no inicializado");
}
```

### Timeout (Sin respuesta a tiempo)

```java
public void onTimerExpired() {
    quiz.registerTimeout();
    // Incrementa automáticamente respuestas incorrectas
    quiz.goNext();
}
```

### Obtener Estadísticas

```java
// Resultados generales
int correct = quiz.getCorrectAnswers();
int wrong = quiz.getWrongAnswers();
int total = quiz.getTotalQuestions();

double percentage = (double) correct / total * 100;
System.out.println(String.format("Score: %.1f%%", percentage));

// Estadísticas por tópico
Map<String, int[]> stats = quiz.getStatsByTopic();

for (Map.Entry<String, int[]> entry : stats.entrySet()) {
    String topic = entry.getKey();
    int[] numbers = entry.getValue();
    int topicCorrect = numbers[0];
    int topicWrong = numbers[1];
    
    System.out.println(topic + ": " + topicCorrect + "/" + 
                      (topicCorrect + topicWrong));
}
```

---

## TopicService

Servicio para descubrir y cargar tópicos disponibles.

### Constructor

```java
TopicService topicService = new TopicService();
```

### Obtener Tópicos Disponibles

```java
List<String> allTopics = topicService.getAvailableTopics();

// Muestra todos los tópicos en orden alfabético
allTopics.forEach(System.out::println);

// Salida típica:
// Arrays
// Collections
// Multithreading
// OOP
// Strings
// ...
```

**Retorna:**
- Lista ordenada alfabéticamente
- Lista vacía si hay error I/O

### Cargar Tópicos

#### Cargar uno solo

```java
List<String> selected = Arrays.asList("OOP");
List<QuizData> quizData = topicService.loadTopics(selected);

if (!quizData.isEmpty()) {
    QuizData oop = quizData.get(0);
    System.out.println("Preguntas cargadas: " + oop.getQuestions().size());
}
```

#### Cargar múltiples tópicos

```java
List<String> selected = Arrays.asList(
    "OOP", 
    "Collections", 
    "Multithreading"
);

List<QuizData> quizzes = topicService.loadTopics(selected);
System.out.println("Tópicos cargados: " + quizzes.size());

// Registra warnings automáticamente si alguno falla
```

**Comportamiento:**
- Si falla un tópico, continúa con los demás (fail-safe)
- Registra warnings en logs para tópicos fallidos
- Retorna solo los que se cargaron exitosamente

### Validar Selección

```java
List<String> userSelection = userSelectionFromUI;

if (topicService.validateSelection(userSelection)) {
    // Seguro proceder
    List<QuizData> data = topicService.loadTopics(userSelection);
} else {
    System.out.println("Debes seleccionar al menos un tópico");
}
```

---

## TimerService

Servicio para gestionar el temporizador del quiz.

### Constructor

```java
TimerService timer = new TimerService();
// Configurado automáticamente con TIMER_SECONDS de Constants
```

### Configurar Callback

```java
timer.setOnTimeout(() -> {
    System.out.println("¡Se acabó el tiempo!");
    quiz.registerTimeout();
    quiz.goNext();
    updateUI();
});
```

### Controlar Timer

#### Iniciar

```java
timer.start();
// Comienza conteo desde MAX_SECONDS hacia 0
// Ejecuta callback cuando llega a 0
```

#### Detener

```java
timer.stop();
// Pausa sin reiniciar
```

#### Reiniciar

```java
timer.restart();
// Equivalente a stop() + start()
```

### Binding con JavaFX UI

#### Label con segundos restantes

```java
Label timerLabel = new Label();
timerLabel.textProperty().bind(
    timer.timeSecondsProperty().asString()
);

// Label actualiza automáticamente cada segundo
```

#### ProgressBar

```java
ProgressBar progressBar = new ProgressBar();
progressBar.progressProperty().bind(timer.progressProperty());

// Progreso de 1.0 (lleno) a 0.0 (vacío)
```

#### Color dinámico

```java
// Cada 1 segundo, revisar color
Timer colorTimer = new Timer();
colorTimer.schedule(new TimerTask() {
    @Override
    public void run() {
        String color = timer.getProgressColor();
        Platform.runLater(() -> {
            progressBar.setStyle(color);
        });
    }
}, 0, 1000);
```

### Obtener Valores

```java
// Segundos actuales
int remaining = timer.getTimeSeconds();

// Progreso (0.0 a 1.0)
double progress = timer.getProgress();

// Color CSS
String css = timer.getProgressColor();
// Retorna: "-fx-accent: green;" | "-fx-accent: orange;" | "-fx-accent: red;"
```

---

## ResultService

Servicio para calcular y evaluar resultados.

### Constructor

```java
ResultService results = new ResultService();
```

### Calcular Puntaje

```java
int correctAnswers = 18;
int totalQuestions = 20;

double score = results.calculateScore(correctAnswers, totalQuestions);
// score = 0.9 (90%)

// Casos especiales
double zero = results.calculateScore(0, 0);     // Retorna 0.0
double allWrong = results.calculateScore(0, 20); // Retorna 0.0
double allCorrect = results.calculateScore(20, 20); // Retorna 1.0
```

### Obtener Mensaje de Retroalimentación

```java
double score = results.calculateScore(18, 20);

String message = results.getFeedbackMessage(score);
// "Congratulations! You scored well."

// Ejemplos según score:
// < 0.5:  "Oh no..! You have failed the quiz. Practice daily!"
// 0.5-0.7: "Oops..! Low score. Improve your knowledge."
// 0.7-0.9: "Good. Keep practicing for better results."
// 0.9-0.95: "Congratulations! You scored well."
// >= 0.95: "Perfect! Full marks, excellent work!"
```

### Formatear Puntaje

```java
String formatted = results.formatScoreText(18, 20);
// "18/20 Marks Score"

// Mostrar en UI
Label scoreLabel = new Label(formatted);
scoreLabel.setStyle("-fx-font-size: 24;");
```

### Obtener Color según Score

```java
double score = results.calculateScore(18, 20);

String color = results.getScoreColor(score);
// "#2ecc71" (verde, porque 0.9 >= 0.9)

// Aplicar a Label
Label scoreLabel = new Label("18/20");
scoreLabel.setStyle("-fx-text-fill: " + color + ";");

// Paleta de colores:
// < 0.5:    "#e74c3c" (rojo)
// 0.5-0.7:  "#f39c12" (naranja)
// 0.7-0.9:  "#3498db" (azul)
// >= 0.9:   "#2ecc71" (verde)
```

---

## Flujo Completo de Ejemplo

Este es un flujo típico de una sesión completa:

```java
public class QuizSessionExample {
    
    private QuizService quiz;
    private TopicService topicService;
    private TimerService timer;
    private ResultService results;
    
    public void startSession() throws InvalidQuizDataException {
        // 1. Crear servicios
        quiz = new QuizService();
        topicService = new TopicService();
        timer = new TimerService();
        results = new ResultService();
        
        // 2. Obtener tópicos disponibles y mostrar al usuario
        List<String> available = topicService.getAvailableTopics();
        System.out.println("Tópicos disponibles: " + available);
        
        // 3. Usuario selecciona tópicos
        List<String> selected = Arrays.asList("OOP", "Collections");
        
        // 4. Validar selección
        if (!topicService.validateSelection(selected)) {
            System.out.println("Selección inválida");
            return;
        }
        
        // 5. Cargar datos
        List<QuizData> quizData = topicService.loadTopics(selected);
        
        // 6. Inicializar quiz
        quiz.initializeMultiple(quizData);
        
        // 7. Configurar timer
        timer.setOnTimeout(() -> {
            quiz.registerTimeout();
            advanceQuestion();
        });
        
        // 8. Mostrar primera pregunta
        displayQuestion();
        timer.start();
    }
    
    private void displayQuestion() {
        Question current = quiz.getCurrentQuestion();
        
        if (current == null) {
            showResults();
            return;
        }
        
        System.out.println("\n=== Pregunta " + (quiz.getCurrentIndex() + 1) + 
                          " de " + quiz.getTotalQuestions() + " ===");
        System.out.println(current.getQn());
        System.out.println("Tópico: [" + current.getTopic() + "]");
        System.out.println(current.getOptions());
    }
    
    public void submitAnswer(String userAnswer) {
        try {
            boolean correct = quiz.checkAnswer(userAnswer);
            quiz.registerAnswer(correct);
            
            if (correct) {
                System.out.println("✓ ¡Correcto!");
            } else {
                System.out.println("✗ Incorrecto. Respuesta: " + 
                                 quiz.getCurrentQuestion().getA());
            }
            
            advanceQuestion();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void advanceQuestion() {
        timer.stop();
        quiz.goNext();
        
        if (quiz.isFinished()) {
            showResults();
        } else {
            timer.restart();
            displayQuestion();
        }
    }
    
    private void showResults() {
        int correct = quiz.getCorrectAnswers();
        int total = quiz.getTotalQuestions();
        
        double score = results.calculateScore(correct, total);
        String message = results.getFeedbackMessage(score);
        String formatted = results.formatScoreText(correct, total);
        String color = results.getScoreColor(score);
        
        System.out.println("\n=== RESULTADOS ===");
        System.out.println(formatted);
        System.out.println("Porcentaje: " + String.format("%.1f%%", score * 100));
        System.out.println("Mensaje: " + message);
        System.out.println("Color: " + color);
        
        // Estadísticas por tópico
        Map<String, int[]> stats = quiz.getStatsByTopic();
        System.out.println("\nEstadísticas por tópico:");
        for (Map.Entry<String, int[]> entry : stats.entrySet()) {
            int[] numbers = entry.getValue();
            System.out.println("  " + entry.getKey() + ": " + 
                             numbers[0] + "/" + (numbers[0] + numbers[1]));
        }
    }
}
```

---

## Manejo de Errores

### InvalidQuizDataException

Se lanza cuando hay problemas con datos del quiz:

```java
try {
    quiz.initialize(null); // null no permitido
} catch (InvalidQuizDataException e) {
    LOGGER.severe("Datos de quiz inválidos: " + e.getMessage());
    // Notificar usuario
}
```

### QuizLoadException

Se lanza cuando falla la carga de tópicos:

```java
try {
    List<QuizData> data = topicService.loadTopics(Arrays.asList("TopicInexistente"));
} catch (QuizLoadException e) {
    LOGGER.warning("Error cargando tópico: " + e.getMessage());
    // Mostrar tópicos disponibles
}
```

### IllegalArgumentException

Se lanza por parámetros inválidos:

```java
try {
    quiz.checkAnswer(null); // null no permitido
} catch (IllegalArgumentException e) {
    LOGGER.warning("Respuesta inválida: " + e.getMessage());
}
```

### IllegalStateException

Se lanza cuando el estado del quiz es inválido:

```java
try {
    QuizService quiz = new QuizService();
    quiz.checkAnswer("A"); // Sin inicializar antes
} catch (IllegalStateException e) {
    LOGGER.severe("Estado inválido: " + e.getMessage());
}
```

---

## Tips Profesionales

### 1. Siempre validar antes de usar

```java
// ❌ Mal
Question q = quiz.getCurrentQuestion();
String text = q.getQn(); // Null Pointer Exception si q es null

// ✅ Bien
Question q = quiz.getCurrentQuestion();
if (q != null) {
    String text = q.getQn();
}
```

### 2. Usar try-catch para operaciones críticas

```java
// ✅ Bien
try {
    quiz.initialize(questions);
} catch (InvalidQuizDataException e) {
    LOGGER.severe("Error: " + e.getMessage());
    // Recuperación o notificar usuario
}
```

### 3. Aprovechar el logging

```java
LOGGER.info("Quiz inicializado con " + questions.size() + " preguntas");
LOGGER.warning("Tópico no encontrado: OOP");
LOGGER.severe("Error crítico al cargar datos");
```

### 4. Binding automático en JavaFX

```java
// En lugar de actualizar manualmente cada segundo:
label.textProperty().bind(
    timer.timeSecondsProperty().asString()
);
// Automático y eficiente
```

---

## Referencia Rápida

| Servicio | Método | Descripción |
|----------|--------|-------------|
| **QuizService** | `initialize(List)` | Inicializar quiz |
| | `initializeMultiple(List)` | Inicializar con múltiples tópicos |
| | `getCurrentQuestion()` | Obtener pregunta actual |
| | `checkAnswer(String)` | Validar respuesta |
| | `registerAnswer(boolean)` | Registrar resultado |
| | `goNext()` | Siguiente pregunta |
| | `goPrevious()` | Pregunta anterior |
| | `shuffle()` | Mezclar preguntas |
| | `isFinished()` | ¿Quiz terminado? |
| | `getStatsByTopic()` | Estadísticas por tópico |
| **TopicService** | `getAvailableTopics()` | Listar tópicos |
| | `loadTopics(List)` | Cargar tópicos |
| | `validateSelection(List)` | Validar selección |
| **TimerService** | `start()` | Iniciar timer |
| | `stop()` | Detener timer |
| | `restart()` | Reiniciar timer |
| | `setOnTimeout(Runnable)` | Callback al terminar |
| | `timeSecondsProperty()` | Property para binding |
| | `progressProperty()` | Property progreso |
| **ResultService** | `calculateScore(int, int)` | Calcular % |
| | `getFeedbackMessage(double)` | Mensaje motivacional |
| | `formatScoreText(int, int)` | Formato puntuación |
| | `getScoreColor(double)` | Color según score |

---

<div align="center">

**📖 Para más detalles, ver [JavaDoc](target/reports/apidocs/index.html)**

Made with ❤️ by Angel

</div>