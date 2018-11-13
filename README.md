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

## Configuring L3VPN

This project supports configuring new sites into a pre-existing L3VPN environment consisting of Provider Edge (PE) routers
which may be Cisco (IOS CSR1kv)or Arista. This section goes over the steps of how an ODL user can provision out new customer
attachments to PE sites using ansible-networking as the backend driver. The PE routers must already exist and have BGP
and MPLS configuration done between them.

For example purposes this section will use a simple topology of 2 PEs (ios1, ios2) connected with a single link. ios1 has
a management IP address of 192.168.10.100, while ios2 is 192.168.20.100. The customers in this case are represented by
loopback interfaces, but a real interface may also be used.

### Configuring Sites

The first step to configuring L3VPN is to configure site information into ODL using the following URI:

`restconf/config/ietf-l3vpn-svc:l3vpn-svc/sites/site/blue`

Here the data is shown for the first site on ios1, called blue:

```json
{
    "site": [
        {
            "site-id": "blue",
            "site-network-accesses": {
                "site-network-access": [
                    {
                        "site-network-access-id": "ios1",
                        "vpn-attachment": {
                            "vpn-id": "blue_vpn1",
                            "site-role": "any-to-any-role"
                        },
                        "ip-connection": {
                            "ipv4": {
                                "addresses": {
                                    "provider-address": "162.168.10.100",
                                    "mask": "24"
                                }
                            }
                        },
                        "pe-2-ce-tp-id": "loopback5",
                        "pe-bgp-as": 10,
                        "username": "redhat",
                        "password": "redhat",
                        "device-type": "cisco",
                        "pe-mgmt-ip" : "192.168.10.100"
                    }
                ]
            }
        }
    ]
}
```

Next configure the second site, called blue2:

```json
{
    "site": [
        {
            "site-id": "blue2",
            "site-network-accesses": {
                "site-network-access": [
                    {
                        "site-network-access-id": "ios2",
                        "vpn-attachment": {
                            "vpn-id": "blue_vpn1",
                            "site-role": "any-to-any-role"
                        },
                        "ip-connection": {
                            "ipv4": {
                                "addresses": {
                                    "provider-address": "162.168.20.100",
                                    "mask": "24"
                                }
                            }
                        },
                        "pe-2-ce-tp-id": "loopback5",
                        "pe-bgp-as": 10,
                        "username": "redhat",
                        "password": "redhat",
                        "device-type": "cisco",
                        "pe-mgmt-ip" : "192.168.20.100"
                    }
                ]
            }
        }
    ]
}

```

In the above data, the `pe-mgmt-ip` is the management IP used to access the router with Ansible. Additionally the
`username`, `password` are the credentials used to login to the box. Think of these as the same as the inventory used
in Ansible to access a device. The `pe-bgp-as` is the Autonomous System (AS) value already configured in BGP on the nodes.
The `ip-connection` information is the addressing that will be used to configure the customer attachment interface defined
by `pe-2-ce-tp-id`.

### Configuring L3VPN

The next step is to configure the L3VPN, which contains a list of different VPNs you want to provision. In order to
configure the L3VPN, use the following URI:

`restconf/config/ietf-l3vpn-svc:l3vpn-svc/vpn-services/`

The data to be sent with this REST call:

```json
{
    "vpn-services": {
        "vpn-service": [
            {
                "vpn-id": "blue_vpn1",
                "customer-name": "blue",
                "vpn-service-topology": "ietf-l3vpn-svc:any-to-any",
                "vrf-name": "blue"
            }
        ]
    }
}
```

In the above data the name of the VRF to be configured on the routers is specified with `vrf-name`, along with the id of
the VPN to be configured. Note this `vpn-id` matches `vpn-id` under the per site configuration.

### Committing L3VPN

The final command to provision L3VPN between sites is to issue a commit using an RPC call:

`curl -u admin:admin -X POST http://10.19.41.2:8181/restconf/operations/ietf-l3vpn-svc:commit-l3vpn-svc`

This will trigger VPN creation running as Ansible-Runner role calls as multiple threads and show the status of L3VPN
as In-Progress. Once all threads are complete the overall status of the L3VPN will be updated to a Complete or Failed
status. In order to query the status of the L3VPN:

`curl -u admin:admin http://10.19.41.2:8181/restconf/operational/ietf-l3vpn-svc:status-l3vpn-provider/provider-state`

### Deleting L3VPN

To remove the previously configured VPN, simply issue the delete RPC call:

`curl -u admin:admin -X POST http://10.19.41.2:8181/restconf/operations/ietf-l3vpn-svc:delete-l3vpn-svc`
