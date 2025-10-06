package org.openjfx.javaquiz.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utilidad para configurar loggers de manera consistente
 */
public class LoggerUtil {
        
         /**
     * Obtiene un logger configurado para una clase
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
