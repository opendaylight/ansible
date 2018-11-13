/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound;

import static org.opendaylight.genius.infra.Datastore.OPERATIONAL;
import static org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.Status.DeleteInProgress;
import static org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.Status.InProgress;

import com.google.common.util.concurrent.ListenableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.genius.infra.RetryingManagedNewTransactionRunner;
import org.opendaylight.ansible.northbound.api.IetfL3vpnSvcUtils;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.infrautils.utils.concurrent.ListenableFutures;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcInput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcOutput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.CommitL3vpnSvcOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.DeleteL3vpnSvcInput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.DeleteL3vpnSvcOutput;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.DeleteL3vpnSvcOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.IetfL3vpnSvcService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.ops4j.pax.cdi.api.OsgiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class IetfL3vpnSvcServiceImpl implements IetfL3vpnSvcService {
    private static final Logger LOG = LoggerFactory.getLogger(IetfL3vpnSvcServiceImpl.class);
    private final RetryingManagedNewTransactionRunner txRunner;
    private static final String COMMIT_VERSION = "123";

    @Inject
    public IetfL3vpnSvcServiceImpl(@OsgiService final DataBroker dataBroker) {
        LOG.info("constructor");
        this.txRunner = new RetryingManagedNewTransactionRunner(dataBroker);
    }

    @Override
    public ListenableFuture<RpcResult<CommitL3vpnSvcOutput>> commitL3vpnSvc(CommitL3vpnSvcInput input) {
        ListenableFutures.checkedGet(
            txRunner.callWithNewReadWriteTransactionAndSubmit(OPERATIONAL, tx -> {
                IetfL3vpnSvcUtils.putStatusL3VPN(tx, COMMIT_VERSION, InProgress);
            }), null);

        return RpcResultBuilder.success(
            new CommitL3vpnSvcOutputBuilder().setL3vpnSvcVersion(COMMIT_VERSION).build()).buildFuture();
    }

    @Override
    public ListenableFuture<RpcResult<DeleteL3vpnSvcOutput>> deleteL3vpnSvc(DeleteL3vpnSvcInput input) {
        ListenableFutures.checkedGet(
                txRunner.callWithNewReadWriteTransactionAndSubmit(OPERATIONAL, tx -> {
                    IetfL3vpnSvcUtils.putStatusL3VPN(tx, COMMIT_VERSION, DeleteInProgress);
                }), null);

        return RpcResultBuilder.success(
                new DeleteL3vpnSvcOutputBuilder().setL3vpnSvcVersion(COMMIT_VERSION).build()).buildFuture();
    }
}
