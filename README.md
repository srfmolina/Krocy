# Krocy

> **ERP beyond your fridge — now on Android & Desktop**

Krocy is a modern, open-source, multiplatform client for [Grocy](https://grocy.info) — a self-hosted grocery and household management solution. Built with **Kotlin Multiplatform** and **Compose Multiplatform**, Krocy brings a native, consistent experience to both Android and Desktop (JVM) from a single shared codebase.

> **Note:** Krocy is a companion app. It requires a running self-hosted instance of the [Grocy server](https://grocy.info). It cannot run standalone. A demo server is available at login for testing purposes.

---

## ✨ Features

Krocy aims to cover the full feature set of the official Grocy API:

- **Stock Management** — Track your groceries and household items, including quantities and expiration dates.
- **Barcode Scanning** — Add or consume products instantly by scanning barcodes with your device camera.
- **Shopping Lists** — Create and manage shopping lists; auto-generate them based on minimum stock levels.
- **Purchases & Consumption** — Log purchases and product consumption with batch processing support.
- **Recipes** — Browse your Grocy recipes and check ingredient availability at a glance.
- **Meal Planning** — Plan daily meals and add missing ingredients to the shopping list in one tap.
- **Chores** — Manage and track household tasks and their schedules.
- **Master Data Management** — Manage products, locations, quantity units, product groups, and stores.
- **Open Food Facts integration** — Look up product information by barcode via built-in external database support.
- **Self-signed certificate support** — Connect securely to Grocy instances with custom certificates.
- **Home Assistant Add-on compatibility** — Works with Grocy running as a Home Assistant add-on.

---

## 🖥️ Supported Platforms

| Platform        | Status         |
|-----------------|----------------|
| Android (6.0+)  | ✅ In development |
| Desktop (JVM)   | ✅ In development |

> Web support is not planned for the initial release.

---

## 🛠️ Tech Stack

- [Kotlin Multiplatform (KMP)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/)
- Grocy REST API (with integrated Swagger UI at `/api`)

---

## 🚀 Build & Run

### Prerequisites

- JDK 17+
- Android SDK (for Android builds)

### Android

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Desktop (JVM)

```shell
# macOS / Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

---

## 📁 Project Structure

```
composeApp/src/
├── commonMain/    # Shared code for all platforms
├── androidMain/   # Android-specific code
└── jvmMain/       # Desktop (JVM)-specific code
```

---

## 🤝 Contributing

Contributions are welcome! If you find a bug or want to propose a new feature, please open an issue. Pull requests are highly appreciated.

---

## 📄 License

Krocy is free software distributed under the **GNU General Public License v3.0**. See [LICENSE](./LICENSE) for details.

---

## 🙏 Acknowledgements

- [Bernd Bestel](https://github.com/berrnd) — creator of [Grocy](https://grocy.info)
- [Patrick Zedler](https://github.com/patzly) — creator of [grocy-android](https://github.com/patzly/grocy-android), the main inspiration for Krocy
- The Grocy community for their continued support and contributions

---
---

# Krocy (Español)

> **ERP más allá de tu nevera — ahora en Android y Escritorio**

Krocy es un cliente moderno, de código abierto y multiplataforma para [Grocy](https://grocy.info) — una solución autoalojada de gestión de despensa y hogar. Desarrollado con **Kotlin Multiplatform** y **Compose Multiplatform**, Krocy ofrece una experiencia nativa y coherente tanto en Android como en Escritorio (JVM) desde una única base de código compartida.

> **Nota:** Krocy es una aplicación complementaria. Requiere una instancia de [servidor Grocy](https://grocy.info) autoalojada y en ejecución. No puede funcionar de forma independiente. Se dispone de un servidor de demostración en la pantalla de inicio de sesión para realizar pruebas.

---

## ✨ Funcionalidades

Krocy tiene como objetivo cubrir el conjunto completo de funciones de la API oficial de Grocy:

- **Gestión de inventario** — Controla tus alimentos y artículos del hogar, incluyendo cantidades y fechas de caducidad.
- **Escaneo de códigos de barras** — Añade o consume productos al instante escaneando con la cámara del dispositivo.
- **Listas de la compra** — Crea y gestiona listas de la compra; genera automáticamente en función del stock mínimo.
- **Compras y consumo** — Registra compras y consumo de productos con soporte de procesamiento por lotes.
- **Recetas** — Consulta tus recetas de Grocy y comprueba de un vistazo si tienes todos los ingredientes.
- **Planificación de comidas** — Planifica las comidas del día y añade los ingredientes que faltan a la lista de la compra con un toque.
- **Tareas del hogar** — Gestiona y realiza el seguimiento de las tareas domésticas y sus horarios.
- **Gestión de datos maestros** — Administra productos, ubicaciones, unidades de medida, grupos de productos y tiendas.
- **Integración con Open Food Facts** — Busca información de productos por código de barras mediante soporte integrado de base de datos externa.
- **Soporte para certificados autofirmados** — Conéctate de forma segura a instancias de Grocy con certificados personalizados.
- **Compatibilidad con el Add-on de Home Assistant** — Funciona con Grocy ejecutándose como complemento de Home Assistant.

---

## 🖥️ Plataformas soportadas

| Plataforma      | Estado              |
|-----------------|---------------------|
| Android (6.0+)  | ✅ En desarrollo     |
| Escritorio (JVM) | ✅ En desarrollo    |

> El soporte web no está previsto para la versión inicial.

---

## 🛠️ Tecnologías

- [Kotlin Multiplatform (KMP)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/)
- API REST de Grocy (con Swagger UI integrado en `/api`)

---

## 🚀 Compilar y ejecutar

### Requisitos previos

- JDK 17+
- Android SDK (para compilaciones Android)

### Android

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Escritorio (JVM)

```shell
# macOS / Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

---

## 📁 Estructura del proyecto

```
composeApp/src/
├── commonMain/    # Código compartido para todas las plataformas
├── androidMain/   # Código específico de Android
└── jvmMain/       # Código específico de Escritorio (JVM)
```

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si encuentras un error o quieres proponer una nueva funcionalidad, abre un *issue*. Los *pull requests* son muy apreciados.

---

## 📄 Licencia

Krocy es software libre distribuido bajo la **Licencia Pública General GNU v3.0**. Consulta [LICENSE](./LICENSE) para más detalles.

---

## 🙏 Agradecimientos

- [Bernd Bestel](https://github.com/berrnd) — creador de [Grocy](https://grocy.info)
- [Patrick Zedler](https://github.com/patzly) — creador de [grocy-android](https://github.com/patzly/grocy-android), la principal inspiración de Krocy
- La comunidad de Grocy por su continuo apoyo y contribuciones
