/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.config;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.config.rev180821.AnsibleConfig;

@Singleton
public class AnsibleConfigImpl implements org.opendaylight.ansible.config.api.AnsibleConfig {
    private final AnsibleConfig ansibleConfig;

    @Inject
    AnsibleConfigImpl(final AnsibleConfig ansibleConfig) {
        this.ansibleConfig = ansibleConfig;
    }

    @Override
    public String getAnsibleRunnerName() {
        return ansibleConfig.getAnsibleRunnerName();
    }

    @Override
    public String getAnsibleRunnerPath() {
        return ansibleConfig.getAnsibleRunnerPath();
    }
}
