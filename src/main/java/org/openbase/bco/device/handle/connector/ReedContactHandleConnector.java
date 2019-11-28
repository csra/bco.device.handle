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

import org.openbase.bco.dal.control.layer.unit.ReedContactController;
import org.openbase.bco.dal.lib.layer.service.ServiceProvider;
import org.openbase.bco.dal.lib.layer.service.provider.ContactStateProviderService;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.ContactStateType.ContactState;
import org.openbase.type.domotic.unit.dal.ReedContactDataType.ReedContactData;

public class ReedContactHandleConnector extends AbstractHandleConnector<ReedContactController, ReedContactData, ReedContactData.Builder> implements ContactStateProviderService {

    public ReedContactHandleConnector(final ReedContactController unit) throws InstantiationException {
        super(unit);
    }

    @Override
    public ServiceProvider getServiceProvider() {
        return unit;
    }

    @Override
    public ContactState getContactState() throws NotAvailableException {
        return unit.getData().getContactState();
    }

    @Override
    protected ServiceType getServiceType() {
        return ServiceType.CONTACT_STATE_SERVICE;
    }
}
