package org.openbase.bco.device.handle.connector;

/*
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

import com.google.protobuf.Message;
import org.openbase.bco.dal.control.layer.unit.ColorableLightController;
import org.openbase.bco.dal.control.layer.unit.ReedContactController;
import org.openbase.bco.dal.lib.layer.service.*;
import org.openbase.bco.dal.lib.layer.service.operation.*;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.bco.dal.lib.layer.unit.UnitController;
import org.openbase.jul.exception.*;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.type.processing.TimestampProcessor;
import org.openbase.jul.iface.Activatable;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.schedule.FutureProcessor;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.BrightnessStateType.BrightnessState;
import org.openbase.type.domotic.state.ColorStateType.ColorState;
import org.openbase.type.domotic.state.PowerStateType.PowerState;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class HandleConnectorFactory implements OperationServiceFactory, UnitDataSourceFactory {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HandleConnectorFactory.class);

    public static final String HANDLE_SCOPE = "/handle";

    private static HandleConnectorFactory instance = new HandleConnectorFactory();

    public static HandleConnectorFactory getInstance() {
        if (instance == null) {
            instance = new HandleConnectorFactory();
        }
        return instance; }

    private HashMap<String, ColorableLightHandleConnector> colorableLightHandleConnectorMap;
    private HashMap<String, ReedContactHandleConnector> reedContactHandleConnectorMap;

    private HandleConnectorFactory() {
        this.colorableLightHandleConnectorMap = new HashMap<>();
        this.reedContactHandleConnectorMap = new HashMap<>();
    }

    @Override
    public <UNIT extends UnitController<?,?>> OperationService newInstance(final ServiceType operationServiceType, final UNIT unit) throws InstantiationException {
        try {
            switch (unit.getUnitType()) {
                case COLORABLE_LIGHT:
                    if (!colorableLightHandleConnectorMap.containsKey(unit.getId())) {
                        colorableLightHandleConnectorMap.put(unit.getId(), new ColorableLightHandleConnector((ColorableLightController) unit));
                    }
                    return colorableLightHandleConnectorMap.get(unit.getId());
                default:
                    throw new NotSupportedException(unit.getUnitType(), this);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(OperationService.class, operationServiceType.name(), ex);
        }
    }

    @Override
    public <UNIT extends Unit<?>> Activatable newInstance(UNIT unit) throws InstantiationException {
        try {

            switch (unit.getUnitType()) {
                case COLORABLE_LIGHT:
                    if (!colorableLightHandleConnectorMap.containsKey(unit.getId())) {
                        colorableLightHandleConnectorMap.put(unit.getId(), new ColorableLightHandleConnector((ColorableLightController) unit));
                    }
                    return colorableLightHandleConnectorMap.get(unit.getId());
                case REED_CONTACT:
                    if (!reedContactHandleConnectorMap.containsKey(unit.getId())) {
                        reedContactHandleConnectorMap.put(unit.getId(), new ReedContactHandleConnector((ReedContactController) unit));
                    }
                    return reedContactHandleConnectorMap.get(unit.getId());
                default:
                    throw new NotSupportedException(unit.getUnitType(), this);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(OperationService.class, "UnitDataSource", ex);
        }
    }
}
