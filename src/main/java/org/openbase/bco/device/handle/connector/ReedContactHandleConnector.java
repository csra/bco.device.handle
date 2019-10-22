package org.openbase.bco.device.handle.connector;

import org.openbase.bco.dal.control.layer.unit.ReedContactController;
import org.openbase.bco.dal.lib.layer.service.ServiceProvider;
import org.openbase.bco.dal.lib.layer.service.provider.ContactStateProviderService;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.ContactStateType.ContactState;
import org.openbase.type.domotic.unit.dal.ReedContactDataType.ReedContactData;

public class ReedContactHandleConnector extends AbstractHandleConnector<ReedContactData, ReedContactData.Builder> implements ContactStateProviderService {

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
