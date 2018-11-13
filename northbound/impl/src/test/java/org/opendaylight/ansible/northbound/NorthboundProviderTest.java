/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.opendaylight.genius.infra.Datastore.CONFIGURATION;
import static org.opendaylight.ansible.northbound.api.AnsibleTopology.ANSIBLE_TOPOLOGY_PATH;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.genius.infra.Datastore.Configuration;
import org.opendaylight.genius.infra.RetryingManagedNewTransactionRunner;
import org.opendaylight.genius.datastoreutils.SingleTransactionDataBroker;
import org.opendaylight.genius.infra.TypedReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.test.ConstantSchemaAbstractDataBrokerTest;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NorthboundProviderTest extends ConstantSchemaAbstractDataBrokerTest {
    private static final Logger LOG = LoggerFactory.getLogger(NorthboundProviderTest.class);
    private DataBroker dataBroker;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        dataBroker = getDataBroker();
        NorthboundProvider northboundProvider = new NorthboundProvider(dataBroker);
    }

    // There are four different methods to read from datastore in the tests below.
    // The last method is the preferred.

    @Test
    public void testInitializeAnsibleTopology() throws ExecutionException, InterruptedException {
        ReadOnlyTransaction transaction = dataBroker.newReadOnlyTransaction();
        CheckedFuture<Optional<Topology>, ReadFailedException> ansibleTopology =
            transaction.read(LogicalDatastoreType.CONFIGURATION, ANSIBLE_TOPOLOGY_PATH);
        assertTrue(ansibleTopology.get().isPresent());
    }

    @Test
    public void testInitializeAnsibleTopology2() throws ReadFailedException {
        assertTrue(SingleTransactionDataBroker.syncReadOptional(dataBroker,
            LogicalDatastoreType.CONFIGURATION, ANSIBLE_TOPOLOGY_PATH).isPresent());
    }

    @Test
    public void testInitializeAnsibleTopology3() throws ReadFailedException {
        Optional<Topology> ansibleTopology = SingleTransactionDataBroker.syncReadOptional(dataBroker,
            LogicalDatastoreType.CONFIGURATION, ANSIBLE_TOPOLOGY_PATH);
        assertTrue(ansibleTopology.isPresent());
        assertNotNull(ansibleTopology.get());
    }

    private Optional<Topology>  getAnsibleTopology(TypedReadWriteTransaction<Configuration> tx)
        throws ExecutionException, InterruptedException {
        Optional<Topology> ansibleTopology = tx.read(ANSIBLE_TOPOLOGY_PATH).get();
        // do not return null, topology should be there
        // if ok not to be there, return the optional
        return ansibleTopology;
    }

    @Test
    public void testInitializeAnsibleTopology4() {
        RetryingManagedNewTransactionRunner txRunner =
            new RetryingManagedNewTransactionRunner(dataBroker, 3);
        txRunner.callWithNewReadWriteTransactionAndSubmit(CONFIGURATION, tx -> {
            Optional<Topology> ansibleTopology = getAnsibleTopology(tx);
            assertTrue(ansibleTopology.isPresent());
            assertNotNull(ansibleTopology.get());
        });
    }
}
