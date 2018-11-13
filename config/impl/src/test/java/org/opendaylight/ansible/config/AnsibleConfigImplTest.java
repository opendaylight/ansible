/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.config;

import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.opendaylight.ansible.config.api.AnsibleConfig;
import org.opendaylight.infrautils.inject.guice.testutils.GuiceRule;

public class AnsibleConfigImplTest {
    public @Rule MethodRule guice = new GuiceRule(new AnsibleConfigTestModule());

    @Inject
    private AnsibleConfig ansibleConfig;

    @Test
    public void testGetAnsibleConfig() {
        Assert.assertEquals(ansibleConfig.getAnsibleRunnerName(), "ansible-runner");
        Assert.assertEquals(ansibleConfig.getAnsibleRunnerPath(), ".");
    }
}
