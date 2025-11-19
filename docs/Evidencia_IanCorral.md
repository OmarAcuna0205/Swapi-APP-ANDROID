# **Resumen de Contribuciones Individuales y Experiencia del Sprint**

### **Aportaciones Personales al Proyecto**

Durante este sprint, mi enfoque principal fue la reestructuración de la arquitectura, la implementación de funcionalidades core (búsqueda y multimedia) y correcciones tecnicas. Mis contribuciones específicas incluyen:

* **Refactorización de Arquitectura y Rutas:** Reorganización completa de la estructura de paquetes (moviendo vistas como NewPublicationView a sus módulos correctos) y corrección de rutas en el AndroidManifest y configuraciones de Gradle para solucionar crashes de ClassNotFoundException al inicio.  
* **Funcionalidad de Búsqueda Global ("Live Search"):** Implementación de una lógica de búsqueda en tiempo real en el HomeView. Esto implicó desde la TopBar hacia el HomeViewModel, permitiendo filtrar resultados dinámicamente sin necesidad de presionar "Enter" y mostrando estados de "Sin resultados".  
* **Gestión de Sesión y Navegación:**  
  * Implementación de la lógica de Logout (Cierre de sesión) conectando la vista de Perfil con el DataStore para limpiar credenciales y redirigir al Login.  
  * Corrección del MainActivity que causaba que la app saltara el Onboarding incorrectamente.  
* **Integración Multimedia:** Implementación del selector de imágenes nativo mediante ActivityResultContracts en la vista de "Crear Publicación", permitiendo al usuario abrir su galería y seleccionar fotos.  
* **Correcciones de UI/UX (WindowInsets):** Solución al problema de "doble padding" que desplazaba las vistas hacia abajo, configurando correctamente los WindowInsets en el Scaffold principal para una experiencia correcta.  
* **Internacionalización (i18n):** Extracción de cadenas de texto hardcodeadas a recursos strings.xml y creación del soporte completo para Inglés y Español en diferentes principales.

### **Aprendizajes durante el Desarrollo**

* **Separación de Lógica y Diseño (State Hoisting)** Me quedó mucho más claro por qué es importante "elevar el estado". Aprendí a dejar mis componentes visuales limpios y mover toda la lógica al ViewModel, lo que facilitó mucho conectar el buscador en tiempo real sin hacer un desastre en la vista.
* **Manejo de Insets y Scaffolds:** Aprendí a diferenciar las librerías nuevas de las viejas. Me costó un poco entender por qué se generaban espacios en blanco en la pantalla, pero al final dominé el uso de Scaffold y WindowInsets para lograr un diseño moderno de borde a borde.
* **Persistencia con DataStore:** Reforcé mucho mi comprensión sobre cómo guardar datos sencillos de forma asíncrona. Ahora entiendo bien cómo usar Flows para que la app recuerde si el usuario ya inició sesión o si ya vio la introducción.

### **Dificultades Encontradas**

* **Conflictos de Versiones (Material 2 vs 3):** Uno de los mayores retos fue depurar errores de compilación como No parameter with name 'windowInsets' found. Esto se debía a importaciones mezcladas de librerías de Material Design antiguas y nuevas, lo cual resolví limpiando los imports y estandarizando todo a Material 3\.  
* **Crash por Rutas de Actividades:** Al inicio, la refactorización de carpetas rompió la configuración de ejecución (Run Configuration), haciendo que la app crasheara al instante (ClassNotFoundException). Se solucionó sincronizando Gradle y corrigiendo las rutas relativas en el Manifest.  
* **Dependencias Faltantes:** Errores al implementar el selector de imágenes (PickVisualMedia), que requerían dependencias específicas de activity-compose que no estaban incluidas inicialmente.

### **Buenas Prácticas de Git Aplicadas**

Para mantener el orden en un sprint con múltiples refactorizaciones, apliqué el siguiente flujo:

* **Rama de Consolidación:** Trabajé sobre la rama feat/global-app-improvements para agrupar cambios estructurales que eran dependientes entre sí (como el refactor de carpetas y la navegación).  
* **Commits Descriptivos:** Utilicé mensajes de commit detallados en inglés, documentando no solo qué se hizo, sino por qué.  
* **Limpieza de Código:** Antes de confirmar cambios, me aseguré de eliminar importaciones no utilizadas y formatear el código para mantener la consistencia del estilo.

### **Compromisos para la Entrega Final**

Para la siguiente fase y entrega final del proyecto, me comprometo a:

1. **Conexión con API Real:** Migrar toda la lógica del HomeRepository (actualmente simulada o con caché local) para consumir endpoints reales del Backend en Node.js.  
2. **Autenticación JWT:** Implementar el manejo seguro de tokens de sesión en el DataStore para mantener la sesión del usuario activa de forma segura contra el Backend.  
1. **Desarrollo del Backend:** Junto con mi compañero construir una API RESTful robusta utilizando **Node.js**.  