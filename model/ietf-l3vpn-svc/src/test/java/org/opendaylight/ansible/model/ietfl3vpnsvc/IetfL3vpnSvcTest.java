/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.model.ietfl3vpnsvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opendaylight.genius.infra.Datastore.CONFIGURATION;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.genius.infra.Datastore.Configuration;
import org.opendaylight.genius.infra.RetryingManagedNewTransactionRunner;
import org.opendaylight.genius.infra.TypedReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.test.ConstantSchemaAbstractDataBrokerTest;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.SvcId;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.Sites;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.VpnServices;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.Site;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.SiteBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.SiteKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.site.SiteNetworkAccesses;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.site.SiteNetworkAccessesBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.site.site.network.accesses.SiteNetworkAccess;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.site.site.network.accesses.SiteNetworkAccessBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.vpn.services.VpnService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.vpn.services.VpnServiceBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.vpn.services.VpnServiceKey;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.l3vpn.svc.aug.rev170502.PeAugmentation;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.l3vpn.svc.aug.rev170502.PeAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IetfL3vpnSvcTest extends ConstantSchemaAbstractDataBrokerTest {
    private static final Logger LOG = LoggerFactory.getLogger(IetfL3vpnSvcTest.class);
    private DataBroker dataBroker;

    @Before
    public void setUp() {
        dataBroker = getDataBroker();
    }

    @Test
    public void testConfigureVpn() {
        String customerName = "red";
        String vpnId = "vpn";
        VpnServiceKey serviceKey = new VpnServiceKey(new SvcId(vpnId));
        InstanceIdentifier<VpnService> servicePath =
            InstanceIdentifier.create(VpnServices.class).child(VpnService.class, serviceKey);
        VpnService vpnService = new VpnServiceBuilder().setCustomerName(customerName).build();
        RetryingManagedNewTransactionRunner txRunner =
            new RetryingManagedNewTransactionRunner(dataBroker, 3);
        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            tx.put(servicePath, vpnService);
        });

        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            Optional<VpnService> vpnServiceOptional = tx.read(servicePath).get();
            assertTrue(vpnServiceOptional.isPresent());
            assertEquals(vpnServiceOptional.get().getCustomerName(), customerName);
        });
    }

    private Optional<VpnService> getVpnService(TypedReadWriteTransaction<Configuration> tx, String vpnId)
        throws ExecutionException, InterruptedException {
        VpnServiceKey serviceKey = new VpnServiceKey(new SvcId(vpnId));
        InstanceIdentifier<VpnService> servicePath =
            InstanceIdentifier.create(VpnServices.class).child(VpnService.class, serviceKey);
        return tx.read(servicePath).get();
    }

    private void putVpnService(TypedReadWriteTransaction<Configuration> tx, String vpnId, VpnService vpnService) {
        VpnServiceKey serviceKey = new VpnServiceKey(new SvcId(vpnId));
        InstanceIdentifier<VpnService> servicePath =
            InstanceIdentifier.create(VpnServices.class).child(VpnService.class, serviceKey);
        tx.put(servicePath, vpnService);
    }

    @Test
    public void testConfigureVpn2() {
        String customerName = "red2";
        String vpnId = "vpn2";
        VpnService vpnService = new VpnServiceBuilder().setCustomerName(customerName).build();
        RetryingManagedNewTransactionRunner txRunner =
            new RetryingManagedNewTransactionRunner(dataBroker, 3);
        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            putVpnService(tx, vpnId, vpnService);
        });
        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            Optional<VpnService> vpnServiceOptional = getVpnService(tx, vpnId);
            assertTrue(vpnServiceOptional.isPresent());
            assertEquals(vpnServiceOptional.get().getCustomerName(), customerName);
        });
    }

    @Test
    public void testSites() {
        PeAugmentation peAugmentation =
            new PeAugmentationBuilder().setPeNodeId(NodeId.getDefaultInstance("node1")).build();
        SiteNetworkAccess siteNetworkAccess =
            new SiteNetworkAccessBuilder()
                .setSiteNetworkAccessId(SvcId.getDefaultInstance("pe1"))
                .addAugmentation(PeAugmentation.class, peAugmentation)
                .build();
        List<SiteNetworkAccess> siteNetworkAccessList = new ArrayList<>();
        siteNetworkAccessList.add(siteNetworkAccess);
        SiteNetworkAccesses siteNetworkAccesses =
            new SiteNetworkAccessesBuilder().setSiteNetworkAccess(siteNetworkAccessList).build();
        Site site = new SiteBuilder()
            .setSiteId(SvcId.getDefaultInstance("site1"))
            .setSiteNetworkAccesses(siteNetworkAccesses)
            .build();

        SiteKey siteKey = new SiteKey(SvcId.getDefaultInstance("site1"));
        InstanceIdentifier<Site> sitePath = InstanceIdentifier.create(Sites.class).child(Site.class, siteKey);

        RetryingManagedNewTransactionRunner txRunner =
            new RetryingManagedNewTransactionRunner(dataBroker, 3);
        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            tx.put(sitePath, site);
        });

        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            Optional<Site> siteOptional = tx.read(sitePath).get();
            assertTrue(siteOptional.isPresent());
            assertEquals(siteOptional.get().getSiteId().getValue(), "site1");
            List<SiteNetworkAccess> siteNetworkAccessList2 =
                siteOptional.get().getSiteNetworkAccesses().getSiteNetworkAccess();
            PeAugmentation peAugmentation2 = siteNetworkAccessList2.get(0).augmentation(PeAugmentation.class);
            assertEquals(peAugmentation2.getPeNodeId().getValue(), "node1");
        });
    }
}