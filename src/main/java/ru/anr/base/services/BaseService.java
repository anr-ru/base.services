/**
 * 
 */
package ru.anr.base.services;


/**
 * A 'Service' interface
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public interface BaseService {

    /**
     * Retrieves a text message from resource with provided code. If no message
     * found with specified code, it returns a spring in format [xxxCODExxx]. If
     * exists {@link org.springframework.context.i18n.LocaleContextHolder} the
     * current locale is used, otherwise default jvm settings.
     * 
     * @param code
     *            A Message code
     * @param args
     *            Message arguments
     * @return A text with parameters replaced with the arguments
     */
    String text(String code, Object... args);
}
