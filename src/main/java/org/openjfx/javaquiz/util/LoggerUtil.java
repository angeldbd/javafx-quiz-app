package org.openjfx.javaquiz.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utilidad para configurar loggers de manera consistente en toda la aplicación.
 * 
 * Proporciona un método factory para obtener loggers preconfigurados con:
 * - ConsoleHandler para salida a consola
 * - SimpleFormatter para formato legible
 * - Nivel INFO por defecto (configurable)
 * - Sin handlers duplicados
 * 
 * Ejemplo de uso:
 * <pre>
 * public class MiClase {
 *     private static final Logger LOGGER = LoggerUtil.getLogger(MiClase.class);
 *     
 *     public void miMetodo() {
 *         LOGGER.info("Información importante");
 *         LOGGER.warning("Advertencia");
 *         LOGGER.severe("Error crítico");
 *     }
 * }
 * </pre>
 * 
 * Niveles de logging disponibles:
 * - SEVERE: Errores críticos
 * - WARNING: Advertencias
 * - INFO: Información general (default)
 * - FINE: Debug detallado
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 * @see java.util.logging.Logger
 */
public class LoggerUtil {
    
    /**
     * Obtiene un logger configurado para una clase específica.
     * 
     * El logger se configura con:
     * - ConsoleHandler con nivel ALL
     * - SimpleFormatter para formato legible
     * - Nivel INFO por defecto (cambiar a FINE para debug)
     * - No usa handlers del padre (evita duplicados)
     * 
     * Si el logger ya tiene handlers, no los duplica (idempotente).
     * 
     * @param clazz Clase para la cual crear el logger
     * @return Logger configurado y listo para usar
     * 
     * @example
     * private static final Logger LOGGER = LoggerUtil.getLogger(QuizService.class);
     * LOGGER.info("Quiz iniciado");
     */
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        
        // Solo configurar si no tiene handlers (evitar duplicados)
        if (logger.getHandlers().length == 0) {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
            
            logger.addHandler(handler);
            logger.setLevel(Level.INFO); // Cambiar a FINE para más detalle
            logger.setUseParentHandlers(false);
        }
        
        return logger;
    }
}