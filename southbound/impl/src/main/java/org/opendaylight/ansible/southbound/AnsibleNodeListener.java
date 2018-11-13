/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import static org.opendaylight.ansible.northbound.api.AnsibleTopology.ANSIBLE_NODE_PATH;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.serviceutils.tools.mdsal.listener.AbstractSyncDataTreeChangeListener;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.ops4j.pax.cdi.api.OsgiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AnsibleNodeListener extends AbstractSyncDataTreeChangeListener<Node> {
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleNodeListener.class);

    @Inject
    public AnsibleNodeListener(@OsgiService final DataBroker dataBroker) {
        super(dataBroker, LogicalDatastoreType.CONFIGURATION, ANSIBLE_NODE_PATH);
        LOG.info("constructor");
    }

    @Override
    public void add(@Nonnull InstanceIdentifier<Node> instanceIdentifier, @Nonnull Node node) {
        LOG.info("add: id: {}\nnode: {}", instanceIdentifier, node);
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
