package org.openbase.bco.device.handle.manager;

import org.openbase.bco.dal.control.layer.unit.device.DeviceManagerImpl;
import org.openbase.bco.device.handle.connector.HandleConnectorFactory;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.iface.Launchable;
import org.openbase.jul.iface.VoidInitializable;
import org.openbase.type.domotic.unit.UnitConfigType;
import org.openbase.type.domotic.unit.device.DeviceClassType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleDeviceManager implements Launchable<Void>, VoidInitializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleDeviceManager.class);

    private final DeviceManagerImpl deviceManager;

    /**
     * Synchronization observer that triggers resynchronization of all units if their configuration changes.
     */
    public HandleDeviceManager() throws InterruptedException, InstantiationException {
        this.deviceManager = new DeviceManagerImpl(HandleConnectorFactory.getInstance() , HandleConnectorFactory.getInstance(), false) {

            @Override
            public boolean isSupported(UnitConfigType.UnitConfig config) {
                DeviceClassType.DeviceClass deviceClass;
                try {
                    try {
                        deviceClass = Registries.getClassRegistry(true).getDeviceClassById(config.getDeviceConfig().getDeviceClassId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                } catch (CouldNotPerformException e) {
                    return false;
                }
                if (!deviceClass.getBindingConfig().getBindingId().equalsIgnoreCase("handles")) {
                    return false;
                }

                return super.isSupported(config);
            }
        };
    }

    @Override
    public void init() throws InterruptedException, InitializationException {
        deviceManager.init();
    }

    @Override
    public void activate() throws CouldNotPerformException, InterruptedException {
        deviceManager.activate();
    }

    @Override
    public void deactivate() throws CouldNotPerformException, InterruptedException {
        deviceManager.deactivate();
    }

    @Override
    public boolean isActive() {
        return deviceManager.isActive();
    }
}
