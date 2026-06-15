// ===============================================================
// processor.worker.js - Web Worker que procesa estadisticas de
// tickets fuera del hilo principal para no bloquear la UI.
// ===============================================================

// Maneja mensajes entrantes desde el hilo principal
self.onmessage = (e) => {
  const { tickets } = e.data;                               // Extrae tickets del mensaje
  try {                                                     // Try para reportar fallos al hilo principal
    const start = performance.now();                        // Marca de tiempo inicial
    const total = tickets.length;                           // Total de tickets

    // Acumuladores para conteos por estado y prioridad
    const byEstado = { abierto: 0, en_progreso: 0, cerrado: 0 };
    const byPrioridad = { baja: 0, media: 0, alta: 0, critica: 0 };

    // Recorre una sola vez todos los tickets
    for (const t of tickets) {
      if (byEstado[t.estado] !== undefined) byEstado[t.estado]++;        // Suma estado
      if (byPrioridad[t.prioridad] !== undefined) byPrioridad[t.prioridad]++; // Suma prioridad
    }

    // Tabla de puntuacion para ordenar urgencia
    const score = { critica: 4, alta: 3, media: 2, baja: 1 };
    // Considera solo tickets no cerrados como candidatos urgentes
    const abiertos = tickets.filter(t => t.estado !== 'cerrado');
    // Ordena por prioridad descendente
    const sorted = [...abiertos].sort((a, b) => (score[b.prioridad] || 0) - (score[a.prioridad] || 0));
    // Toma los primeros 3 para mostrar
    const topUrgent = sorted.slice(0, 3).map(t => ({ id: t.id, titulo: t.titulo, prioridad: t.prioridad }));

    // Calcula porcentaje cerrado evitando division por cero
    const percentClosed = total ? Math.round((byEstado.cerrado / total) * 100) : 0;
    // Tiempo transcurrido en ms con dos decimales
    const elapsedMs = Number((performance.now() - start).toFixed(2));

    // Envia el resultado al hilo principal
    self.postMessage({
      ok: true,                                              // Bandera de exito
      total,                                                 // Total
      byEstado,                                              // Conteo por estado
      byPrioridad,                                           // Conteo por prioridad
      topUrgent,                                             // Top urgentes
      percentClosed,                                         // Porcentaje cerrado
      elapsedMs,                                             // Tiempo de procesado
      processedAt: new Date().toISOString(),                 // Timestamp
    });
  } catch (err) {                                            // Captura cualquier fallo
    self.postMessage({ ok: false, error: err.message });     // Reporta error
  }
};
