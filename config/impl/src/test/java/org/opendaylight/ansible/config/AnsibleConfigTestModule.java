/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.config;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.test.DataBrokerTestModule;
import org.opendaylight.infrautils.inject.guice.testutils.AbstractGuiceJsr250Module;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.config.rev180821.AnsibleConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.config.rev180821.AnsibleConfigBuilder;

public class AnsibleConfigTestModule extends AbstractGuiceJsr250Module {
    @Override
    protected void configureBindings() {
        DataBroker dataBroker = DataBrokerTestModule.dataBroker();
        bind(DataBroker.class).toInstance(dataBroker);
        AnsibleConfig ansibleConfig = new AnsibleConfigBuilder()
            .setAnsibleRunnerName("ansible-runner").setAnsibleRunnerPath(".").build();
        bind(AnsibleConfig.class).toInstance(ansibleConfig);
        bind(org.opendaylight.ansible.config.api.AnsibleConfig.class).to(AnsibleConfigImpl.class);
    }
}
