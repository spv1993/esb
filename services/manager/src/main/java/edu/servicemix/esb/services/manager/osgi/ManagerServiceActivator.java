package edu.servicemix.esb.services.manager.osgi;

import edu.servicemix.esb.services.manager.ManagerScheduleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ManagerServiceActivator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) { }

    @Override
    public void stop(BundleContext bundleContext) {
        ManagerScheduleService.interruptWorkingThreads();
    }
}
