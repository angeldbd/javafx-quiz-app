# 🎨 JavaQuiz - Design System Guide

Versión: 2.0
Fecha: Enero 2025
Autor: angel

---

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Paleta de Colores](#paleta-de-colores)
3. [Tipografía](#tipografía)
4. [Componentes](#componentes)
5. [Espaciado](#espaciado)
6. [Animaciones](#animaciones)
7. [Uso de Clases CSS](#uso-de-clases-css)

---

## 🎯 Introducción

Este Design System define los principios visuales y componentes reutilizables de la aplicación JavaQuiz. El objetivo es mantener **consistencia visual**, **código limpio** y facilitar el **mantenimiento** a largo plazo.

### Filosofía de Diseño

- **Tech Education**: Profesional, moderno, enfocado en aprendizaje
- **Colores vibrantes**: Azul eléctrico + Morado para captar atención
- **Claridad visual**: Buen contraste, jerarquía clara
- **Sutileza en animaciones**: Transiciones suaves, no distractivas

---

## 🎨 Paleta de Colores

### Colores Primarios

```css
Primary:       #5B4FFF  /* Azul eléctrico - Botones principales */
Primary Dark:  #4338CA  /* Azul oscuro - Hover states */
Primary Light: #818CF8  /* Azul claro - Backgrounds sutiles */
```

**Uso:**
- Botones de acción principal (PLAY, INICIAR QUIZ, SIGUIENTE)
- Títulos y elementos destacados
- Progress bars y indicadores

### Colores Secundarios

```css
Secondary:       #7C3AED  /* Morado - Elementos complementarios */
Secondary Dark:  #6D28D9  /* Morado oscuro - Hover */
Secondary Light: #A78BFA  /* Morado claro - Backgrounds */
```

**Uso:**
- Gradientes (combinado con Primary)
- Labels de contador
- Elementos decorativos

### Colores Semánticos

```css
Success:     #10B981  /* Verde - Respuestas correctas */
Success BG:  #D1FAE5  /* Verde claro - Backgrounds de éxito */

Error:       #EF4444  /* Rojo - Respuestas incorrectas */
Error BG:    #FEE2E2  /* Rojo claro - Backgrounds de error */

Warning:     #F59E0B  /* Naranja - Advertencias */
```

**Uso:**
- Estados de respuestas en el quiz
- Progress indicators (correctas/incorrectas)
- Feedback visual

### Colores Neutrales

```css
Background Primary:   #FFFFFF  /* Blanco - Fondo principal */
Background Secondary: #F8FAFC  /* Gris muy claro - Cards */
Background Tertiary:  #E2E8F0  /* Gris claro - Bordes */

Text Primary:   #0F172A  /* Casi negro - Texto principal */
Text Secondary: #475569  /* Gris oscuro - Texto secundario */
Text Tertiary:  #94A3B8  /* Gris medio - Texto terciario */
```

---

## 📝 Tipografía

### Fuente Principal

```css
Font Family: "Segoe UI", "Helvetica Neue", Arial, sans-serif
```

### Escala de Tamaños

| Uso | Tamaño | Peso | Clase CSS |
|-----|--------|------|-----------|
| **Display** (Títulos grandes) | 48px | Bold (700) | `.title-display` |
| **H1** (Títulos principales) | 32px | Semibold (600) | `.title-large` |
| **H2** (Subtítulos) | 24px | Semibold (600) | `.title-medium` |
| **Body Large** | 18px | Regular (400) | `.text-body-lg` |
| **Body** | 16px | Regular (400) | `.text-body` |
| **Small** | 14px | Regular (400) | `.text-small` |
| **Caption** | 12px | Regular (400) | `.text-caption` |

### Ejemplos

```xml
<!-- Título principal -->
<Label text="JavaQuiz" styleClass="title-large"/>

<!-- Texto de cuerpo -->
<Label text="Pon a prueba tus conocimientos" styleClass="text-body"/>

<!-- Texto pequeño -->
<Label text="💡 Doble clic para quitar" styleClass="text-small"/>
```

---

## 🧩 Componentes

### 1. Botones

#### Botón Primary (Acciones principales)

**Clase CSS:** `.btn-primary`

**Características:**
- Gradiente azul → morado
- Texto blanco
- Sombra azul
- Hover: Gradiente más oscuro + escala 1.02
- Pressed: Fondo sólido + escala 0.98

**Uso:**
```xml
<Button text="COMENZAR QUIZ" styleClass="btn-primary"/>
```

**Cuándo usar:**
- Acción principal de cada pantalla
- Botones de navegación importante (SIGUIENTE)

---

#### Botón Secondary (Acciones secundarias)

**Clase CSS:** `.btn-secondary`

**Características:**
- Fondo blanco
- Borde azul 2px
- Texto azul
- Hover: Fondo azul + texto blanco

**Uso:**
```xml
<Button text="Agregar Tema" styleClass="btn-secondary"/>
```

**Cuándo usar:**
- Acciones complementarias
- Botones de agregar/quitar elementos

---

#### Botón Navigation (Navegación)

**Clase CSS:** `.btn-nav`

**Características:**
- Fondo gris claro
- Texto gris oscuro
- Borde sutil
- Hover: Fondo más oscuro

**Uso:**
```xml
<Button text="← Menú" styleClass="btn-nav"/>
```

**Cuándo usar:**
- Botones de retroceso
- Navegación secundaria
- Utilidades (cerrar, mezclar)

---

#### Botón Finish (Terminar/Éxito)

**Clase CSS:** `.btn-finish`

**Características:**
- Fondo verde
- Texto blanco
- Hover: Verde más oscuro

**Uso:**
```xml
<Button text="✓ Terminar Quiz" styleClass="btn-finish"/>
```

**Cuándo usar:**
- Acciones de finalización
- Confirmaciones positivas

---

### 2. Opciones del Quiz

#### Opción Normal

**Clase CSS:** `.option-button`

**Características:**
- Fondo blanco
- Borde gris claro 2px
- Texto oscuro alineado a la izquierda
- Hover: Fondo gris claro + borde azul

**Uso:**
```xml
<Button styleClass="option-button" text="Opción A"/>
```

---

#### Opción Correcta

**Clase CSS:** `.option-correct`

**Características:**
- Fondo verde claro
- Borde verde
- Texto verde

**Uso (aplicar desde Java):**
```java
button.getStyleClass().add("option-correct");
```

---

#### Opción Incorrecta

**Clase CSS:** `.option-wrong`

**Características:**
- Fondo rojo claro
- Borde rojo
- Texto rojo

**Uso (aplicar desde Java):**
```java
button.getStyleClass().add("option-wrong");
```

---

### 3. Contenedores

#### Container Main

**Clase CSS:** `.container-main`

**Características:**
- Fondo blanco
- Border radius 32px
- Sombra suave
- Requiere clip para esquinas redondeadas

**Uso:**
```xml
<AnchorPane fx:id="rootPane" styleClass="container-main">
```

**Requiere (en Controller):**
```java
private void applyRoundedCorners() {
    Rectangle clip = new Rectangle();
    clip.setArcWidth(32);
    clip.setArcHeight(32);
    clip.widthProperty().bind(rootPane.widthProperty());
    clip.heightProperty().bind(rootPane.heightProperty());
    rootPane.setClip(clip);
}
```

---

#### Stats Card

**Clase CSS:** `.stats-card`

**Características:**
- Fondo blanco
- Border radius 16px
- Sombra media
- Padding 24px

**Uso:**
```xml
<VBox styleClass="stats-card">
```

---

### 4. Progress Indicators

#### Progress Correct (Verde)

**Clase CSS:** `.progress-correct`

```xml
<ProgressIndicator styleClass="progress-correct" progress="0.8"/>
```

---

#### Progress Wrong (Rojo)

**Clase CSS:** `.progress-wrong`

```xml
<ProgressIndicator styleClass="progress-wrong" progress="0.2"/>
```

---

### 5. Listas

#### List View

**Clase CSS:** `.list-view` (aplicada automáticamente)

**Características:**
- Borde gris claro
- Border radius 12px
- Hover en celdas
- Selección con fondo azul claro

---

#### List View Selected (Temas seleccionados)

**Clase CSS:** `.list-view-selected`

**Características:**
- Borde azul (indica selección)
- Celdas seleccionadas en rojo claro (para eliminar)

---

### 6. Tablas

#### Tree Table

**Estilo automático** para TreeTableView

**Características:**
- Borde gris claro
- Header con fondo gris claro
- Hover en filas
- Selección con fondo azul claro

**Celdas especiales:**
- `.correct-cell`: Texto verde y bold
- `.wrong-cell`: Texto rojo y bold

---

## 📏 Espaciado

### Sistema de Espaciado (8px base)

```css
XS:  4px   /* Espaciado mínimo */
SM:  8px   /* Espaciado pequeño */
MD:  16px  /* Espaciado medio */
LG:  24px  /* Espaciado grande */
XL:  32px  /* Espaciado extra grande */
2XL: 48px  /* Espaciado muy grande */
```

### Aplicación

```xml
<!-- Spacing entre elementos -->
<VBox spacing="24">  <!-- LG -->

<!-- Padding interno -->
<padding>
    <Insets top="32" bottom="32" left="32" right="32"/>
</padding>

<!-- Margins -->
<VBox.margin>
    <Insets top="16"/>
</VBox.margin>
```

### Reglas

- **Pantallas principales**: Padding 32px (XL)
- **Cards**: Padding 24px (LG)
- **Entre secciones**: Spacing 24px (LG)
- **Entre elementos relacionados**: Spacing 12-16px (SM-MD)
- **Elementos muy juntos**: Spacing 8px (SM)

---

## 🎬 Animaciones

### Utilidad: AnimationUtil

Ubicación: `org.openjfx.javaquiz.util.AnimationUtil`

### Tipos de Animaciones

#### 1. Fade In

```java
// Fade in básico (300ms)
AnimationUtil.fadeIn(node);

// Fade in con duración personalizada
AnimationUtil.fadeIn(node, Duration.millis(500));
```

**Uso:** Aparición de pantallas

---

#### 2. Fade Out

```java
// Fade out básico
AnimationUtil.fadeOut(node);

// Fade out con callback
AnimationUtil.fadeOutWithCallback(node, () -> {
    // Código después de la animación
});
```

**Uso:** Transiciones entre pantallas

---

#### 3. Pop Effect

```java
AnimationUtil.popEffect(button);
```

**Uso:** Feedback visual al hacer clic en botones importantes

---

#### 4. Fade In With Scale

```java
AnimationUtil.fadeInWithScale(rootPane);
```

**Uso:** Apertura de pantallas principales (efecto de "crecer")

---

#### 5. Shake

```java
AnimationUtil.shake(node);
```

**Uso:** Indicar errores o validaciones fallidas

---

### Principios de Animación

1. **Sutileza**: Duraciones entre 200-500ms
2. **Propósito**: Cada animación debe tener una razón (feedback, transición, jerarquía)
3. **Consistencia**: Mismas duraciones para mismos tipos de animación
4. **No bloquear**: Las animaciones no deben bloquear la UI

---

## 💻 Uso de Clases CSS

### Buenas Prácticas

#### ✅ CORRECTO

```java
// Aplicar clase desde Java
button.getStyleClass().add("btn-primary");

// Aplicar clase desde FXML
<Button styleClass="btn-primary"/>

// Limpiar y aplicar nueva clase
button.getStyleClass().removeAll("option-button");
button.getStyleClass().add("option-correct");
```

#### ❌ INCORRECTO

```java
// NO usar estilos inline
button.setStyle("-fx-background-color: green;");  // ❌

// NO hardcodear colores
button.setStyle("-fx-background-color: #5B4FFF;");  // ❌
```

### Razones

- **Mantenibilidad**: Cambios centralizados en CSS
- **Consistencia**: Todos los componentes usan los mismos estilos
- **Reusabilidad**: Las clases se pueden aplicar a cualquier componente
- **Separación de responsabilidades**: Java maneja lógica, CSS maneja diseño

---

## 🔧 Troubleshooting

### Problema: Esquinas no se ven redondeadas

**Causa:** JavaFX ignora border-radius en algunos casos

**Solución:** Aplicar clip en el controller

```java
private void applyRoundedCorners() {
    Rectangle clip = new Rectangle();
    clip.setArcWidth(32);
    clip.setArcHeight(32);
    clip.widthProperty().bind(rootPane.widthProperty());
    clip.heightProperty().bind(rootPane.heightProperty());
    rootPane.setClip(clip);
}
```

---

### Problema: Gradientes no funcionan

**Causa:** JavaFX no soporta variables CSS en gradientes

**Solución:** Usar colores directos

```css
/* ❌ NO funciona */
-fx-background-color: linear-gradient(to right, -fx-primary, -fx-secondary);

/* ✅ Funciona */
-fx-background-color: linear-gradient(to right, #5B4FFF, #7C3AED);
```

---

### Problema: CSS no se carga

**Causa:** Ruta incorrecta o falta cargar en Scene

**Solución:**

```java
// En el controller o clase principal
String cssPath = getClass().getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
scene.getStylesheets().add(cssPath);
```

---

## 📦 Archivos del Design System

```
src/main/resources/org/openjfx/javaquiz/css/
├── JavaQuiz.css          # Estilos principales
└── javaCodePane.css      # Estilos para código

src/main/java/org/openjfx/javaquiz/util/
└── AnimationUtil.java    # Utilidades de animación
```

---

## 🎓 Principios de Diseño Aplicados

1. **Jerarquía Visual**: Tamaños, colores y pesos definen importancia
2. **Consistencia**: Mismos patrones en toda la aplicación
3. **Feedback Visual**: Animaciones y cambios de color comunican estado
4. **Accesibilidad**: Buen contraste (mínimo 4.5:1)
5. **Espaciado Rítmico**: Sistema de 8px crea armonía visual
6. **Semántica de Colores**: Verde=éxito, Rojo=error, Azul=acción

---

## 📚 Referencias

- **JavaFX CSS Reference**: https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/doc-files/cssref.html
- **Material Design Color**: https://material.io/design/color
- **Design System Best Practices**: https://www.designsystems.com/

---

**Última actualización:** Enero 2025  
**Versión:** 2.0  
**Mantenido por:** angel
