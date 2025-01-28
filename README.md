# PRÁCTICA 4: Listas de la Compra

## Descripción
Este repositorio contiene la solución a la **PRÁCTICA 4: Listas de la Compra**, que tiene como objetivo desarrollar una aplicación Android para gestionar listas de la compra. La aplicación incluye características como la creación y consulta de listas, gestión de productos, y opciones para compartir las listas con contactos simulando el envío de SMS.  

La aplicación está diseñada utilizando una plantilla de **Navigation Drawer Views Activity** con fragmentos para cada funcionalidad principal, integrando tecnologías como **SQLite**, **RecyclerView** y el manejo asíncrono de recursos mediante **ExecutorService**.

---

## Funcionalidades principales
### 1. Crear nueva lista
- Permite crear una lista de la compra desde cero.
- Incluye una actividad para agregar productos, seleccionando cantidades de un listado existente.
  
### 2. Consultar listas de la compra
- Muestra un diálogo con las listas recientes para consultar, modificar o eliminar su contenido.

### 3. Crear nuevo artículo
- Permite crear nuevos artículos, incluyendo una imagen gráfica del producto para la lista.

### 4. Compartir lista
- Presenta un listado de contactos y permite simular el envío de SMS con la lista de la compra.
- Incluye opción de programar el envío con una alarma y recibir notificaciones al completarse.

---

## Requisitos técnicos
- **Nivel de API mínima:** 28  
- **Base de datos:** SQLite, diseñada para almacenar listas, productos y estadísticas de uso.  
- **Componentes utilizados:**
  - **RecyclerView**: Para mostrar listas de compra, productos y contactos.
  - **ExecutorService**: Para la carga asíncrona de imágenes y datos.
  - **HttpURLConnection**: Para cargar datos JSON desde internet y descargar imágenes.
  - **Modal Window**: Para simular el envío de SMS.

---

## Implementación
### Base de datos
- La información se almacena en **SQLite**, incluyendo:
  - Tablas para listas, productos, y estadísticas de uso.
  - Relación entre productos y listas.
  - Actualización automática del número de veces que un producto aparece en una lista.
  
### Carga inicial de datos
- Si la base de datos está vacía, se realiza una petición HTTP al archivo JSON en:
  `https://fp.cloud.riberadeltajo.es/listacompra/listaproductos.json`
- Las imágenes se obtienen desde:
  `https://fp.cloud.riberadeltajo.es/listacompra/images/`.

### Interfaz de usuario
- Diseñada para ser intuitiva y fluida, con el uso de fragmentos para cada funcionalidad principal.
- **Navigation Drawer** para la navegación entre las opciones.

### Simulación del envío de SMS
- Implementada mediante una ventana modal que confirma el envío exitoso del mensaje.

---

## Instrucciones de uso
1. Clona este repositorio en tu entorno de desarrollo.
2. Abre el proyecto en **Android Studio** (versión mínima recomendada: 2020.3).
3. Ejecuta la aplicación en un emulador o dispositivo físico con nivel de API 28 o superior.
4. Interactúa con las funcionalidades de la aplicación desde el menú de navegación.

---

## Archivos incluidos en la entrega
1. **Proyecto Android**: Código fuente completo de la aplicación comprimido en un archivo `.zip`.
2. **Memoria técnica**: Un archivo PDF que incluye:
   - Portada e índice.
   - Esquema de la base de datos (tablas y cardinalidades).
   - Detalles de implementación (clases utilizadas, diagramas de flujo).
   - Capturas de pantalla del funcionamiento de la aplicación.

---

## Requisitos de evaluación
- La aplicación cumple con todos los requisitos planteados en el enunciado:
  - Carga de datos desde un archivo JSON externo.
  - Gestión de imágenes mediante carga asíncrona.
  - Uso de RecyclerView para mostrar datos.
  - Simulación del envío de SMS con ventana modal.
  - Almacenamiento y actualización de datos en SQLite.

---

## Recursos adicionales
- [Documentación de Navigation Drawer](https://developer.android.com/guide/navigation/navigation-ui)
- [Carga de imágenes con HttpURLConnection](https://developer.android.com/reference/java/net/HttpURLConnection)

---

## Autor
**Overdrive80**  
_Resolución de la PRÁCTICA 4: Listas de la Compra_  
