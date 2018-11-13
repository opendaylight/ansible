/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.ops4j.pax.cdi.api.OsgiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class SouthboundProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SouthboundProvider.class);

    @Inject
    public SouthboundProvider(@OsgiService final DataBroker dataBroker) {
        LOG.info("constructor");

    }

}
