/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import static org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.Status.InProgress;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.infrautils.inject.guice.testutils.GuiceRule;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.AnsibleCommandService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.RunAnsibleCommandInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.RunAnsibleCommandInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.RunAnsibleCommandOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.run.ansible.command.input.command.type.Playbook;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ansible.command.rev180821.run.ansible.command.input.command.type.PlaybookBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleCommandServiceImplTest {
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleCommandServiceImplTest.class);
    public @Rule MethodRule guice = new GuiceRule(new AnsibleCommandTestModule());

    @Inject
    private AnsibleCommandService ansibleCommandService;

    @Inject
    private DataBroker dataBroker;

    @Test
    public void testRunAnsibleCommand() throws ExecutionException, InterruptedException {
        Playbook playbook = new PlaybookBuilder().setFile("file").build();
        RunAnsibleCommandInput input = new RunAnsibleCommandInputBuilder()
            .setDirectory("./src/test/resources")
            .setCommandType(playbook)
            .setHost("localhost").build();
        Future<RpcResult<RunAnsibleCommandOutput>> output = ansibleCommandService.runAnsibleCommand(input);
        Assert.assertEquals(output.get().getResult().getStatus(), InProgress);
    }
}
