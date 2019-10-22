package org.openbase.bco.device.handle.manager;

/*-
 * #%L
 * BCO Handle Device Manager
 * %%
 * Copyright (C) 2015 - 2019 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
