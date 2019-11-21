package org.jenkinsci.plugins.ansible_tower.util;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.plugins.ansible_tower.exceptions.AnsibleTowerException;
import org.jenkinsci.plugins.ansible_tower.exceptions.AnsibleTowerItemDoesNotExist;

import java.io.IOException;
import java.io.Serializable;

public class TowerProject implements Serializable {
    String projectName;
    TowerConnector myConnector = null;
    JSONObject projectData = null;
    JSONObject updateResponse = null;

    public TowerProject(String projectName, TowerConnector myConnector) throws AnsibleTowerException {
        this.projectName = projectName;
        this.myConnector = myConnector;
        if(projectName == null || projectName.isEmpty()) {
            throw new AnsibleTowerException("Template can not be null");
        }

        String apiEndPoint = "/projects/";

        String projectID = "";
        try {
            projectID = myConnector.convertPotentialStringToID(projectName, apiEndPoint);
        } catch(AnsibleTowerItemDoesNotExist atidne) {
            throw new AnsibleTowerException("Project "+ projectName +" does not exist in tower");
        } catch(AnsibleTowerException ate) {
            throw new AnsibleTowerException("Unable to find project "+ projectName +": "+ ate.getMessage());
        }

        // Now that we have an ID, get the project from its ID.
        HttpResponse response;
        try {
            response = myConnector.makeRequest(myConnector.GET, apiEndPoint + projectID + "/", null, false);
        } catch(AnsibleTowerException e) {
            throw new AnsibleTowerException("Failed to load project information for "+ projectName +": "+ e.getMessage());
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new AnsibleTowerException("Unexpected error code returned when getting project (" + response.getStatusLine().getStatusCode() + ")");
        }
        try {
            String json = EntityUtils.toString(response.getEntity());
            projectData = JSONObject.fromObject(json);
        } catch (IOException ioe) {
            throw new AnsibleTowerException("Unable to read project response and convert it into json: " + ioe.getMessage());
        }
    }

    public String getProjectSyncURL() {
        return this.projectData.getJSONObject("related").getString("update");
    }

    public boolean canUpdate() throws AnsibleTowerException {
        if(updateResponse == null) {
            if (!projectData.containsKey("related") || !projectData.getJSONObject("related").containsKey("update")) {
                return false;
            }

            HttpResponse response = myConnector.makeRequest(myConnector.GET, projectData.getJSONObject("related").getString("update"), null, false);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new AnsibleTowerException("Unexpected error code return when getting project update (" + response.getStatusLine().getStatusCode() + ")");
            }
            try {
                this.updateResponse = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
            } catch (IOException ioe) {
                throw new AnsibleTowerException("Unable to read project update response and convert it into json: " + ioe.getMessage());
            }
        }
        return this.updateResponse.getBoolean("can_update");
    }

    public TowerProjectSync sync() throws AnsibleTowerException {
        TowerProjectSync mySync = new TowerProjectSync(this.myConnector, this);
        return mySync;
    }

    public void releaseToken() throws AnsibleTowerException {
        myConnector.releaseToken();
    }
}
