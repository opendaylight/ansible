/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound;

import static org.opendaylight.genius.infra.Datastore.OPERATIONAL;
import static org.opendaylight.ansible.northbound.api.AnsibleTopology.ANSIBLE_NODE_PATH;

import com.google.common.base.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.genius.infra.RetryingManagedNewTransactionRunner;
import org.opendaylight.genius.datastoreutils.SingleTransactionDataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.serviceutils.tools.mdsal.listener.AbstractSyncDataTreeChangeListener;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.ops4j.pax.cdi.api.OsgiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AnsibleNodeListener extends AbstractSyncDataTreeChangeListener<Node> {
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleNodeListener.class);
    private final RetryingManagedNewTransactionRunner txRunner;
    private DataBroker dataBroker;
    private TopologyId flowId = new TopologyId("flow:1");
    private InstanceIdentifier<Topology> flowTopoId = InstanceIdentifier.create(NetworkTopology.class).child(
            Topology.class, new TopologyKey(flowId));

    @Inject
    public AnsibleNodeListener(@OsgiService final DataBroker dataBroker) {
        super(dataBroker, LogicalDatastoreType.CONFIGURATION, ANSIBLE_NODE_PATH);
        LOG.info("constructor");
        this.txRunner = new RetryingManagedNewTransactionRunner(dataBroker);
        this.dataBroker = dataBroker;
    }

    @Override
    public void add(@Nonnull InstanceIdentifier<Node> instanceIdentifier, @Nonnull Node node) {
        LOG.info("add: id: {}\nnode: {}", instanceIdentifier, node);
        try {
            Optional<Node> myNode = SingleTransactionDataBroker.syncReadOptional(dataBroker,
                    LogicalDatastoreType.CONFIGURATION, instanceIdentifier);
            if (myNode.isPresent()) {
                LOG.info("Node found in configuration datastore");

                txRunner.callWithNewReadWriteTransactionAndSubmit(OPERATIONAL, tx -> {
                    tx.put(instanceIdentifier, myNode.get());
                    LOG.info("Node written to oper: {}", myNode.get().getNodeId().getValue());
                });
            } else {
                LOG.error("Failed to read topology node from configuration during add");
            }

        } catch (ReadFailedException e) {
            LOG.error("Error reading ansible node during add");
        }
    }

    @Override
    public void remove(@Nonnull InstanceIdentifier<Node> instanceIdentifier, @Nonnull Node node) {
        LOG.info("remove: id: {}\nnode: {}", instanceIdentifier, node);
    }

    @Override
    public void update(@Nonnull InstanceIdentifier<Node> instanceIdentifier,
                       @Nonnull Node oldNode, @Nonnull Node newNode) {
        LOG.info("update: id: {}\nold node: {}\nold node: {}", instanceIdentifier, oldNode, newNode);
    }
}
