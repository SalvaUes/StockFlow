// ===============================================================
// crud.js - Operaciones puras de CRUD y validaciones
// No toca el DOM ni el storage; recibe estado y devuelve estado.
// ===============================================================

// Crea un nuevo ticket y lo coloca al inicio del arreglo
export function createTicket(tickets, payload) {
  if (!Array.isArray(tickets)) throw new TypeError('tickets debe ser un arreglo'); // Defensa
  const ticket = {                                        // Construye el objeto ticket
    id: crypto.randomUUID(),                              // Identificador unico
    titulo: String(payload.titulo || '').trim(),          // Titulo limpio
    descripcion: String(payload.descripcion || '').trim(),// Descripcion limpia
    estado: payload.estado || 'abierto',                  // Estado por defecto abierto
    prioridad: payload.prioridad || 'media',              // Prioridad por defecto media
    fechaCreacion: new Date().toISOString(),              // Marca temporal ISO
    fechaActualizacion: new Date().toISOString(),         // Marca de actualizacion inicial
    coordenadas: payload.coordenadas || null,             // Coordenadas opcionales
  };
  return [ticket, ...tickets];                            // Devuelve nuevo arreglo inmutable
}

// Actualiza un ticket existente segun su id
export function updateTicket(tickets, id, changes) {
  if (!id) throw new Error('Id requerido para actualizar'); // Validacion previa
  return tickets.map(t => {                                 // Recorre cada ticket
    if (t.id !== id) return t;                              // Si no coincide, lo deja igual
    return {                                                // Construye copia mezclada
      ...t,                                                 // Datos previos
      ...changes,                                           // Cambios entrantes
      titulo: changes.titulo != null ? String(changes.titulo).trim() : t.titulo, // Limpia titulo
      descripcion: changes.descripcion != null ? String(changes.descripcion).trim() : t.descripcion, // Limpia desc
      fechaActualizacion: new Date().toISOString(),         // Actualiza marca
    };
  });
}

// Elimina un ticket por id
export function deleteTicket(tickets, id) {
  if (!id) throw new Error('Id requerido para eliminar');   // Validacion previa
  return tickets.filter(t => t.id !== id);                  // Devuelve nuevo arreglo sin el id
}

// Filtra tickets segun busqueda, estado y prioridad
export function filterTickets(tickets, { search = '', estado = '', prioridad = '' } = {}) {
  const q = String(search).trim().toLowerCase();            // Termino de busqueda normalizado
  return tickets.filter(t => {                              // Filtra el arreglo
    if (estado && t.estado !== estado) return false;        // Descarta si estado no coincide
    if (prioridad && t.prioridad !== prioridad) return false; // Descarta si prioridad no coincide
    if (q) {                                                // Si hay busqueda
      const hay = `${t.titulo} ${t.descripcion}`.toLowerCase(); // Texto a comparar
      if (!hay.includes(q)) return false;                   // Descarta si no incluye termino
    }
    return true;                                            // Pasa el filtro
  });
}

// Valida los campos obligatorios de un ticket
export function validateTicket({ titulo, descripcion, estado, prioridad }) {
  const errors = {};                                        // Acumulador de errores
  const t = String(titulo || '').trim();                    // Titulo limpio
  const d = String(descripcion || '').trim();               // Descripcion limpia
  if (t.length < 3) errors.titulo = 'El titulo debe tener al menos 3 caracteres'; // Min titulo
  if (t.length > 80) errors.titulo = 'Maximo 80 caracteres'; // Max titulo
  if (d.length < 5) errors.descripcion = 'La descripcion debe tener al menos 5 caracteres'; // Min desc
  if (d.length > 500) errors.descripcion = 'Maximo 500 caracteres'; // Max desc
  const estadosValidos = ['abierto', 'en_progreso', 'cerrado']; // Lista permitida
  if (estado && !estadosValidos.includes(estado)) errors.estado = 'Estado invalido'; // Valida estado
  const prioridadesValidas = ['baja', 'media', 'alta', 'critica']; // Lista permitida
  if (prioridad && !prioridadesValidas.includes(prioridad)) errors.prioridad = 'Prioridad invalida'; // Valida
  return { valid: Object.keys(errors).length === 0, errors }; // Resultado
}
