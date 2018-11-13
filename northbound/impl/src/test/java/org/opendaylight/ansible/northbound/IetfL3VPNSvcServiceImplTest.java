/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opendaylight.genius.infra.Datastore.OPERATIONAL;
import static org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.Status.InProgress;

import com.google.common.base.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.opendaylight.genius.infra.RetryingManagedNewTransactionRunner;
import org.opendaylight.ansible.northbound.api.IetfL3vpnSvcUtils;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.infrautils.inject.guice.testutils.GuiceRule;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcInput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcInputBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcOutput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.IetfL3vpnSvcService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.StatusL3vpnProvider;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IetfL3VPNSvcServiceImplTest {
    private static final Logger LOG = LoggerFactory.getLogger(IetfL3VPNSvcServiceImplTest.class);
    public @Rule MethodRule guice = new GuiceRule(new IetfL3VPNSvcServiceTestModule());

    @Inject
    private IetfL3vpnSvcService ietfL3vpnSvcService;

    @Inject
    private DataBroker dataBroker;

    @Test
    public void testCommitL3vpnSvc() throws ExecutionException, InterruptedException {
        CommitL3vpnSvcInput input = new CommitL3vpnSvcInputBuilder().build();
        Future<RpcResult<CommitL3vpnSvcOutput>> output = ietfL3vpnSvcService.commitL3vpnSvc(input);
        Assert.assertEquals(output.get().getResult().getL3vpnSvcVersion(), "123");

        RetryingManagedNewTransactionRunner txRunner =
            new RetryingManagedNewTransactionRunner(dataBroker, 3);
        txRunner.callWithNewReadWriteTransactionAndSubmit(OPERATIONAL, tx -> {
            Optional<StatusL3vpnProvider> status = IetfL3vpnSvcUtils.getStatusL3vpn(tx);
            assertTrue(status.isPresent());
            assertEquals(status.get().getProviderState().getStatus(), InProgress);
        });
    }
}
