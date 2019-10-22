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

import org.openbase.bco.dal.control.layer.unit.ColorableLightController;
import org.openbase.bco.dal.lib.layer.service.ServiceProvider;
import org.openbase.bco.dal.lib.layer.service.operation.BrightnessStateOperationService;
import org.openbase.bco.dal.lib.layer.service.operation.ColorStateOperationService;
import org.openbase.bco.dal.lib.layer.service.operation.PowerStateOperationService;
import org.openbase.jul.communication.controller.RPCHelper;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.BrightnessStateType.BrightnessState;
import org.openbase.type.domotic.state.ColorStateType.ColorState;
import org.openbase.type.domotic.state.PowerStateType.PowerState;
import org.openbase.type.domotic.unit.dal.ColorableLightDataType.ColorableLightData;

import java.util.concurrent.Future;

public class ColorableLightHandleConnector extends AbstractHandleConnector<ColorableLightData, ColorableLightData.Builder> implements ColorStateOperationService, BrightnessStateOperationService, PowerStateOperationService {

    public ColorableLightHandleConnector(final ColorableLightController unit) throws InstantiationException {
        super(unit);
    }

    @Override
    public ServiceProvider getServiceProvider() {
        return unit;
    }

    @Override
    public BrightnessState getBrightnessState() throws NotAvailableException {
        return unit.getData().getBrightnessState();
    }

    @Override
    public Future<ActionDescription> setBrightnessState(BrightnessState state) {
        return RPCHelper.callRemoteServerMethod(state, remoteServer, ActionDescription.class);
    }

    @Override
    public ColorState getColorState() throws NotAvailableException {
        return unit.getData().getColorState();
    }

    @Override
    public Future<ActionDescription> setColorState(ColorState state) {
        return RPCHelper.callRemoteServerMethod(state, remoteServer, ActionDescription.class);
    }

    @Override
    public PowerState getPowerState() throws NotAvailableException {
        return unit.getData().getPowerState();
    }

    @Override
    public Future<ActionDescription> setPowerState(PowerState state) {
        return RPCHelper.callRemoteServerMethod(state, remoteServer, ActionDescription.class);
    }

    @Override
    protected ServiceType getServiceType() {
        return ServiceType.COLOR_STATE_SERVICE;
    }
}
