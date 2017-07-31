package org.atlasapi.reporting.telescope;

import com.metabroadcast.columbus.telescope.api.Environment;
import com.metabroadcast.columbus.telescope.api.Process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates proxies to telescopeClients that can be used for reporting to telescope.
 * <p>
 * If you need to amend this class to accommodate more Processes (i.e. add more reporters),
 * extend {@link atlasFeedsReporters} enum accordingly.
 */
public class TelescopeFactory {

    public static final String TELESCOPE_HOST = "columbus-telescope.stage.svc.cluster.local";//Configurer.get("telescope.host").get();
    public static final String ENVIRONMENT ="STAGE";//Configurer.get("telescope.environment").get();
    private static final Logger log = LoggerFactory.getLogger(TelescopeFactory.class);

    /**
     * This factory will always give you a telescope (never null). If there are initialization
     * errors the telescope you will get might be unable to report, and fail graciously.
     */
    public static TelescopeProxy make(TelescopeReporter reporterName) {
        Process process = getProcess(reporterName);
        TelescopeProxy telescopeProxy = new TelescopeProxy(process);

        return telescopeProxy;
    }

    //create and return a telescope.api.Process.
    protected static Process getProcess(TelescopeReporter name) {
        Environment environment;
        try {
            environment = Environment.valueOf(ENVIRONMENT);
        } catch (IllegalArgumentException e) {
            //add stage as the default environment, which is better than crashing
            environment = Environment.STAGE;
            log.error(
                    "Could not find a telescope environment with the given name, name={}. Falling back to STAGE.",
                    ENVIRONMENT,
                    e
            );
        }

        return Process.create(name.getReporterKey(), name.getReporterName(), environment);
    }

}