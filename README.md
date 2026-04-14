# Weka Heart – Dashboard Web + API (ZeroR, OneR, Naive Bayes) 🩺📊

## Descripción 📚

Este proyecto es una **aplicación web** con **API REST** desarrollada con:

- **Java 17 + Spring Boot**
- **Apache Weka** para Machine Learning
- **HTML, CSS y JavaScript** para el dashboard web

El objetivo es **explorar algoritmos clásicos de clasificación** sobre el dataset de diabetes de Pima Indians, de forma educativa.

Desde el navegador se puede:

- Subir un dataset (`.arff` o `.csv`)
- Elegir un algoritmo de clasificación
- Obtener métricas y resultados de manera visual

---

## Algoritmos incluidos 🧠

Actualmente el proyecto trabaja con varios algoritmos de Weka:

### ZeroR

- Es el modelo más simple posible.
- Ignora todos los atributos de entrada.
- Siempre predice la **clase mayoritaria** del dataset.
- Se utiliza como **baseline**: si un modelo más complejo no supera a ZeroR, hay un problema.

### OneR

- Genera una única regla basada en **un solo atributo**.
- Para cada valor de ese atributo, asigna la clase más frecuente observada.
- Es muy simple, pero ya tiene en cuenta la información de los datos.

### Naive Bayes

- Clasificador probabilístico basado en el teorema de Bayes.
- Asume independencia entre atributos.
- Suele funcionar sorprendentemente bien en muchos problemas reales y sirve como un **modelo base fuerte**.

El dashboard web permite **seleccionar cualquiera de estos algoritmos** y comparar resultados.

---

## Dataset utilizado 📊

Se utiliza el dataset:

- **Pima Indians Diabetes Database**
- Archivo: `diabetes.arff`

Atributos principales:

| Atributo (JSON) | Tipo de Dato | Descripción Médica                                 |
|-----------------|--------------|----------------------------------------------------|
| **preg**        | Numérico     | Número de embarazos                                |
| **plas**        | Numérico     | Glucosa en plasma a 2 horas                       |
| **pres**        | Numérico     | Presión arterial diastólica (mm Hg)               |
| **skin**        | Numérico     | Grosor del pliegue cutáneo del tríceps (mm)       |
| **insu**        | Numérico     | Nivel de insulina sérica a 2 horas (mu U/ml)      |
| **mass**        | Numérico     | Índice de Masa Corporal (IMC)                     |
| **pedi**        | Numérico     | Función de pedigrí de la diabetes (historial)     |
| **age**         | Numérico     | Edad del paciente                                 |
| **class**       | Nominal      | `tested_positive` o `tested_negative` (a predecir)|

---

## Tecnologías utilizadas 🛠️

- **Backend**
  - Java 17
  - Spring Boot 3.x
  - Apache Weka 3.8.x
- **Frontend**
  - HTML
  - CSS
  - JavaScript (sin frameworks)
- **Build y dependencias**
  - Maven
- **Contenedores (opcional)**
  - Docker

---

## 1. Requisitos previos (para principiantes) ✅

Antes de intentar ejecutar el proyecto, asegúrate de tener:

1. **Java 17 (JDK 17)**  
   - Comprobar en la terminal:
     ```bash
     java -version
     ```
     Debería mostrar `17` o superior (pero este proyecto está pensado para 17).

2. **Maven** (si vas a compilar sin Docker)  
   - Comprobar:
     ```bash
     mvn -version
     ```

3. (Opcional) **Docker**  
   - Solo si quieres ejecutar la aplicación dentro de un contenedor, sin instalar Java/Maven localmente.

4. Un editor/IDE (recomendado)
   - IntelliJ IDEA
   - VS Code (con extensiones de Java)
   - Eclipse

---

## 2. Clonar el proyecto 🧬

### Opción A: Usando Git (recomendado)

1. Abrir una terminal (Command Prompt / PowerShell en Windows, Terminal en Linux/macOS).
2. Ejecutar:

   ```bash
   git clone https://github.com/DiegoSerpa58/Weka_Heart.git
   cd Weka_Heart
   ```

### Opción B: Descargar ZIP desde GitHub

1. Ir al repositorio en GitHub.
2. Clicar en **Code → Download ZIP**.
3. Extraer el `.zip`.
4. Abrir una terminal dentro de la carpeta extraída (`Weka_Heart`).

---

## 3. Ejecutar la aplicación con Maven (forma sencilla) 🚀

> Estos pasos asumen que ya estás dentro de la carpeta del proyecto `Weka_Heart`.

### 3.1. Usando el wrapper de Maven (sin instalar Maven globalmente)

#### En Windows

1. Abrir **PowerShell** o **Command Prompt** en la carpeta del proyecto.
2. Ejecutar:

   ```bash
   mvnw.cmd spring-boot:run
   ```

#### En Linux / macOS

1. Abrir **Terminal** en la carpeta del proyecto.
2. Dar permisos al wrapper (solo la primera vez):

   ```bash
   chmod +x mvnw
   ```

3. Ejecutar:

   ```bash
   ./mvnw spring-boot:run
   ```

### 3.2. Usando Maven instalado globalmente

Si ya tienes Maven instalado y configurado:

```bash
mvn spring-boot:run
```

### 3.3. ¿Cómo sé que arrancó bien?

En la consola deberías ver mensajes de Spring Boot y algo similar a:

```text
Started WekaHeartApplication in X.XXX seconds
```

Y que está escuchando en el puerto `8080`.

---

## 4. Ejecutar desde un IDE (IntelliJ / VS Code / Eclipse) 💻

1. Abrir el IDE.
2. Importar el proyecto como **Maven Project** (o “Open Existing Project” apuntando a la carpeta `Weka_Heart`).
3. Buscar la clase principal:

   ```text
   src/main/java/com/example/weka_heart/WekaHeartApplication.java
   ```

4. Hacer clic derecho sobre `WekaHeartApplication` → **Run 'WekaHeartApplication'**.
5. Esperar a que Spring Boot levante.
6. Abrir en el navegador:

   ```text
   http://localhost:8080/
   ```

---

## 5. Abrir y usar el dashboard web 🌐

Con la aplicación ya corriendo:

1. Abrir un navegador (Chrome, Edge, Firefox…).
2. Ir a:

   ```text
   http://localhost:8080/
   ```

3. Deberías ver el **Classification Dashboard** con:
   - Un área para subir un dataset.
   - Una sección con tarjetas de algoritmos (ZeroR, OneR, Naive Bayes, etc.).
   - Un panel de resultados/métricas.

### Flujo básico dentro del dashboard

1. **Subir un dataset**
   - Arrastra un archivo `.arff` o `.csv` a la zona de “Drop zone”,  
     o haz clic para seleccionar el archivo desde el explorador.
   - Si el tipo de archivo no es válido, verás un mensaje de error.

2. **Elegir un algoritmo**
   - En la sección de algoritmos, haz clic en uno:
     - ZeroR
     - OneR
     - Naive Bayes
   - La tarjeta seleccionada quedará resaltada.

3. **Elegir método de evaluación**
   - Puedes alternar entre:
     - **Cross-validation (k-folds)**
     - **Percentage split** (porcentaje de entrenamiento)
   - Completar:
     - `folds` (por ejemplo, 10)
     - `trainPercent` (por ejemplo, 70)
     - `seed` (un número entero)

4. **Ejecutar la clasificación**
   - Pulsar el botón de **Run / Ejecutar**.
   - Aparecerá un indicador de carga mientras el backend procesa.
   - Al finalizar, se muestran métricas y resultados en el panel de resultados.

---

## 6. Probar el backend por API (opcional) 🔗

Además del dashboard, el backend expone endpoints REST.

Ejemplo sencillo de petición `POST` (usando uno de los modelos que espera parámetros numéricos):

```bash
curl -X POST http://localhost:8080/api/prediction \
-H "Content-Type: application/json" \
-d "{\"preg\":6,\"plas\":148,\"pres\":72,\"skin\":35,\"insu\":0,\"mass\":33.6,\"pedi\":0.627,\"age\":50}"
```

La respuesta será un texto con la predicción, y dependiendo del algoritmo seleccionado en el servicio, puede mostrar:

- La clase predicha (`tested_positive` / `tested_negative`).
- Mensajes adicionales según tu implementación.

**Nota:**  
Cuando se usa **ZeroR**, la predicción depende solo de la clase mayoritaria del dataset.  
Con **OneR** y **Naive Bayes**, la predicción sí depende de los atributos enviados.

---

## 7. Ejecutar con Docker (opcional) 🐳

Si no quieres instalar Java ni Maven localmente, pero sí tienes Docker, puedes ejecutar así:

1. Desde la carpeta del proyecto, construir la imagen:

   ```bash
   docker build -t weka-heart .
   ```

2. Ejecutar el contenedor:

   ```bash
   docker run --rm -p 8080:8080 weka-heart
   ```

3. Abrir en el navegador:

   ```text
   http://localhost:8080/
   ```

Mientras el contenedor esté corriendo, la app estará disponible en ese puerto.

---

## 8. Errores comunes y cómo resolverlos 🩹

- **`java: command not found` o versión < 17**  
  → Instalar **JDK 17**, actualizar variables de entorno y comprobar con `java -version`.

- **`mvn: command not found`**  
  → Instalar Maven o usar el wrapper:
  - Windows: `mvnw.cmd spring-boot:run`
  - Linux/macOS: `./mvnw spring-boot:run`

- **El navegador no abre nada en `http://localhost:8080`**  
  - Verificar que la app sigue corriendo en la consola (sin errores).
  - Comprobar que ningún otro programa esté usando el puerto 8080.

- **Error al subir archivo**  
  - Asegurarse de que el archivo sea `.arff` o `.csv`.
  - Revisar que el archivo tenga el formato esperado por Weka.

---

## 9. Resumen rápido para tus compañeros 🧾

1. Instalar **Java 17** (y opcionalmente **Maven** o **Docker**).
2. Clonar el repo:
   ```bash
   git clone https://github.com/DiegoSerpa58/Weka_Heart.git
   cd Weka_Heart
   ```
3. Ejecutar (Windows):
   ```bash
   mvnw.cmd spring-boot:run
   ```
   Ejecutar (Linux/macOS):
   ```bash
   chmod +x mvnw
   ./mvnw spring-boot:run
   ```
4. Abrir en el navegador:
   ```text
   http://localhost:8080/
   ```
5. Subir dataset, elegir algoritmo (**ZeroR, OneR o Naive Bayes**) y ejecutar.

Con estos pasos, cualquier compañero sin mucha experiencia debería poder levantar y probar la aplicación sin problemas.
