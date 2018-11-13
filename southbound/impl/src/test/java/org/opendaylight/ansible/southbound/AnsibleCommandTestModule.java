/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.test.DataBrokerTestModule;
import org.opendaylight.infrautils.inject.guice.testutils.AbstractGuiceJsr250Module;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.AnsibleCommandService;
import org.ops4j.pax.cdi.api.OsgiService;

public class AnsibleCommandTestModule extends AbstractGuiceJsr250Module {
    @Override
    protected void configureBindings() {
        DataBroker dataBroker = DataBrokerTestModule.dataBroker();
        bind(DataBroker.class).toInstance(dataBroker);
        bind(DataBroker.class).annotatedWith(OsgiService.class).toInstance(dataBroker);
        bind(AnsibleCommandService.class).to(AnsibleCommandServiceImpl.class);
    }
}
