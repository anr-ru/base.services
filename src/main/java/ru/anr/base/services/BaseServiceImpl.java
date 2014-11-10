/**
 * 
 */
package ru.anr.base.services;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import ru.anr.base.BaseSpringParent;
import ru.anr.base.services.pattern.Strategy;
import ru.anr.base.services.pattern.StrategyFactory;
import ru.anr.base.services.pattern.StrategyFactoryImpl;
import ru.anr.base.services.pattern.StrategyStatistic;

/**
 * Implementation of Base Service.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public class BaseServiceImpl extends BaseSpringParent implements BaseService {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

    /**
     * Default name for production profile
     */
    public static final String PRODUCTION_PROFILE = "production";

    /**
     * Checking for 'Production' mode
     * 
     * @return true, if 'production' profile found
     */
    protected boolean isProdMode() {

        Set<String> profiles = getProfiles();
        return profiles.contains(PRODUCTION_PROFILE);
    }

    /**
     * A ref to a text resource service
     */
    @Autowired
    @Qualifier("messageSourceAccessor")
    private MessageSourceAccessor messages;

    /**
     * A template of string is returned in case when message with code not found
     */
    private static final String MSG_ERROR_DECORATION = "[xxx%sxxx]";

    /**
     * {@inheritDoc}
     */
    @Override
    public String text(String code, Object... args) {

        String txt = null;

        try {
            txt = messages.getMessage(code, args);

        } catch (NoSuchMessageException ex) {

            logger.error("Message resource error: {}", ex.getMessage());
            txt = String.format(MSG_ERROR_DECORATION, code);
        }
        return txt;
    }

    /**
     * List of additional extentions of the service
     */
    private List<Strategy> extentions = list();

    /**
     * A factory which manages by extentions execution
     */
    private StrategyFactory extentionFactory;

    /**
     * Initialization
     */
    @PostConstruct
    public void init() {

        extentionFactory = new StrategyFactoryImpl(extentions);
    }

    /**
     * A point of extention of the current service with sort of plugins.
     * Delegates an additional processing to some strategies, defined for the
     * service.
     * 
     * @param object
     *            Original object to process
     * @param params
     *            Additional parameters
     * @return Possible updated original object
     */
    protected Object processExtentions(Object object, Object... params) {

        StrategyStatistic stat = extentionFactory.process(object, params);

        if (logger.isDebugEnabled()) {
            logger.debug("List of applied strategies: {}", stat.getAppliedStrategies());
        }
        return stat.getObject();
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param extentions
     *            the extentions to set
     */
    public void setExtentions(List<Strategy> extentions) {

        this.extentions = extentions;
    }
}
