# 💼 Salud y Finanzas Pro - Enterprise Edition

¡Bienvenido a **Salud y Finanzas Pro**! Una aplicación de escritorio robusta, moderna y segura escrita en Java. Diseñada de forma corporativa simulando la escalabilidad de aplicaciones empresariales masivas.

## ✨ Características Principales (Arquitectura Pro)

- **Arquitectura Limpia (MVC):** El código está estructurado en Controladores, Modelos, DAOs y Vistas independientes, facilitando exponencialmente el mantenimiento.
- **Sistema de Seguridad (Criptografía):** Inyección de un `LoginFrame` como punto de entrada. Las credenciales se almacenan mediante hashing (`SHA-256`) asegurando que los usuarios no expongan datos sensibles.
- **Daemon Cron Jobs Simulado:** Un motor asíncrono implementado en un hilo lógico que revisa el historial cruzado al arrancar la aplicación para procesar pagos y membresías de forma recurrente/automática mes a mes.
- **Alertas Predictivas IA:** El sistema cruza activamente las gráficas de Gastos vs Límite de Presupuestos, enviando un freno restrictivo visual de alerta si pasas más del 80% del umbral definido permitiendo sanidad financiera total.
- **Diseño FlatLaf:** Abandonamos el diseño clásico `Nimbus` de Swing para portar un LookAndFeel nativo plano (Oscuro y Claro), idéntico al de los Frameworks web masIVOS.
- **Bases de Datos Separadas Relacionales:** Soporte completo en base de datos local SQLite para múltiples tablas de metadatos (`users`, `health_records`, `finance_records`, `budgets`, `savings_goals`).

## 🛠 Instalación y Ejecución

Es muy sencillo abrir y ejecutar la plataforma si tienes Java instalado en tu equipo.

### Requisitos Técnicos
* Java 8 o superior (Se recomienda Java 17 LTS).
* Maven (Build Tool).

### Levantar el Proyecto Localmente

1. **Clona el repositorio** en tu equipo local:
   ```bash
   git clone https://github.com/josedavidosorio2005/juanjo-2.0.git
   ```
2. **Entra al directorio** del software:
   ```bash
   cd juanjo-2.0
   ```
3. **Instala y Compila las Dependencias** mediante Maven:
   ```bash
   mvn clean package
   ```
4. **Inicia el Ejecutable:**
   ```bash
   java -jar target/health-finance-app-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

*(Nota: Al acceder por primera vez, utiliza el Acceso Administrativo seguro para crear la base de datos `admin` - `admin`.)*

## 📚 Stack Tecnológico

| Módulo/Librería | Propósito |
|---------|---------|
| **Java Swing + AWT** | Interfaz Gráfica Nativa de Componentes |
| **FlatLaf (Flat Dark Laf)** | Modernización Visual Global del Tema UI |
| **SQLite (Xerial JDBC)** | Base de Datos incrustada robusta y relacional |
| **JFreeChart** | Visualización de metadatos e Históricos Estadísticos |
| **Maven** | Gestión Transaccional de Librerías y Empaquetado `Fat JAR` |

> *Software diseñado pensando en escalabilidad y limpieza de patrones de diseño orientados a objetos.*
