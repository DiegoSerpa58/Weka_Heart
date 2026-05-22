# Weka Heart – Web Dashboard & ML Analysis Platform 🩺📊

**Una plataforma web flexible para análisis de Machine Learning basada en Apache Weka.**

---

## 📋 Descripción

**Weka Heart** es una aplicación web profesional que proporciona una interfaz intuitiva y potente para el análisis y clasificación de datos utilizando múltiples algoritmos de Machine Learning. La plataforma permite cargar datasets personalizados y comparar el rendimiento de diferentes algoritmos de forma interactiva.

### Características principales

- 🔄 **Datasets personalizados** – Carga archivos `.arff` o `.csv` de cualquier fuente
- 🧠 **Múltiples algoritmos** – Selecciona entre 9 algoritmos de clasificación
- 📊 **Métricas detalladas** – Visualiza precisión, recall, F-measure y más
- 🎯 **Evaluación flexible** – Cross-validation o Percentage Split
- 🚀 **API REST** – Integración para aplicaciones externas
- 🐳 **Dockerizado** – Despliegue en contenedores sin dependencias locales

---

## 🧠 Algoritmos Disponibles

La plataforma soporta los siguientes 9 algoritmos de clasificación:

| Algoritmo | Tipo | Descripción |
|-----------|------|-----------|
| **ZeroR** | Baseline | Predice la clase mayoritaria. Utilizado como referencia base |
| **OneR** | Lazy | Genera un clasificador de una sola regla basado en el mejor atributo |
| **Naive Bayes** | Probabilístico | Clasificador probabilístico usando el teorema de Bayes con independencia de atributos |
| **Random Forest** | Ensemble | Algoritmo tipo Random Forest para la demostración |
| **Regresión** | Lineal | Regresión lineal adaptada a clasificación |
| **R.Logística** | Lineal | Regresión logística para clasificación supervisada |
| **Series** | Temporal | Clasificador por márgenes para demostración de series |
| **Kmeans** | Clustering | Demostración inspirada en Kmeans usando vecinos cercanos |
| **EM** | Probabilístico | Demostración inspirada en EM con red bayesiana |

> Todos los algoritmos están integrados con **Apache Weka 3.8.x**

---

## 🛠️ Tecnologías

### Backend
- **Java 17** – Lenguaje principal
- **Spring Boot 3.x** – Framework web
- **Apache Weka 3.8.x** – Motor de Machine Learning
- **Maven** – Gestor de dependencias

### Frontend
- **HTML5** – Estructura
- **CSS3** – Estilos y responsividad
- **JavaScript (Vanilla)** – Interactividad sin dependencias

### Infraestructura
- **Docker** – Contenedorización (opcional)
- **Docker Compose** – Orquestación

---

## 📦 Requisitos Previos

Asegúrate de tener instalado:

| Requisito | Versión | Obligatorio |
|-----------|---------|-----------|
| Java JDK | 17+ | ✅ Sí |
| Maven | 3.8+ | ⚠️ Opcional* |
| Docker | Latest | ⚠️ Opcional |

*Si usas `mvnw` (Maven Wrapper), no necesitas instalar Maven globalmente.

### Verificar instalación

```bash
java -version           # Debe mostrar Java 17+
mvn -version           # Si Maven está instalado globalmente
```

---

## 🚀 Instalación y Ejecución

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/DiegoSerpa58/Weka_Heart.git
cd Weka_Heart
```

### 2️⃣ Ejecutar con Maven Wrapper (Recomendado)

#### Windows
```bash
mvnw.cmd spring-boot:run
```

#### Linux / macOS
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### 3️⃣ Ejecutar con Maven global

```bash
mvn clean install
mvn spring-boot:run
```

### 4️⃣ Ejecutar con Docker

```bash
# Construir la imagen
docker build -t weka-heart .

# Ejecutar el contenedor
docker run -p 8080:8080 weka-heart
```

---

## 💻 Acceder a la Aplicación

Una vez que el servidor está corriendo, abre tu navegador en:

```
http://localhost:8080/
```

### Flujo de trabajo principal

1. **📁 Cargar dataset**
   - Arrastra un archivo `.arff` o `.csv` a la zona de carga
   - O selecciona mediante el navegador de archivos

2. **🧪 Seleccionar algoritmo**
   - Elige entre los 9 algoritmos disponibles:
     - **ZeroR** – Baseline para comparación
     - **OneR** – Clasificador de una regla
     - **Naive Bayes** – Probabilístico independiente
     - **Random Forest** – Ensemble
     - **Regresión** – Lineal adaptada
     - **R.Logística** – Regresión logística
     - **Series** – Temporal/Márgenes
     - **Kmeans** – Clustering
     - **EM** – Probabilístico bayesiano
   - La tarjeta se resaltará al seleccionar

3. **⚙️ Configurar parámetros de evaluación**
   - Elige entre **Cross-Validation** o **Percentage Split**
   - Especifica `folds` (para CV), `trainPercent` (para Split) y `seed`

4. **▶️ Ejecutar análisis**
   - Haz clic en "Run / Ejecutar"
   - Visualiza las métricas en tiempo real

5. **📊 Analizar resultados**
   - Precision, Recall, F-Measure
   - Matriz de confusión
   - Tiempo de entrenamiento

---

## 🔗 API REST

Además del dashboard web, la plataforma expone una API REST para integración con otras aplicaciones.

### Endpoints principales

#### Cargar y clasificar dataset
```http
POST /api/classify
Content-Type: application/json

{
  "algorithm": "NaiveBayes",
  "evaluationMethod": "CrossValidation",
  "folds": 10,
  "seed": 42,
  "fileContent": "..."  // contenido del archivo
}
```

**Respuesta:**
```json
{
  "algorithm": "NaiveBayes",
  "accuracy": 0.85,
  "precision": 0.82,
  "recall": 0.88,
  "fmeasure": 0.85,
  "executionTime": 1234,
  "confusionMatrix": [[...], [...]]
}
```

#### Predicción individual
```http
POST /api/prediction
Content-Type: application/json

{
  "preg": 6,
  "plas": 148,
  "pres": 72,
  "skin": 35,
  "insu": 0,
  "mass": 33.6,
  "pedi": 0.627,
  "age": 50
}
```

---

## 📊 Formatos de Datos Soportados

### ARFF (Attribute-Relation File Format)
```
@relation diabetes
@attribute preg numeric
@attribute plas numeric
@attribute class {tested_positive, tested_negative}
@data
6,148,72,35,0,33.6,0.627,50,tested_positive
1,85,66,29,0,26.6,0.351,31,tested_negative
```

### CSV (Comma-Separated Values)
```csv
preg,plas,pres,skin,insu,mass,pedi,age,class
6,148,72,35,0,33.6,0.627,50,tested_positive
1,85,66,29,0,26.6,0.351,31,tested_negative
```

---

## 🐛 Troubleshooting

| Problema | Solución |
|----------|----------|
| `java: command not found` | Instala JDK 17+ y configura `JAVA_HOME` |
| `mvn: command not found` | Usa el Maven Wrapper: `./mvnw spring-boot:run` |
| Puerto 8080 en uso | Cambia el puerto en `application.properties`: `server.port=8081` |
| Error al cargar archivo | Verifica que sea `.arff` o `.csv` con formato válido |
| La app no responde | Comprueba los logs de Spring Boot en la consola |

---

## 📁 Estructura del Proyecto

```
Weka_Heart/
├── src/
│   ├── main/
│   │   ├── java/com/example/weka_heart/
│   │   │   ├── WekaHeartApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   └── model/
│   │   └── resources/
│   │       ├── templates/       # HTML templates
│   │       ├── static/          # CSS, JS, assets
│   │       └── application.properties
│   └── test/
├── pom.xml                       # Dependencias Maven
├── Dockerfile                    # Configuración Docker
└── mvnw / mvnw.cmd             # Maven Wrapper
```

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto se distribuye bajo licencia educativa. Consulta el archivo `LICENSE` para más detalles.

---

## 📧 Contacto y Soporte

Para reportar bugs, sugerencias o preguntas:

- 📌 **Issues en GitHub** – [Crear un issue](https://github.com/DiegoSerpa58/Weka_Heart/issues)
- 👤 **Autor** – Diego Serpa

---

## 🎓 Referencias

- [Apache Weka Documentation](https://www.cs.waikato.ac.nz/ml/weka/)
- [Spring Boot Official Guide](https://spring.io/projects/spring-boot)
- [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)

---

**Última actualización:** Mayo 2026  
**Versión:** 2.1 (9 algoritmos disponibles)
