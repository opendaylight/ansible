/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.northbound.api;

import com.google.common.base.Optional;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.opendaylight.genius.infra.Datastore;
import org.opendaylight.genius.datastoreutils.SingleTransactionDataBroker;
import org.opendaylight.genius.infra.TypedReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Destination;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.DestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Source;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.SourceBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.LinkKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnsibleTopology {
    public static final TopologyId ANSIBLE_TOPOLOGY_ID = new TopologyId("flow:1");
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleTopology.class);

    private AnsibleTopology() {}

    public static final InstanceIdentifier<Topology> ANSIBLE_TOPOLOGY_PATH =
        InstanceIdentifier.create(NetworkTopology.class).child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID));

    public static final InstanceIdentifier<Node> ANSIBLE_NODE_PATH =
        InstanceIdentifier.create(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID))
            .child(Node.class);

    public static final InstanceIdentifier<Link> ANSIBLE_LINK_PATH =
            InstanceIdentifier.create(NetworkTopology.class)
                    .child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID))
                    .child(Link.class);

    public static Node getNode(NodeId nodeId, DataBroker dataBroker) {
        InstanceIdentifier<Node> nodePath = InstanceIdentifier.create(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID))
                .child(Node.class, new NodeKey(nodeId));
        Node node = null;
        try {
            Optional<Node> optionalNode = SingleTransactionDataBroker.syncReadOptional(dataBroker,
                    LogicalDatastoreType.OPERATIONAL, nodePath);
            if (optionalNode.isPresent()) {
                node = optionalNode.get();
            }
        } catch (ReadFailedException e) {
            LOG.warn("Read exception...Unable to find matching topology node: {}", nodeId);
        }

        return node;
    }

    public static void populateLink(Link link, Boolean remove, TypedReadWriteTransaction<? extends Datastore> tx) {
        InstanceIdentifier<Link> linkPath = InstanceIdentifier.create(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID))
                .child(Link.class, new LinkKey(link.getLinkId()));
        if (remove) {
            LOG.info("Removing link: {}", link.getLinkId());
            tx.delete(linkPath);
        } else {
            LOG.info("Adding new link: {}", link.getLinkId());
            tx.put(linkPath, link, true);
        }
    }

    public static List<Link> getLinks(DataBroker dataBroker) {
        InstanceIdentifier<Topology> topologyPath = InstanceIdentifier.create(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(ANSIBLE_TOPOLOGY_ID));
        List<Link> links = null;
        try {
            Optional<Topology> optionalTopo = SingleTransactionDataBroker.syncReadOptional(dataBroker,
                    LogicalDatastoreType.OPERATIONAL, topologyPath);
            if (optionalTopo.isPresent()) {
                links = optionalTopo.get().getLink();
            }
        } catch (ReadFailedException e) {
            LOG.warn("Read exception...Unable to find links in topology");
        }
        return links;
    }

    public static Source getLinkSource(Destination dest, DataBroker dataBroker) {
        List<Link> links = getLinks(dataBroker);
        if (links == null) {
            return null;
        }
        for (Link link : links) {
            if (link.getDestination().equals(dest)) {
                LOG.info("Matching Source link found for link: {}", link.getLinkId().getValue());
                return link.getSource();
            }
        }
        LOG.info("Did not find any matching source links for dest link node: {}, interface: {}",
                dest.getDestNode().getValue(), dest.getDestTp().getValue());
        return null;
    }

    public static Destination getLinkDest(Source source, DataBroker dataBroker) {
        List<Link> links = getLinks(dataBroker);
        if (links == null) {
            return null;
        }
        for (Link link : links) {
            if (link.getSource().equals(source)) {
                LOG.info("Matching Source link found for link: {}", link.getLinkId().getValue());
                return link.getDestination();
            }
        }
        LOG.info("Did not find any matching dest links for source link node: {}, interface: {}",
                source.getSourceNode().getValue(), source.getSourceTp().getValue());
        return null;
    }

    public static Pair<NodeId, TpId> getLinkPeer(NodeId nodeId, TpId tpId, DataBroker dataBroker) {
        // method searches for a link peer, returns the node Id and Tp ID
        // try to search first for links that match destination
        Destination origDest = new DestinationBuilder().setDestNode(nodeId)
                .setDestTp(tpId).build();
        Source source = getLinkSource(origDest, dataBroker);
        if (source != null) {
            return new ImmutablePair<>(source.getSourceNode(), source.getSourceTp());
        }
        // searching for links that matched dest failed, now search for source
        Source origSource = new SourceBuilder().setSourceNode(nodeId)
                .setSourceTp(tpId).build();
        Destination dest = getLinkDest(origSource, dataBroker);
        if (dest != null) {
            return new ImmutablePair<>(dest.getDestNode(), dest.getDestTp());
        }
        // If we get here we did not find any links for this node id and tpid
        LOG.warn("Unable to find any links with nodeid: {}, and tpid: {}", nodeId.getValue(), tpId.getValue());
        return null;
    }
}
