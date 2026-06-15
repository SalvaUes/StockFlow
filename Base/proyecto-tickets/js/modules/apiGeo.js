// ===============================================================
// apiGeo.js - Geolocalizacion del navegador y consumo de APIs
// publicas (Open-Meteo para clima y BigDataCloud para reverse geo).
// ===============================================================

// Solicita la posicion actual del usuario via navigator.geolocation
export function getCurrentPosition() {
  return new Promise((resolve, reject) => {                 // Envuelve en promesa
    if (!('geolocation' in navigator)) {                    // Verifica soporte
      return reject(new Error('Geolocalizacion no soportada por el navegador')); // Rechaza
    }
    navigator.geolocation.getCurrentPosition(               // Llama a la API nativa
      pos => resolve({                                      // Caso exito
        lat: pos.coords.latitude,                           // Latitud
        lon: pos.coords.longitude,                          // Longitud
        accuracy: pos.coords.accuracy,                      // Precision en metros
      }),
      err => reject(err),                                   // Caso error: propaga
      { timeout: 10000, enableHighAccuracy: false, maximumAge: 60000 } // Opciones
    );
  });
}

// Consulta el clima actual usando la API publica Open-Meteo (sin API key)
export async function fetchWeather(lat, lon) {
  try {                                                     // Try/catch para errores de red
    const url = `https://api.open-meteo.com/v1/forecast`     // Endpoint base
      + `?latitude=${lat}&longitude=${lon}`                  // Coordenadas
      + `&current=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m`; // Variables
    const res = await fetch(url);                            // Peticion HTTP
    if (!res.ok) throw new Error(`HTTP ${res.status}`);     // Error si no OK
    return await res.json();                                // Devuelve JSON parseado
  } catch (err) {                                           // Captura cualquier fallo
    console.error('[apiGeo] Error obteniendo clima:', err); // Log
    throw err;                                              // Re-lanza para que el caller decida
  }
}

// Reverse geocoding gratuito (BigDataCloud) para obtener ciudad y pais
export async function reverseGeocode(lat, lon) {
  try {                                                     // Try defensivo
    const url = `https://api.bigdatacloud.net/data/reverse-geocode-client`
      + `?latitude=${lat}&longitude=${lon}&localityLanguage=es`; // URL con coords
    const res = await fetch(url);                            // Peticion HTTP
    if (!res.ok) throw new Error(`HTTP ${res.status}`);     // Error si no OK
    return await res.json();                                // Devuelve JSON
  } catch (err) {                                           // Captura
    console.error('[apiGeo] Error en reverseGeocode:', err);// Log
    return null;                                             // Devuelve null en lugar de fallar
  }
}

// Tabla de descripciones humanas para los codigos de Open-Meteo
const WEATHER_CODES = {
  0: 'Despejado', 1: 'Mayormente despejado', 2: 'Parcialmente nublado', 3: 'Nublado',
  45: 'Niebla', 48: 'Niebla con escarcha',
  51: 'Llovizna ligera', 53: 'Llovizna', 55: 'Llovizna densa',
  61: 'Lluvia ligera', 63: 'Lluvia', 65: 'Lluvia fuerte',
  71: 'Nieve ligera', 73: 'Nieve', 75: 'Nieve fuerte',
  80: 'Chubascos', 81: 'Chubascos fuertes', 82: 'Chubascos violentos',
  95: 'Tormenta', 96: 'Tormenta con granizo', 99: 'Tormenta fuerte',
};

// Devuelve la descripcion legible para un codigo de clima
export function describeWeather(code) {
  return WEATHER_CODES[code] || 'Condicion desconocida';   // Fallback seguro
}
