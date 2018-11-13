/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import ch.vorburger.exec.ManagedProcessListener;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Uuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnsibleProcessListener implements ManagedProcessListener {
    private Uuid processUuid;
    private AnsibleCommandServiceImpl commandService;
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleProcessListener.class);

    public AnsibleProcessListener(AnsibleCommandServiceImpl commandService, Uuid processUuid) {
        this.commandService = commandService;
        this.processUuid = processUuid;
    }

    @Override
    public void onProcessComplete(int rc) {
        try {
            commandService.parseAnsibleResult(processUuid);
        } catch (AnsibleCommandException e) {
            LOG.error("Unable to complete process callback", e);
        }
    }

    @Override
    public void onProcessFailed(int rc, Throwable throwable) {
        try {
            commandService.parseAnsibleResult(processUuid);
        } catch (AnsibleCommandException e) {
            LOG.error("Unable to complete process callback", e);
        }
    }
}
