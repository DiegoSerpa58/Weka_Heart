# Weka Diabetes Prediction API (ZeroR) - Proyecto Educativo

## Descripción 📚

Este repositorio contiene una API educativa desarrollada con Spring Boot que utiliza el framework **Weka** para realizar predicciones médicas. 

Como parte de una asignación académica, el proyecto fue adaptado para utilizar el algoritmo **ZeroR** y el dataset oficial de **Pima Indians Diabetes**.

## Características principales ✨

- **Modelo predictivo**: Implementación del algoritmo ZeroR de Weka (`weka.classifiers.rules.ZeroR`).
- **Dataset**: `diabetes.arff` (Pima Indians Diabetes Database).
- **API RESTful**: Endpoint configurado para recibir JSON con datos clínicos de pacientes y devolver predicciones.

## ¿Qué es ZeroR? 🧠

ZeroR (Zero Rules) es el algoritmo de clasificación más simple que existe. Su función es ignorar todos los atributos (variables) de entrada y **predecir siempre la clase mayoritaria** del dataset de entrenamiento. 

Aunque no es útil para diagnósticos médicos reales, en Machine Learning se utiliza como un **modelo base (baseline)**. Si un algoritmo complejo (como Random Forest o Redes Neuronales) tiene un rendimiento inferior a ZeroR, significa que no está aprendiendo nada útil de los datos.

## Descripción de los Campos (Atributos de Entrada) 📊

Los datos que la API recibe deben coincidir con la estructura del dataset de Pima Indians:

| Atributo (JSON) | Tipo de Dato | Descripción Médica |
|-----------------|--------------|--------------------|
| **preg** | Numérico | Número de embarazos |
| **plas** | Numérico | Concentración de glucosa en plasma (a 2 horas) |
| **pres** | Numérico | Presión arterial diastólica (mm Hg) |
| **skin** | Numérico | Grosor del pliegue cutáneo del tríceps (mm) |
| **insu** | Numérico | Nivel de insulina sérica a 2 horas (mu U/ml) |
| **mass** | Numérico | Índice de Masa Corporal (IMC) |
| **pedi** | Numérico | Función de pedigrí de la diabetes (historial familiar) |
| **age** | Numérico | Edad del paciente |
| **class** | Nominal | `tested_positive` o `tested_negative` *(Variable a predecir)* |

## Ejemplo de Uso (cURL) 💻

**Petición POST:**
```bash
curl -X POST http://localhost:8080/api/prediction \
-H "Content-Type: application/json" \
-d "{\"preg\":6,\"plas\":148,\"pres\":72,\"skin\":35,\"insu\":0,\"mass\":33.6,\"pedi\":0.627,\"age\":50}"
```
**Petición POST con PowerShell:**
```bash
$body = @{ preg=6; plas=148; pres=72; skin=35; insu=0; mass=33.6; pedi=0.627; age=50 } | ConvertTo-Json -Compress
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/prediction" -ContentType "application/json" -Body $body
```

**Respuesta de la API:**
```text
Predicción ZeroR: tested_negative (Nota: ZeroR siempre predice la clase mayoritaria).
```
*(Nota: Independientemente de los valores numéricos enviados, ZeroR responderá basándose únicamente en la tendencia histórica del archivo `.arff`)*.

## Tecnologías utilizadas 🛠️

- **Backend**: Java 17, Spring Boot 3.x
- **Machine Learning**: Weka 3.8.6
- **Herramientas**: Maven

## Configuración y Ejecución ⚙️

1. **Clonar repositorio**:
   ```bash
   git clone https://github.com/DiegoSerpa58/Weka_Heart.git
   ```
2. **Abrir en IDE**: Abrir la carpeta en Visual Studio Code o IntelliJ IDEA.
3. **Ejecutar**: Correr el archivo principal `WekaHeartApplication.java`. El servidor iniciará en el puerto `8080`.
