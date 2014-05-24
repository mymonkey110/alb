package net.alb.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import net.alb.ALBException.ALBException;
import net.alb.util.CsvTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mymon_000 on 13-12-28.
 */
public class Loader {
    private Configuration configuration = null;
    private static Logger logger = LoggerFactory.getLogger(Loader.class);

    public Loader() {
        logger.info("Loader is initializing...");
        try {
            configuration = Configuration.getConfiguration();
            configuration.init();
        } catch (ALBException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }

    public Loader(String configPath) {
        try {
            configuration = Configuration.getConfiguration(configPath);
            configuration.init();
        } catch (ALBException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Function:Initializing component and tools in the application.
     */
    public void init() {
        try {
            logger.info("Initializing components and tools...");
            //Initialize logback,load the designed configuration
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();
            configurator.doConfigure(configuration.getLogConfigPath());
            //Initialize CsvTool
            CsvTool.setCsvTool(configuration.getCsvPath(), configuration.isCsvAppend());
            logger.info("Initializing components and tools completed.");
            logger.info("Loader initialize completed.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            logger.error(e.getMessage());
            System.exit(-1);
        }
    }
}
