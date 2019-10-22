package org.openbase.bco.device.handle.connector;

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
