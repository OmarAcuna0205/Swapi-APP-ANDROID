# **Resumen de Contribuciones Individuales y Experiencia del Sprint**

### Aportaciones Personales al Proyecto

Durante este sprint, mi enfoque  fue mejorar la estabilidad de la aplicación, enriquecer la experiencia de usuario y establecer las bases para la internacionalización. Mis contribuciones específicas incluyen:

* **Desarrollo de Nuevas Vistas:** Implementación completa de la vista de **"Guardados"** (SavedPostsView), permitiendo al usuario visualizar sus publicaciones favoritas.  
* **Corrección de Bugs Críticos:** Solución al *crash* fatal en el flujo de Login/Registro. Se implementó una redirección a una vista de verificación (demo) para evitar que la app se cerrara inesperadamente.  
* **Mejoras**  
  * Implementación del modificador dismissKeyboardOnClick para ocultar el teclado automáticamente al tocar fuera de los campos de texto, mejorando la usabilidad en formularios.  
  * Rediseño de la ProductCard y su adaptación para la vista de guardados.  
  * Estandarización de la identidad visual aplicando el color de marca (Swapi Blue) en botones, iconos y elementos interactivos de toda la aplicación.  
* **Internacionalización:** Configuración inicial para el soporte de múltiples idiomas en la aplicación.  
* **Documentación:** Creación e inicio del archivo README para documentar el proyecto, que anteriormente era inexistente.

### Aprendizajes durante el Desarrollo

* **Navegación Avanzada en Compose:** Profundicé en el manejo de NavHost  y cómo compartir argumentos (como URLs de imágenes) entre distintas pantallas de manera eficiente.  
* **Manejo de Estados de UI:** Aprendí a gestionar mejor los estados de carga, éxito y error dentro de los ViewModels para ofrecer una interfaz más reactiva.  
* **Gestión de Recursos:** Comprendí la importancia de centralizar colores y estilos en el tema de Compose para facilitar cambios globales de diseño.

### Dificultades Encontradas

* **Errores de Navegación:** Uno de los mayores retos fue depurar excepciones del tipo IllegalArgumentException: Navigation destination... cannot be found. Esto ocurría al intentar navegar desde el TabBar hacia una vista no registrada en ese contexto específico. Se resolvió reestructurando las rutas en el TabBarNavigationView.  
* **Integración de Imágenes:** Hubo dificultades iniciales al renderizar imágenes dinámicas desde URLs en las tarjetas de producto, lo cual se solucionó integrando y configurando correctamente la librería Coil.

### Buenas Prácticas de Git Aplicadas

Para mantener un historial de cambios limpio y un trabajo colaborativo eficiente, seguimos estrictamente un flujo de trabajo 

* **Uso de Ramas:** Cada nueva funcionalidad o corrección se desarrolló en una rama independiente con la convención feat/nombre-funcionalidad.  
  * *Ejemplos visibles en el repositorio:* feat/Global-App-Improvements-V2, feat/searchButton, feat/profile, feat/internationalization.  
* **Commits Atómicos y Semánticos:** Realicé commits que agrupan cambios lógicos con mensajes claros en inglés (e.g., *"feat: Add Saved Posts screen and apply brand theme"*), facilitando la lectura del historial.  
* **Pull Requests:** Como se evidencia en el repositorio, el código se integra a la rama principal mediante Pull Requests (PR \#12, \#11, \#10), lo que permite la revisión de código antes de la fusión.

### Compromisos para la Entrega Final

Para la siguiente fase y entrega final del proyecto, me comprometo a:

1. **Desarrollo del Backend:** Construir una API RESTful robusta utilizando **Node.js**.  
2. **Base de Datos:** Diseñar e implementar la persistencia de datos utilizando **MongoDB**.  
3. **Integración Full-Stack:** Conectar la aplicación Android (Frontend) con el nuevo Backend, reemplazando los datos simulados (mock data) por datos reales provenientes del servidor.