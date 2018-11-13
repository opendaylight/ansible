/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound.api;

import com.google.common.base.Optional;
import java.util.concurrent.ExecutionException;
import org.opendaylight.genius.infra.Datastore;
import org.opendaylight.genius.infra.TypedReadWriteTransaction;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.Status;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.StatusL3vpnProvider;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.StatusL3vpnProviderBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.status.l3vpn.provider.ProviderState;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.status.l3vpn.provider.ProviderStateBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public final class IetfL3vpnSvcUtils {

    private IetfL3vpnSvcUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final InstanceIdentifier<StatusL3vpnProvider> STATUS_L3VPN_PATH =
        InstanceIdentifier.create(StatusL3vpnProvider.class);

    public static Optional<StatusL3vpnProvider> getStatusL3vpn(TypedReadWriteTransaction<? extends Datastore> tx)
        throws ExecutionException, InterruptedException {
        return tx.read(STATUS_L3VPN_PATH).get();
    }

    public static Status getStatus(TypedReadWriteTransaction<? extends Datastore> tx)
        throws ExecutionException, InterruptedException {
        Status l3vpnStatus = null;
        Optional<StatusL3vpnProvider> optionalStatus = tx.read(STATUS_L3VPN_PATH).get();
        if (optionalStatus.isPresent()) {
            ProviderState providerState = optionalStatus.get().getProviderState();
            if (providerState.getStatus() != null) {
                l3vpnStatus = providerState.getStatus();
            }
        }

        return l3vpnStatus;
    }

    public static void putStatusL3VPN(TypedReadWriteTransaction<? extends Datastore> tx,
                                      String version, Status status) {
        StatusL3vpnProvider statusL3vpnProviderBuilder = new StatusL3vpnProviderBuilder()
            .setProviderState(
                new ProviderStateBuilder().setStatus(status).setL3vpnSvcVersion(version).build())
            .build();
        tx.put(STATUS_L3VPN_PATH, statusL3vpnProviderBuilder, true);
    }
}
