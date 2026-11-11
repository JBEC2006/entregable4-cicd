# Catálogo de videojuegos - Entregable 4 CI/CD

Aplicación web hecha con Spring Boot para registrar videojuegos, clasificarlos por género y mostrar el catálogo con imágenes. El foco del entregable es el pipeline de CI/CD con Jenkins.

Repositorio: https://github.com/JBEC2006/entregable4-cicd

## Estructura

```
pom.xml            pom padre
juegos/            módulo para registrar y listar juegos (entidad, repositorio y servicio)
generos/           módulo para clasificar y filtrar los juegos por género
web/               aplicación Spring Boot, controller, vistas Thymeleaf y configuracion
scripts/deploy.bat script de despliegue a producción con respaldo, prueba de humo y rollback
Jenkinsfile        pipeline declarativo
```

## Requisitos

- JDK 21
- Maven 3.9
- Jenkins con los plugins Pipeline, Git, JUnit, Pipeline Stage View y Workspace Cleanup
- Windows, porque el pipeline y el deploy usan comandos `bat`

## Ambientes

| Ambiente   | Puerto | Base de datos                         |
|------------|--------|---------------------------------------|
| Jenkins    | 8080   |                                       |
| Desarrollo | 8081   | web/data/juegos-dev (H2 en archivo)   |
| Producción | 8082   | C:\ambientes\prod\data\juegos         |

La base se inicializa con `schema.sql` y `data.sql`. Las dos usan sentencias que no borran datos (`CREATE TABLE IF NOT EXISTS` y `MERGE`), así que los juegos registrados en producción se mantienen entre despliegues.

## Correr en desarrollo

```
mvn install -DskipUnitarios=true -DskipITs
cd web
mvn spring-boot:run
```

Después entrar a http://localhost:8081

## Tests

- Unitarios (`*Test`, con surefire): `mvn test`
- Integración (`*IT`, con failsafe): `mvn verify -DskipUnitarios=true`
- Todos: `mvn verify`

## Configurar Jenkins

1. Jenkins tiene que encontrar JDK 21, Maven y curl en el PATH. Si en la máquina hay otro Java instalado, hay que setear `JAVA_HOME` al JDK 21 y ponerlo primero en el PATH antes de levantar Jenkins. curl ya viene con Windows 10 y 11.
2. Crear el job desde New Item > Pipeline.
3. En la sección Pipeline elegir "Pipeline script from SCM", con SCM Git, la URL https://github.com/JBEC2006/entregable4-cicd.git, la rama `*/main` y el Script Path `Jenkinsfile`. No hacen falta credenciales porque el repo es público.
4. Correr el primer build a mano con "Build Now". Jenkins registra el `pollSCM` y el parámetro `RUTA_HUMO` del Jenkinsfile recién después de ese build. A partir de ahí cada push a `main` dispara el pipeline solo. Los push a otras ramas no lo disparan.

No hace falta crear `C:\ambientes\prod`, porque `deploy.bat` la crea si no existe. El usuario con el que corre Jenkins tiene que poder escribir en esa carpeta.

## Pipeline

1. Checkout
2. Build
3. Tests unitarios
4. Tests de integración: además de correr los tests, `mvn verify` genera `web/target/catalogo.jar`
5. Empaquetado: archiva ese jar en Jenkins con fingerprint, así el jar se construye una sola vez
6. Deploy a producción: corre `scripts/deploy.bat`

Jenkins revisa el repositorio cada minuto con `pollSCM` y si hay cambios corre el pipeline. Si falla cualquier stage, el pipeline se corta y producción queda con la versión anterior.

El deploy guarda el jar actual como `app-anterior.jar`, frena la versión que está corriendo, copia el jar nuevo y lo levanta con el perfil `prod`. Después hace una prueba de humo contra http://localhost:8082. Si no responde 200, restaura `app-anterior.jar`, la vuelve a levantar y el pipeline queda en rojo.

Para probar el rollback se puede usar "Build with Parameters" en Jenkins y poner en `RUTA_HUMO` una ruta que no exista, por ejemplo `/no-existe`.
