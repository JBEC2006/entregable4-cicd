# Catálogo de videojuegos - Entregable 4 CI/CD

Aplicación web hecha con Spring Boot para registrar videojuegos, clasificarlos por género y mostrar el catálogo con imágenes. El foco del entregable es el pipeline de CI/CD con Jenkins.

## Estructura

```
pom.xml            pom padre
juegos/            modulo para registrar y listar juegos (entidad, repositorio y servicio)
generos/           modulo para clasificar y filtrar los juegos por genero
web/               aplicacion Spring Boot, controller, vistas Thymeleaf y configuracion
scripts/deploy.bat script de despliegue a produccion con respaldo, prueba de humo y rollback
Jenkinsfile        pipeline declarativo
```

## Requisitos

- JDK 21
- Maven 3.9
- Jenkins con los plugins Pipeline, Git, JUnit, Pipeline Stage View y Workspace Cleanup

## Ambientes

| Ambiente   | Puerto | Base de datos                         |
|------------|--------|---------------------------------------|
| Jenkins    | 8080   |                                       |
| Desarrollo | 8081   | web/data/juegos-dev (H2 en archivo)   |
| Produccion | 8082   | C:\ambientes\prod\data\juegos         |

La base se inicializa con `schema.sql` y `data.sql`. Las dos usan sentencias que no borran datos (`CREATE TABLE IF NOT EXISTS` y `MERGE`), así que los juegos registrados en producción se mantienen entre despliegues.

## Correr en desarrollo

```
mvn install -DskipUnitarios=true -DskipITs
cd web
mvn spring-boot:run
```

Despues entrar a http://localhost:8081

## Tests

- Unitarios (`*Test`, con surefire): `mvn test`
- Integración (`*IT`, con failsafe): `mvn verify -DskipUnitarios=true`
- Todos: `mvn verify`

## Pipeline

1. Checkout
2. Build
3. Tests unitarios
4. Tests de integración
5. Empaquetado: genera `web/target/catalogo.jar` y lo archiva en Jenkins con fingerprint
6. Deploy a producción: corre `scripts/deploy.bat`

Jenkins revisa el repositorio cada minuto con `pollSCM` y si hay cambios corre el pipeline. Si falla cualquier stage, el pipeline se corta y producción queda con la versión anterior.

El deploy guarda el jar actual como `app-anterior.jar`, frena la versión que está corriendo, copia el jar nuevo y lo levanta con el perfil `prod`. Después hace una prueba de humo contra http://localhost:8082. Si no responde 200, restaura `app-anterior.jar`, la vuelve a levantar y el pipeline queda en rojo.

Para probar el rollback se puede usar "Build with Parameters" en Jenkins y poner en `RUTA_HUMO` una ruta que no exista, por ejemplo `/no-existe`.
