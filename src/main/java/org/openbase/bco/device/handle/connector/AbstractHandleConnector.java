package org.openbase.bco.device.handle.connector;

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

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.bco.dal.lib.layer.unit.UnitController;
import org.openbase.jul.communication.controller.AbstractControllerServer;
import org.openbase.jul.communication.controller.RPCHelper;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.rsb.com.RSBFactoryImpl;
import org.openbase.jul.extension.rsb.com.RSBSharedConnectionConfig;
import org.openbase.jul.extension.rsb.iface.RSBListener;
import org.openbase.jul.extension.rsb.iface.RSBRemoteServer;
import org.openbase.jul.extension.type.processing.TimestampProcessor;
import org.openbase.jul.iface.Activatable;
import org.openbase.jul.schedule.SyncObject;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandleConnector<D extends AbstractMessage, DB extends D.Builder<DB>> implements Activatable {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final SyncObject dataUpdateMonitor = new SyncObject("DataUpdateMonitor");

    protected final Unit<D> unit;
    protected final String hardwareId;
    protected final RSBRemoteServer remoteServer;
    protected final RSBListener listener;

    private transient boolean active;
    private long newestEventTime = 0;
    private long newestEventTimeNano = 0;

    public AbstractHandleConnector(final UnitController<D, ?> unit) throws InstantiationException {
        try {
            this.unit = unit;
            this.hardwareId = unit.getHostUnitConfig().getDeviceConfig().getSerialNumber();
            final String unitTypeString = unit.getUnitType().name().toLowerCase().replaceAll("_", "");
            this.remoteServer = RSBFactoryImpl.getInstance().createSynchronizedRemoteServer(
                    HandleConnectorFactory.HANDLE_SCOPE +
                            "/" + unitTypeString +
                            "/" + hardwareId + AbstractControllerServer.SCOPE_ELEMENT_SUFFIX_CONTROL, RSBSharedConnectionConfig.getParticipantConfig());
            this.listener = RSBFactoryImpl.getInstance().createSynchronizedListener(
                    HandleConnectorFactory.HANDLE_SCOPE +
                            "/" + unitTypeString +
                            "/" + hardwareId + AbstractControllerServer.SCOPE_ELEMENT_SUFFIX_STATUS, RSBSharedConnectionConfig.getParticipantConfig());

            try {
                this.listener.addHandler(event -> {
                    try {
                        synchronized (dataUpdateMonitor) {
                            if (event == null) {
                                throw new NotAvailableException("event");
                            }

                            D dataUpdate = (D) event.getData();

                            if (dataUpdate == null) {
                                // received null data from controller which indicates a shutdown
                                return;
                            } else {

                                // skip events which were send later than the last received update
                                long userTime = RPCHelper.USER_TIME_VALUE_INVALID;
                                if (event.getMetaData().hasUserTime(RPCHelper.USER_TIME_KEY)) {
                                    userTime = event.getMetaData().getUserTime(RPCHelper.USER_TIME_KEY);
                                } else {
                                    LOGGER.debug("Data message does not contain user time key on scope " + event.getScope());
                                }

                                // filter outdated events
                                if (event.getMetaData().getCreateTime() < newestEventTime || (event.getMetaData().getCreateTime() == newestEventTime && userTime < newestEventTimeNano)) {
                                    LOGGER.info("Skip event on scope[" + event.getScope() + "] because event seems to be outdated! Received event time < latest event time [" + event.getMetaData().getCreateTime() + "<= " + newestEventTime + "][" + event.getMetaData().getUserTime(RPCHelper.USER_TIME_KEY) + " < " + newestEventTimeNano + "]");
                                    return;
                                }

                                if (userTime != RPCHelper.USER_TIME_VALUE_INVALID) {
                                    newestEventTimeNano = userTime;
                                }
                                newestEventTime = event.getMetaData().getCreateTime();


                                final Message serviceState = getServiceState(dataUpdate);

                                TimestampProcessor.updateTimestampWithCurrentTime(serviceState, LOGGER);
                                unit.applyDataUpdate(serviceState, getServiceType());
                            }
                        }
                    } catch (CouldNotPerformException | ClassCastException ex) {
                        ExceptionPrinter.printHistory("Could not process incoming data!", ex, LOGGER);
                    }
                }, true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CouldNotPerformException("Interrupted during startup!", e);
            }

        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    protected abstract ServiceType getServiceType();

    private Message getServiceState(D data) throws NotAvailableException {
        try {
            return Services.invokeProviderServiceMethod(getServiceType(), data);
        }catch (CouldNotPerformException ex) {
            throw new NotAvailableException("ServiceState", ex);
        }
    }

    @Override
    public void activate() throws CouldNotPerformException, InterruptedException {
        active = true;
        remoteServer.activate();
        listener.activate();
    }

    @Override
    public void deactivate() throws CouldNotPerformException, InterruptedException {
        active = false;
        remoteServer.deactivate();
        listener.deactivate();
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
