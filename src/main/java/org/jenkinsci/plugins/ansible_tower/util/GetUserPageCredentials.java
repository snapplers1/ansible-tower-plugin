package org.jenkinsci.plugins.ansible_tower.util;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;


public class GetUserPageCredentials {
    // This method is from
    // https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc#providing-a-ui-form-element-to-let-a-user-select-credentials
    public static ListBoxModel getUserAvailableCredentials(Item item, String credentialsId) {
        StandardListBoxModel result = new StandardListBoxModel();
        if (item == null) {
            if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                return result.includeCurrentValue(credentialsId);
            }
        } else {
            if (!item.hasPermission(Item.EXTENDED_READ) && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                return result.includeCurrentValue(credentialsId);
            }
        }
        return result.includeEmptyValue().includeAs(
                ACL.SYSTEM,
                Jenkins.get(),
                StandardUsernameCredentials.class
        ).includeAs(
                ACL.SYSTEM,
                Jenkins.get(),
                StringCredentials.class
        ).includeCurrentValue(credentialsId);
    }
}
