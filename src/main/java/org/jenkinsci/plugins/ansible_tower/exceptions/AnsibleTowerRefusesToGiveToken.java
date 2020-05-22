package org.jenkinsci.plugins.ansible_tower.exceptions;

/*
    Just our own type of exception
 */

public class AnsibleTowerRefusesToGiveToken extends AnsibleTowerException {
    public AnsibleTowerRefusesToGiveToken(String message) {
        super(message);
    }
}
