// ===============================================================
// app.js - Punto de entrada: orquesta los modulos, maneja eventos
// del DOM y mantiene el estado de la aplicacion en memoria.
// ===============================================================

// Importa modulos de persistencia
import { loadTickets, saveTickets, loadFilter, saveFilter, saveLastTab, loadLastTab, clearAll } from './modules/storage.js';
// Importa operaciones puras de tickets
import { createTicket, updateTicket, deleteTicket, filterTickets, validateTicket } from './modules/crud.js';
// Importa logica del dashboard y worker
import { initWorker, processStats, renderMetrics, loadGeoInfo } from './modules/dashboard.js';

// ---------- Estado global de la aplicacion ----------
const state = {
  tickets: [],                                              // Lista actual de tickets
  filter: { search: '', estado: '', prioridad: '' },        // Filtros activos
  editingId: null,                                          // Id del ticket en edicion (o null)
  geo: null,                                                // Coordenadas actuales
};

// Datos de ejemplo para sembrar el storage
const seedData = [
  { titulo: 'Error 500 en checkout', descripcion: 'Los usuarios no pueden completar la compra', estado: 'abierto', prioridad: 'critica' },
  { titulo: 'Logo desalineado en movil', descripcion: 'Se ve mal en pantallas pequenas', estado: 'en_progreso', prioridad: 'baja' },
  { titulo: 'Solicitud de nuevo reporte', descripcion: 'El cliente pide reporte mensual de ventas', estado: 'abierto', prioridad: 'media' },
  { titulo: 'Caida de servidor de correos', descripcion: 'No llegan notificaciones a clientes', estado: 'cerrado', prioridad: 'alta' },
];

// ---------- Utilidades de UI ----------

// Muestra una notificacion tipo toast en la esquina inferior
function toast(message, type = 'info') {
  const c = document.getElementById('toast-container');     // Contenedor
  const el = document.createElement('div');                 // Crea elemento
  el.className = `toast ${type}`;                           // Aplica estilo segun tipo
  el.textContent = message;                                 // Texto seguro (sin HTML)
  c.appendChild(el);                                        // Agrega al DOM
  setTimeout(() => el.remove(), 3000);                      // Desaparece a los 3s
}

// Escapa HTML para evitar inyeccion en el listado
function escapeHtml(s = '') {
  return String(s).replace(/[&<>"']/g, c => (               // Reemplaza caracteres peligrosos
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
  ));
}

// Formatea fecha ISO a formato local legible
function formatDate(iso) {
  try {                                                     // Try defensivo
    return new Date(iso).toLocaleString('es-ES', { dateStyle: 'medium', timeStyle: 'short' });
  } catch {                                                 // Si falla
    return iso;                                             // Devuelve la cadena original
  }
}

// Exporta el listado actual de tickets a un archivo CSV
function exportCSV(tickets) {
  const headers = ['id', 'titulo', 'descripcion', 'estado', 'prioridad', 'fechaCreacion']; // Cabeceras
  const rows = tickets.map(t =>                             // Por cada ticket
    headers.map(h => `"${String(t[h] ?? '').replace(/"/g, '""')}"`).join(',') // Escapa comillas
  );
  const csv = [headers.join(','), ...rows].join('\n');      // Une todo
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' }); // Crea blob
  const url = URL.createObjectURL(blob);                    // URL temporal
  const a = document.createElement('a');                    // Anchor temporal
  a.href = url;                                             // Apunta al blob
  a.download = `tickets_${Date.now()}.csv`;                 // Nombre de archivo
  a.click();                                                // Dispara la descarga
  URL.revokeObjectURL(url);                                 // Libera memoria
}

// Lanza una notificacion del navegador (con permiso) o un toast como fallback
async function notify(title, body) {
  if (!('Notification' in window)) {                        // No soporta Notifications
    toast(`${title}: ${body}`, 'info');                     // Fallback toast
    return;                                                 // Sale
  }
  if (Notification.permission === 'default') {              // Aun no se pidio permiso
    await Notification.requestPermission();                 // Solicita permiso
  }
  if (Notification.permission === 'granted') {              // Si concedido
    new Notification(title, { body });                      // Muestra notificacion
  } else {                                                  // Si denegado
    toast(`${title}: ${body}`, 'info');                     // Fallback toast
  }
}

// Importa tickets desde un archivo JSON local
function importJSON(file) {
  return new Promise((resolve, reject) => {                 // Promesa para asincronia
    const reader = new FileReader();                        // Lector de archivos
    reader.onload = () => {                                 // Cuando termina
      try {                                                 // Try parseo
        const data = JSON.parse(reader.result);             // Parsea
        if (!Array.isArray(data)) throw new Error('Se esperaba un arreglo'); // Valida
        resolve(data);                                      // Resuelve
      } catch (err) { reject(err); }                        // Rechaza
    };
    reader.onerror = () => reject(reader.error);            // Error al leer
    reader.readAsText(file);                                // Lee como texto
  });
}

// ---------- Renderizado ----------

// Renderiza la lista de tickets aplicando los filtros activos
function renderTickets() {
  const list = document.getElementById('tickets-list');     // Contenedor
  const filtered = filterTickets(state.tickets, state.filter); // Aplica filtros
  document.getElementById('result-count').textContent =     // Actualiza contador
    `${filtered.length} resultado${filtered.length === 1 ? '' : 's'}`;

  if (!filtered.length) {                                   // Si no hay resultados
    list.innerHTML = `<div class="empty">No hay tickets que coincidan. Crea uno nuevo o ajusta los filtros.</div>`;
    return;                                                 // Sale
  }

  // Genera el HTML para cada ticket
  list.innerHTML = filtered.map(t => `
    <article class="ticket-card prioridad-${t.prioridad}" data-id="${t.id}">
      <div class="ticket-head">
        <div class="ticket-title">${escapeHtml(t.titulo)}</div>
        <div class="ticket-actions">
          <button class="btn btn-edit" data-action="edit" data-id="${t.id}" type="button">Editar</button>
          <button class="btn btn-danger" data-action="delete" data-id="${t.id}" type="button">Eliminar</button>
        </div>
      </div>
      <div class="ticket-desc">${escapeHtml(t.descripcion)}</div>
      <div class="ticket-meta">
        <span class="chip estado-${t.estado}">${t.estado.replace('_', ' ')}</span>
        <span class="chip">Prioridad: ${t.prioridad}</span>
        <span class="chip">${formatDate(t.fechaCreacion)}</span>
        ${t.coordenadas ? `<span class="chip">${t.coordenadas.lat.toFixed(2)}, ${t.coordenadas.lon.toFixed(2)}</span>` : ''}
      </div>
    </article>
  `).join('');                                              // Une el HTML
}

// Refresca todo lo que depende del estado
function refresh() {
  processStats(state.tickets);                              // Actualiza dashboard via worker
  renderTickets();                                          // Vuelve a pintar listado
}

// ---------- Manejadores de eventos ----------

// Cambia la pestana activa (vista) y la persiste en cookie
function switchTab(name) {
  document.querySelectorAll('.tab').forEach(t =>            // Recorre tabs
    t.classList.toggle('active', t.dataset.tab === name)    // Marca activa
  );
  document.querySelectorAll('.view').forEach(v =>           // Recorre vistas
    v.classList.toggle('active', v.id === `view-${name}`)   // Muestra correspondiente
  );
  saveLastTab(name);                                        // Guarda en cookie
}

// Aplica los valores del filtro al DOM
function applyFilterToInputs() {
  document.getElementById('search').value = state.filter.search || '';        // Busqueda
  document.getElementById('filter-estado').value = state.filter.estado || ''; // Estado
  document.getElementById('filter-prioridad').value = state.filter.prioridad || ''; // Prioridad
}

// Maneja el envio del formulario (crear o editar)
function onSubmit(e) {
  e.preventDefault();                                       // Evita reload del form
  try {                                                     // Try general
    const payload = {                                       // Datos del formulario
      titulo: document.getElementById('titulo').value,       // Titulo
      descripcion: document.getElementById('descripcion').value, // Descripcion
      estado: document.getElementById('estado').value,       // Estado
      prioridad: document.getElementById('prioridad').value, // Prioridad
    };
    const { valid, errors } = validateTicket(payload);      // Valida
    document.querySelectorAll('.error').forEach(el => el.textContent = ''); // Limpia errores
    if (!valid) {                                           // Si invalido
      for (const k in errors) {                             // Pinta errores por campo
        const el = document.querySelector(`[data-error="${k}"]`); // Elemento destino
        if (el) el.textContent = errors[k];                 // Texto de error
      }
      toast('Revisa los campos del formulario', 'error');   // Aviso al usuario
      return;                                               // Sale
    }
    if (state.editingId) {                                  // Modo edicion
      state.tickets = updateTicket(state.tickets, state.editingId, payload); // Actualiza
      toast('Ticket actualizado', 'success');               // Aviso
    } else {                                                // Modo creacion
      state.tickets = createTicket(state.tickets, { ...payload, coordenadas: state.geo }); // Crea
      toast('Ticket creado', 'success');                    // Aviso
      if (payload.prioridad === 'critica') {                // Si es critico
        notify('Ticket Critico creado', payload.titulo);    // Notifica
      }
    }
    saveTickets(state.tickets);                             // Persiste
    resetForm();                                            // Limpia form
    refresh();                                              // Recalcula UI
    switchTab('tickets');                                   // Cambia a listado
  } catch (err) {                                           // Captura inesperados
    console.error(err);                                     // Log
    toast('Error inesperado: ' + err.message, 'error');     // Aviso usuario
  }
}

// Carga datos del ticket al formulario para editarlo
function beginEdit(id) {
  const t = state.tickets.find(x => x.id === id);           // Busca
  if (!t) return;                                           // Defensa
  state.editingId = id;                                     // Marca edicion
  document.getElementById('form-title').textContent = 'Editar Ticket'; // Titulo
  document.getElementById('ticket-id').value = id;          // Id oculto
  document.getElementById('titulo').value = t.titulo;       // Titulo
  document.getElementById('descripcion').value = t.descripcion; // Descripcion
  document.getElementById('estado').value = t.estado;       // Estado
  document.getElementById('prioridad').value = t.prioridad; // Prioridad
  switchTab('nuevo');                                       // Cambia a form
}

// Elimina un ticket previa confirmacion
function onDelete(id) {
  if (!confirm('Eliminar este ticket? Esta accion no se puede deshacer.')) return; // Confirma
  try {                                                     // Try defensivo
    state.tickets = deleteTicket(state.tickets, id);        // Elimina
    saveTickets(state.tickets);                             // Persiste
    refresh();                                              // Refresca UI
    toast('Ticket eliminado', 'success');                   // Aviso
  } catch (err) {                                           // Captura
    toast('Error al eliminar: ' + err.message, 'error');    // Aviso
  }
}

// Resetea el formulario al estado de creacion
function resetForm() {
  state.editingId = null;                                   // Sale de edicion
  document.getElementById('form-title').textContent = 'Crear Ticket'; // Titulo por defecto
  document.getElementById('ticket-form').reset();           // Reset nativo
  document.querySelectorAll('.error').forEach(el => el.textContent = ''); // Limpia errores
  document.getElementById('prioridad').value = 'media';     // Prioridad por defecto
}

// Conecta todos los listeners del DOM
function bindEvents() {
  // Navegacion por tabs (delegacion)
  document.getElementById('tabs').addEventListener('click', (e) => {
    const btn = e.target.closest('.tab');                   // Encuentra el tab clickeado
    if (!btn) return;                                       // Sale si no aplica
    switchTab(btn.dataset.tab);                             // Cambia vista
  });

  // Submit y cancelar del formulario
  document.getElementById('ticket-form').addEventListener('submit', onSubmit); // Guardar
  document.getElementById('btn-cancel').addEventListener('click', () => {      // Cancelar
    resetForm();                                            // Limpia
    switchTab('tickets');                                   // Vuelve al listado
  });

  // Filtros del listado (input event para reactividad inmediata)
  ['search', 'filter-estado', 'filter-prioridad'].forEach(id => {
    document.getElementById(id).addEventListener('input', () => {
      state.filter = {                                      // Reconstruye filtro
        search: document.getElementById('search').value,
        estado: document.getElementById('filter-estado').value,
        prioridad: document.getElementById('filter-prioridad').value,
      };
      saveFilter(state.filter);                             // Persiste
      renderTickets();                                      // Refresca lista
    });
  });

  // Delegacion de eventos sobre la lista (editar/eliminar)
  document.getElementById('tickets-list').addEventListener('click', (e) => {
    const btn = e.target.closest('button[data-action]');    // Boton con accion
    if (!btn) return;                                       // Sale
    const id = btn.dataset.id;                              // Id del ticket
    if (btn.dataset.action === 'edit') beginEdit(id);       // Editar
    if (btn.dataset.action === 'delete') onDelete(id);      // Eliminar
  });

  // Boton: exportar CSV
  document.getElementById('btn-export').addEventListener('click', () => {
    if (!state.tickets.length) return toast('No hay tickets para exportar', 'warn'); // Defensa
    exportCSV(state.tickets);                               // Exporta
    toast('CSV exportado', 'success');                      // Aviso
  });

  // Boton: importar JSON (abre dialogo de archivo)
  document.getElementById('btn-import').addEventListener('click', () => {
    document.getElementById('file-import').click();         // Dispara input file
  });

  // Cuando el usuario selecciona un archivo JSON
  document.getElementById('file-import').addEventListener('change', async (e) => {
    const file = e.target.files[0];                         // Primer archivo
    if (!file) return;                                      // Sale si no hay
    try {                                                   // Try parseo
      const imported = await importJSON(file);              // Lee y parsea
      const valid = imported.filter(x => x && x.titulo && x.descripcion); // Filtra validos
      valid.forEach(payload => {                            // Por cada uno
        state.tickets = createTicket(state.tickets, payload); // Crea
      });
      saveTickets(state.tickets);                           // Persiste
      refresh();                                            // Refresca
      toast(`Importados ${valid.length} tickets`, 'success'); // Aviso
    } catch (err) {                                         // Captura
      toast('JSON invalido: ' + err.message, 'error');      // Aviso
    } finally {                                             // Siempre
      e.target.value = '';                                  // Resetea input para reusar
    }
  });

  // Boton: notificacion de prueba
  document.getElementById('btn-notify').addEventListener('click', () => {
    const abiertos = state.tickets.filter(t => t.estado !== 'cerrado').length; // Conteo
    notify('SoporteHub', `Tienes ${abiertos} tickets sin cerrar`); // Notifica
  });

  // Boton: cargar datos demo
  document.getElementById('btn-seed').addEventListener('click', () => {
    if (!confirm('Cargar datos de prueba? Se sumaran a los actuales.')) return; // Confirma
    seedData.forEach(s => { state.tickets = createTicket(state.tickets, s); }); // Crea cada demo
    saveTickets(state.tickets);                             // Persiste
    refresh();                                              // Refresca
    toast('Datos de prueba cargados', 'success');           // Aviso
  });

  // Boton: borrar todo (storage + estado)
  document.getElementById('btn-clear').addEventListener('click', () => {
    if (!confirm('Borrar TODOS los tickets y filtros guardados?')) return; // Confirma
    clearAll();                                             // Limpia storage
    state.tickets = [];                                     // Reset estado
    state.filter = { search: '', estado: '', prioridad: '' }; // Reset filtros
    applyFilterToInputs();                                  // Aplica al DOM
    refresh();                                              // Refresca
    toast('Todos los datos fueron eliminados', 'warn');     // Aviso
  });

  // Boton: actualizar ubicacion
  document.getElementById('btn-geo').addEventListener('click', async () => {
    state.geo = await loadGeoInfo();                        // Recarga geo
    if (state.geo) toast('Ubicacion actualizada', 'success'); // Aviso si exito
  });

  // Captura global de errores no manejados
  window.addEventListener('error', (e) => {                 // Errores sincronos
    console.error('[global error]', e.error || e.message);  // Log
  });
  window.addEventListener('unhandledrejection', (e) => {    // Promesas rechazadas
    console.error('[unhandled promise]', e.reason);         // Log
  });
}

// Inicializa la aplicacion al cargar el DOM
function init() {
  state.tickets = loadTickets();                            // Carga tickets desde LocalStorage
  state.filter = loadFilter();                              // Carga filtros desde SessionStorage
  initWorker((stats) => renderMetrics(stats));              // Inicializa Web Worker
  bindEvents();                                             // Conecta listeners
  applyFilterToInputs();                                    // Aplica filtros al DOM
  switchTab(loadLastTab());                                 // Restaura ultima pestana via cookie
  refresh();                                                // Pinta UI inicial
  loadGeoInfo().then(geo => { state.geo = geo; });          // Pide geo en background
}

// Espera a que el DOM este listo para arrancar
document.addEventListener('DOMContentLoaded', init);
