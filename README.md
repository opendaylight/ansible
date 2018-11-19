[![Build Status](https://travis-ci.com/shague/opendaylight-ansible.svg?branch=master)](https://travis-ci.com/shague/opendaylight-ansible)
[![Coverage Status](https://coveralls.io/repos/github/shague/opendaylight-ansible/badge.svg?branch=master)](https://coveralls.io/github/shague/opendaylight-ansible?branch=master)
[![License](https://img.shields.io/badge/License-EPL%201.0-blue.svg)](https://opensource.org/licenses/EPL-1.0)

# opendaylight-ansible

## Executing Ansible Playbooks

Southbound Ansible execution may be directly invoked by using RPCs. Either a playbook can be executed or
a role directly. To execute a playbook you must provide the host, directory, and playbook file:
To run a `test.yaml` playbook in `/tmp` on localhost:

`curl -i -u admin:admin -X POST -H "Content-Type: application/json" 127.0.0.1:8181/restconf/operations/ansible-command:run-ansible-command -d '{"input": {"host":"localhost", "directory":"/tmp", "file":"test.yaml"}}'`

The RPC will then return a uuid of the command requested:

`{"output":{"status":"INPROGRESS","uuid":"2bb34fab-2e05-42f6-acec-ca0c5cf02ad2"}}`

This uuid can now be used to query the operational database to see the result of the Command:

`curl -u admin:admin 127.0.0.1:8181/restconf/operational/ansible-command:commands/command/2bb34fab-2e05-42f6-acec-ca0c5cf02ad2/`

`{"command":[{"uuid":"2bb34fab-2e05-42f6-acec-ca0c5cf02ad2","status":"COMPLETE"}]}`

In case of a failed command the output from the Ansible Task which failed will also be present:

`{"command":[{"uuid":"75ab1888-c742-46c3-9d77-5fdfe4b22df3","status":"FAILED","failed-event":"\u001b[0;31mfatal: [localhost]: FAILED! => {\"changed\": true, \"cmd\": \"alsjf\", \"delta\": \"0:00:00.002941\", \"end\": \"2018-08-30 15:56:00.770642\", \"failed\": true, \"rc\": 127, \"start\": \"2018-08-30 15:56:00.767701\", \"stderr\": \"/bin/sh: alsjf: command not found\", \"stderr_lines\": [\"/bin/sh: alsjf: command not found\"], \"stdout\": \"\", \"stdout_lines\": []}\u001b[0m"}]}`

Additionally, Ansible variables may be provided which can set things like the SSH user/password for a command:

`curl -u admin:admin -X POST -H "Content-Type: application/json" 127.0.0.1:8181/restconf/operations/ansible-command:run-ansible-command -d '{"input": {"host":"192.168.10.100", "ansible-vars": ["ansible_user=redhat", "ansible_ssh_pass=redhat", "ansible_connection=network_cli", "ansible_network_os=ios"], "directory":"/tmp/test", "file":"test.
 yaml"}}'`

 ## Executing Ansible Roles

In order to execute an Ansible role directly, the `role-name` must be provided, and optionally, any role variables
to be set for the role via `role-vars`. For example:

`curl -u admin:admin -X POST -H "Content-Type: application/json" 127.0.0.1:8181/restconf/operations/ansible-command:run-ansible-command -d '{"input": {"host":"192.168.10.100", "ansible-vars": ["ansible_user=redhat", "ansible_ssh_pass=redhat", "ansible_connection=network_cli", "ansible_network_os=ios"], "directory":"/tmp/test", "role-vars": ["blah=dummy", "test=hello"], "role-name":"ansible-network.cisco_ios"}}'`
