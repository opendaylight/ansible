/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnsibleEventList {
    private List<AnsibleEvent> eventList;
    private static final Logger LOG = LoggerFactory.getLogger(AnsibleEventList.class);

    public AnsibleEventList(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        eventList = mapper.readValue(jsonString, new TypeReference<List<AnsibleEvent>>() {
        });
    }

    public List<AnsibleEvent> getEventList() {
        return eventList;
    }

    public AnsibleEvent getLastEvent() {
        return eventList.get(eventList.size() - 1);
    }

    public boolean ansiblePassed() throws AnsibleCommandException {
        ObjectNode lastData = getLastEvent().getEventData();
        LOG.info("Last event is: " + lastData.toString());
        if (!lastData.has("failures")) {
            throw new AnsibleCommandException("Unable to parse final Ansible output for failure:"
                    + lastData.toString());
        }
        JsonNode xn = lastData.findValue("failures");

        if (xn.size() == 0) {
            return true;
        } else {
            LOG.error("Failure data is: " + xn.toString());
            return false;
        }
    }

    public AnsibleEvent getFailedEvent() {
        for (AnsibleEvent i : eventList) {
            if (i.getEvent().equals("runner_on_failed")) {
                return i;
            }
        }
        return null;
    }
}
