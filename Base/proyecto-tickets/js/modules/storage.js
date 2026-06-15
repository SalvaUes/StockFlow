// ===============================================================
// storage.js - Capa de persistencia
// LocalStorage para tickets, SessionStorage para filtros y Cookies
// para preferencias ligeras (ultima pestana visitada).
// ===============================================================

// Clave usada en LocalStorage para guardar el arreglo de tickets
const KEY_TICKETS = 'soportehub_tickets_v1';
// Clave usada en SessionStorage para guardar el ultimo filtro aplicado
const KEY_FILTER = 'soportehub_last_filter';
// Nombre de la cookie para recordar la ultima pestana abierta
const COOKIE_TAB = 'soportehub_last_tab';

// Carga el listado de tickets desde LocalStorage
export function loadTickets() {
  try {                                                  // Bloque protegido por errores de parseo
    const raw = localStorage.getItem(KEY_TICKETS);       // Lee el valor crudo
    if (!raw) return [];                                 // Si no existe, devuelve arreglo vacio
    const data = JSON.parse(raw);                        // Convierte a objeto JS
    return Array.isArray(data) ? data : [];              // Garantiza que sea arreglo
  } catch (err) {                                        // Captura errores de parseo
    console.error('[storage] Error leyendo tickets:', err); // Log de diagnostico
    return [];                                           // Fallback seguro
  }
}

// Guarda el listado completo de tickets (sobrescribe la clave)
export function saveTickets(tickets) {
  try {                                                  // Protege contra cuotas excedidas
    localStorage.setItem(KEY_TICKETS, JSON.stringify(tickets)); // Serializa y persiste
  } catch (err) {                                        // Captura errores
    console.error('[storage] Error guardando tickets:', err); // Log
  }
}

// Persiste el ultimo filtro aplicado en SessionStorage
export function saveFilter(filter) {
  try {                                                  // Try/catch defensivo
    sessionStorage.setItem(KEY_FILTER, JSON.stringify(filter)); // Guarda objeto serializado
  } catch (err) {                                        // Captura errores
    console.error('[storage] Error guardando filtro:', err); // Log
  }
}

// Carga el ultimo filtro desde SessionStorage
export function loadFilter() {
  try {                                                  // Protegido
    const raw = sessionStorage.getItem(KEY_FILTER);      // Lee la clave
    return raw ? JSON.parse(raw) : { search: '', estado: '', prioridad: '' }; // Default seguro
  } catch {                                              // Si falla el parseo
    return { search: '', estado: '', prioridad: '' };    // Devuelve defaults
  }
}

// Establece una cookie simple (solo navegador, sin httpOnly)
export function setCookie(name, value, days = 30) {
  try {                                                  // Try defensivo
    const d = new Date();                                // Fecha actual
    d.setTime(d.getTime() + days * 864e5);               // Suma N dias en milisegundos
    const expires = 'expires=' + d.toUTCString();        // Fecha de expiracion
    document.cookie = `${name}=${encodeURIComponent(value)};${expires};path=/;SameSite=Lax`; // Cookie
  } catch (err) {                                        // Captura
    console.error('[storage] Error escribiendo cookie:', err); // Log
  }
}

// Lee una cookie por nombre, devuelve cadena vacia si no existe
export function getCookie(name) {
  try {                                                  // Try defensivo
    const target = name + '=';                           // Prefijo a buscar
    const parts = document.cookie.split(';');            // Separa por ;
    for (let c of parts) {                               // Recorre cada cookie
      c = c.trim();                                      // Quita espacios
      if (c.indexOf(target) === 0) {                     // Si coincide el nombre
        return decodeURIComponent(c.substring(target.length)); // Devuelve el valor
      }
    }
    return '';                                           // No encontrada
  } catch {                                              // Si falla
    return '';                                           // Devuelve vacio
  }
}

// Atajos especificos para la pestana activa
export const saveLastTab = (tab) => setCookie(COOKIE_TAB, tab); // Guarda pestana
export const loadLastTab = () => getCookie(COOKIE_TAB) || 'dashboard'; // Lee pestana

// Limpia todos los datos del proyecto (usado por boton Borrar todo)
export function clearAll() {
  try {                                                  // Try defensivo
    localStorage.removeItem(KEY_TICKETS);                // Borra tickets
    sessionStorage.removeItem(KEY_FILTER);               // Borra filtro
    document.cookie = `${COOKIE_TAB}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`; // Caduca cookie
  } catch (err) {                                        // Captura
    console.error('[storage] Error en clearAll:', err);  // Log
  }
}
