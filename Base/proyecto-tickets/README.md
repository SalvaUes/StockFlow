# SoporteHub - Gestion de Tickets de Soporte

Proyecto **100% frontend** (HTML5 + CSS3 + JavaScript ES6+ vanilla) que implementa un sistema de gestion de tickets con dashboard, Web Workers, geolocalizacion, consumo de API publica y persistencia en LocalStorage / SessionStorage / Cookies.

## Estructura

```
proyecto-tickets/
├── index.html
├── .gitignore
├── README.md
├── styles/
│   ├── main.css           # Variables, reset y layout global
│   └── components.css     # Tarjetas, botones, formularios, toasts
├── js/
│   ├── app.js             # Punto de entrada y orquestacion
│   ├── modules/
│   │   ├── crud.js        # Crear, editar, eliminar, listar y validaciones
│   │   ├── storage.js     # LocalStorage, SessionStorage y Cookies
│   │   ├── apiGeo.js      # Fetch a Open-Meteo + geolocalizacion + reverse geocoding
│   │   └── dashboard.js   # Renderizado de metricas y conexion con el worker
│   └── workers/
│       └── processor.worker.js   # Calculo de stats fuera del hilo principal
└── assets/                # Recursos estaticos (iconos, imagenes)
```

## Funcionalidades

- **CRUD completo** de tickets con validaciones y bloques `try/catch`.
- **Dashboard funcional**: metricas, distribucion por prioridad, top urgentes, geolocalizacion + clima en vivo, exportacion CSV, importacion JSON, notificaciones del navegador, datos demo y limpieza total.
- **Web Worker** para calculos de estadisticas sin bloquear la UI.
- **Geolocalizacion HTML5** + API publica **Open-Meteo** (clima) y **BigDataCloud** (reverse geocoding).
- **Persistencia** con LocalStorage (tickets), SessionStorage (filtros) y Cookies (ultima pestana).
- **Responsive** con CSS Grid, Flexbox y variables nativas.
- **Manejo global de errores** via `window.error` y `unhandledrejection`.

## Como ejecutar localmente

Al usar modulos ES6 y Web Workers, debe servirse desde un servidor HTTP (no `file://`):

```bash
# Opcion 1: Python
python3 -m http.server 8080

# Opcion 2: Node
npx serve .
```

Luego abrir `http://localhost:8080`.

## Despliegue

Compatible directamente con **GitHub Pages** o **Netlify**: subir la carpeta y publicar. No requiere build ni backend.
