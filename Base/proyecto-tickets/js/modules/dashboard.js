// ===============================================================
// dashboard.js - Renderizado de metricas, conexion con el Web
// Worker y composicion del panel de geolocalizacion/clima.
// ===============================================================

import { getCurrentPosition, fetchWeather, reverseGeocode, describeWeather } from './apiGeo.js'; // APIs externas

// Referencia al worker (singleton dentro del modulo)
let worker = null;

// Inicializa el Web Worker y registra el callback de resultados
export function initWorker(onResult) {
  try {                                                     // Try defensivo
    worker = new Worker(new URL('../workers/processor.worker.js', import.meta.url), { type: 'module' }); // Crea worker
    worker.onmessage = (e) => onResult(e.data);             // Cada mensaje del worker
    worker.onerror = (err) => console.error('[dashboard] Worker error:', err); // Errores
  } catch (err) {                                           // Captura
    console.error('[dashboard] No se pudo iniciar el Web Worker:', err); // Log
  }
}

// Envia los tickets al worker para procesar estadisticas
export function processStats(tickets) {
  if (!worker) return;                                      // Si no existe, sale
  worker.postMessage({ tickets });                          // Envia datos
}

// Pequena utilidad de escape para evitar inyeccion HTML
function escapeHtml(s = '') {
  return String(s).replace(/[&<>"']/g, c => (              // Reemplaza caracteres peligrosos
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c] // Tabla
  ));
}

// Pinta las metricas y barras de prioridad recibidas del worker
export function renderMetrics(stats) {
  if (!stats || !stats.ok) return;                          // Defensa
  document.getElementById('m-total').textContent = stats.total;            // Total
  document.getElementById('m-abierto').textContent = stats.byEstado.abierto;       // Abiertos
  document.getElementById('m-progreso').textContent = stats.byEstado.en_progreso;  // En progreso
  document.getElementById('m-cerrado').textContent = stats.byEstado.cerrado;       // Cerrados

  const max = Math.max(1, ...Object.values(stats.byPrioridad)); // Maximo para escalar
  const bars = document.getElementById('priority-bars');     // Contenedor de barras
  bars.innerHTML = ['critica', 'alta', 'media', 'baja'].map(p => { // Recorre prioridades
    const v = stats.byPrioridad[p];                          // Cantidad
    const pct = (v / max) * 100;                             // Porcentaje
    return `
      <div class="bar-row">
        <span class="bar-label">${p}</span>
        <div class="bar-track"><div class="bar-fill bar-${p}" style="width:${pct}%"></div></div>
        <span class="bar-count">${v}</span>
      </div>`;                                               // HTML por barra
  }).join('');                                               // Une todo

  const w = document.getElementById('worker-stats');         // Caja de stats del worker
  w.innerHTML = `
    <p><strong>${stats.percentClosed}%</strong> de tickets cerrados</p>
    <p class="muted">Procesado en ${stats.elapsedMs} ms (off-main-thread)</p>
    ${stats.topUrgent.length ? `
      <p style="margin-top:.5rem"><strong>Top urgentes:</strong></p>
      <ul style="padding-left:1.2rem;font-size:.85rem;color:var(--text-muted)">
        ${stats.topUrgent.map(t => `<li>${escapeHtml(t.titulo)} <em>(${t.prioridad})</em></li>`).join('')}
      </ul>` : '<p class="muted">Sin tickets urgentes pendientes</p>'}
  `;
}

// Carga la informacion geo + clima y devuelve coordenadas para reusar
export async function loadGeoInfo() {
  const box = document.getElementById('geo-info');           // Contenedor objetivo
  box.innerHTML = '<p class="muted">Solicitando ubicacion...</p>'; // Estado de carga
  try {                                                      // Try general
    const { lat, lon, accuracy } = await getCurrentPosition(); // Pide geo
    const [weather, place] = await Promise.all([             // Pide clima y reverse en paralelo
      fetchWeather(lat, lon).catch(() => null),              // Clima (tolerante a fallo)
      reverseGeocode(lat, lon).catch(() => null),            // Reverse geocode
    ]);
    const cur = weather && weather.current;                  // Atajo
    const ciudad = (place && (place.city || place.locality)) || 'Tu ubicacion'; // Ciudad
    const pais = place && place.countryName ? ' - ' + place.countryName : ''; // Pais
    box.innerHTML = `
      <p><strong>${escapeHtml(ciudad)}</strong>${escapeHtml(pais)}</p>
      <p>Lat: ${lat.toFixed(3)} - Lon: ${lon.toFixed(3)} <span class="muted">(+/- ${Math.round(accuracy)}m)</span></p>
      ${cur ? `
        <p>${escapeHtml(describeWeather(cur.weather_code))} - <strong>${cur.temperature_2m}&deg;C</strong></p>
        <p class="muted">Viento: ${cur.wind_speed_10m} km/h - Humedad: ${cur.relative_humidity_2m}%</p>
      ` : '<p class="muted">Clima no disponible en este momento</p>'}
    `;                                                       // Renderiza
    return { lat, lon };                                     // Devuelve coords para asociarlas a nuevos tickets
  } catch (err) {                                            // Captura cualquier error (permiso denegado, timeout)
    box.innerHTML = `<p class="muted">No se pudo obtener la ubicacion: ${escapeHtml(err.message || 'desconocido')}</p>`; // Mensaje
    return null;                                             // Devuelve null
  }
}
