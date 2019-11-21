package org.jenkinsci.plugins.ansible_tower.exceptions;

/*
    Just our own type of exception
 */

public class AnsibleTowerDoesNotSupportAuthToken extends AnsibleTowerException {
    public AnsibleTowerDoesNotSupportAuthToken(String message) {
        super(message);
    }
}
