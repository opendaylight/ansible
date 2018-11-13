/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ansible.southbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AnsibleEvent {
    private String uuid;
    private ObjectNode eventData;
    private String stdout;
    private int counter;
    private int pid;
    private String created;
    private int endLine;
    private int startLine;
    private String event;
    private String runnerIdent;

    public AnsibleEvent(String uuid, ObjectNode eventData, String stdout) {
        this.uuid = uuid;
        this.eventData = eventData;
        this.stdout = stdout;
    }

    public AnsibleEvent() {
    }

    @JsonProperty("event_data")
    public ObjectNode getEventData() {
        return eventData;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStdout() {
        return stdout;
    }

    @JsonProperty("event_data")
    public void setEventData(ObjectNode eventData) {
        this.eventData = eventData;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @JsonProperty("end_line")
    public int getEndLine() {
        return endLine;
    }

    @JsonProperty("end_line")
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @JsonProperty("start_line")
    public int getStartLine() {
        return startLine;
    }

    @JsonProperty("start_line")
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("runner_ident")
    public String getRunnerIdent() {
        return runnerIdent;
    }

    @JsonProperty("runner_ident")
    public void setRunnerIdent(String runnerIdent) {
        this.runnerIdent = runnerIdent;
    }
}
