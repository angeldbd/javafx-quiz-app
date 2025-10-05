# JavaQuiz - Interactive Java Knowlege Assesment Tool

Una aplicación de escriotorio desarrollado con JavaFX para evaluar conocimientos de Java a través de cuestionarios interectivos con mútilples temas técnicos.

# 🎯 Características

-  **35+ temas técnicos**: Desde Java Basics hasta tecnologias avanzadas (Spring, Hibernate, Docker, AWS)
- **Visualización de código**: Panel especial para mostrar snippets de código en las preguntas
- **Sistema de puntuación**: Seguimiento de respuestas correctas e incorrectas por tema
- **Temporizador**: 15 segundos por pregunta para simular presión real
- **Modo aleatorio**: Opción de mezclar preguntas
- **Estadísticas detalladas**: Gráficos de rendimiento al finalizar el quiz
- **Navegación fluida**: Avanzar, retroceder y reiniciar en cualquier momento

## 📋 Requisitos Previos

- **Java**: JDK 11 o superior
- **Maven**: 3.6 o superior
- **JavaFX**: SDK incluido en las dependencias


## 🚀 Instalación

1. Clonar el repositorio:
    ```bash
    git clone https://github.com/angeldbd/javafx-quiz-app.git
    cd javafx-quiz-app

2. Compilar el proyecto:
    mvn clean compile

3. Ejecutar la aplicación:
    mvn javafx:run

📖 Uso
1. Pantalla de inicio: Click en "Iniciar" para comenzar
2. Selección de temas: Elige uno o más temas de la lista y presiona "Agregar tema"
3. Iniciar quiz: Click en "Iniciar" para comenzar el cuestionario
4. Responder preguntas: Selecciona una de las 4 alternativas antes de que termine el tiempo
5. Navegación: Usa los botones para avanzar, retroceder o terminar el quiz
6. Resultados: Revisa tus estadísticas al finalizar

📁 Estructura del Proyecto
src/main/java/org/openjfx/javaquiz/
├── JavaQuiz.java           # Punto de entrada de la aplicación
├── model/                  # Clases de datos
│   ├── Question.java       # Modelo de pregunta
│   ├── QuizData.java       # Contenedor de preguntas
│   └── TopicStats.java     # Estadísticas por tema
├── controller/             # Controladores JavaFX
│   ├── HomeController.java
│   ├── MenuController.java
│   ├── QuizController.java
│   └── ResultController.java
├── repository/             # Acceso a datos
│   └── QuizLoader.java     # Carga de preguntas desde JSON
└── util/                   # Utilidades
    └── CodeDisplay.java    # Visualización de código

src/main/resources/org/openjfx/javaquiz/
├── fxml/                   # Interfaces de usuario
│   ├── JavaQuiz.fxml
│   ├── menu.fxml
│   ├── quiz1.fxml
│   └── result.fxml
├── json/                   # Base de datos de preguntas
│   ├── A-BASICS.json
│   ├── B-OOP.json
│   └── ... (35+ archivos)
└── css/                    # Estilos
    ├── JavaQuiz.css
    └── javaCodePane.css

🛠️ Tecnologías

· JavaFX - Framework de interfaz gráfica
· Jackson - Procesamiento de JSON
· Maven - Gestión de dependencias y build
· Scene Builder - Diseño de interfaces FXML

📚 Temas Disponibles

· Java Basics, OOP, Inheritance, Polymorphism
· Static, Overloading/Overriding, Abstraction
· Exception Handling, Multi-threading
· Collections, String, Serialization
· Java 8 Features, Reflection
· Spring Framework, Hibernate
· Docker, AWS, Microservices
· Git, Maven, Unix Shell
· Y más...

🔮 Próximas Mejoras
- [ ] Base de datos SQL en lugar de JSON
- [ ] Sistema de usuarios con login
- [ ] Historial de intentos
- [ ] Exportar resultados a PDF
- [ ] Modo de estudio (sin tiempo límite)
- [ ] Agregar preguntas desde la UI

👨‍💻 Autor
Desarrollado como proyecto de aprendizaje de JavaFX y arquitectura de software.

📄 Licencia
Este proyecto es de código abierto y está disponible bajo la licencia MIT.