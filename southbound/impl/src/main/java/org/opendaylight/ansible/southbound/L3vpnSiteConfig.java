/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.net.util.SubnetUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.l3vpn.svc.rev170502.l3vpn.svc.fields.sites.site.site.network.accesses.SiteNetworkAccess;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.l3vpn.svc.aug.rev170502.PeAugmentation;

public class L3vpnSiteConfig {

    public static class Attachment {
        private String netInterface;
        private String address;
        private String maskLen;

        @JsonProperty("interface")
        public String getNetInterface() {
            return netInterface;
        }

        @JsonProperty("interface")
        public void setNetInterface(String netInterface) {
            this.netInterface = netInterface;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @JsonProperty("masklen")
        public String getMaskLen() {
            return maskLen;
        }

        @JsonProperty("masklen")
        public void setMaskLen(String maskLen) {
            this.maskLen = maskLen;
        }
    }

    private List<Attachment> attachments;
    private String pe;

    public L3vpnSiteConfig(SiteNetworkAccess siteNetworkAccess) {
        Attachment attachment = new Attachment();
        PeAugmentation sitePeAug = siteNetworkAccess.augmentation(PeAugmentation.class);
        attachment.setNetInterface(sitePeAug.getPe2CeTpId().getValue());
        attachment.setAddress(siteNetworkAccess.getIpConnection().getIpv4().getAddresses().getProviderAddress()
                .getValue());
        String prefix = siteNetworkAccess.getIpConnection().getIpv4().getAddresses().getMask().toString();
        String subnet = attachment.getAddress() + "/" + prefix;
        SubnetUtils utils = new SubnetUtils(subnet);
        attachment.setMaskLen(utils.getInfo().getNetmask());
        attachments = Arrays.asList(attachment);
        pe = siteNetworkAccess.getSiteNetworkAccessId().getValue();
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }
}
