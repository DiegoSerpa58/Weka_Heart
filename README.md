## Configuración y Ejecución

### Prerrequisitos

Antes de ejecutar la versión web del proyecto, asegúrate de tener lo siguiente instalado:
- **Java 17**: Este proyecto requiere Java 17. Puedes descargarlo desde el [sitio oficial de Java](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).
- **Maven**: Se utiliza para gestionar las dependencias del proyecto. Puedes descargar Maven desde su [sitio oficial](https://maven.apache.org/download.cgi).
- (Opcional) **Docker**: Si prefieres ejecutar la aplicación en un contenedor, necesitarás Docker. Puedes descargarlo desde [aquí](https://www.docker.com/products/docker-desktop).

### Clonación del Repositorio

1. Abre una terminal.
2. Clona el repositorio ejecutando el siguiente comando:
   ```bash
   git clone https://github.com/DiegoSerpa58/Weka_Heart.git
   ```

### Ejecutar con Maven

1. Navega al directorio del proyecto:
   ```bash
   cd Weka_Heart
   ```
2. Ejecuta el siguiente comando para iniciar la aplicación:
   ```bash
   mvn spring-boot:run
   ```
   o si quieres usar el wrapper de Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

### Ejecutar desde un IDE

Puedes ejecutar el proyecto desde un IDE como IntelliJ o Visual Studio Code. Solo necesitas importar el proyecto y ejecutar la clase principal que se encuentra en:
 `src/main/java/com/tu_paquete/TuClasePrincipal.java`.  Asegúrate de que está configurado para usar Java 17.

### Ejecutar con Docker

1. Construir la imagen de Docker:
   ```bash
   docker build -t nombre-de-tu-imagen .
   ```
2. Ejecutar el contenedor de Docker:
   ```bash
   docker run -p 8080:8080 nombre-de-tu-imagen
   ```

### Acceso a la Aplicación

1. Abre tu navegador web.
2. Ingresa la siguiente dirección en la barra de direcciones:
   ```
   http://localhost:8080
   ```
3. Si todo está funcionando correctamente, deberías ver que el dashboard de la aplicación se carga.

### Verificación

Para asegurarte de que todo está funcionando correctamente, verifica que la página de inicio del dashboard se muestra en tu navegador y que no hay errores en la consola.

Disfruta usando la aplicación!