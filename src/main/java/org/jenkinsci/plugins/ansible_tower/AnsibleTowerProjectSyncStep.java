package org.jenkinsci.plugins.ansible_tower;

/*
    This class is the pipeline step
    We simply take the data from Jenkins and call an AnsibleTowerRunner
 */

import com.google.inject.Inject;
import hudson.*;
import hudson.model.Computer;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.ansible_tower.util.TowerInstallation;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Properties;

public class AnsibleTowerProjectSyncStep extends AbstractStepImpl {
    private String towerServer              = "";
    private String project                  = "";
    private Boolean verbose                 = false;
    private Boolean importTowerLogs         = false;
    private Boolean removeColor             = false;
    private Boolean throwExceptionWhenFail  = true;
    private Boolean async                   = false;

    @DataBoundConstructor
    public AnsibleTowerProjectSyncStep(
            @Nonnull String towerServer, @Nonnull String project, Boolean verbose,
            Boolean importTowerLogs, Boolean removeColor, Boolean throwExceptionWhenFail, Boolean async
    ) {
        this.towerServer = towerServer;
        this.project = project;
        this.verbose = verbose;
        this.importTowerLogs = importTowerLogs;
        this.removeColor = removeColor;
        this.throwExceptionWhenFail = throwExceptionWhenFail;
        this.async = async;
    }

    @Nonnull
    public String getTowerServer()              { return towerServer; }
    @Nonnull
    public String getProject()                  { return project; }
    public Boolean getVerbose()                 { return verbose; }
    public Boolean getImportTowerLogs()         { return importTowerLogs; }
    public Boolean getRemoveColor()             { return removeColor; }
    public Boolean getThrowExceptionWhenFail()  { return throwExceptionWhenFail; }
    public Boolean getAsync()                   { return async; }

    @DataBoundSetter
    public void setTowerServer(String towerServer) { this.towerServer = towerServer; }
    @DataBoundSetter
    public void setProject(String project) { this.project = project; }
    @DataBoundSetter
    public void setVerbose(Boolean verbose) { this.verbose = verbose; }
    @DataBoundSetter
    public void setImportTowerLogs(Boolean importTowerLogs) { this.importTowerLogs = importTowerLogs; }
    @DataBoundSetter
    public void setRemoveColor(Boolean removeColor) { this.removeColor = removeColor; }
    @DataBoundSetter
    public void setThrowExceptionWhenFail(Boolean throwExceptionWhenFail) { this.throwExceptionWhenFail = throwExceptionWhenFail; }
    @DataBoundSetter
    public void setAsync(Boolean async) { this.async = async; }

    public boolean isGlobalColorAllowed() {
        System.out.println("Using the class is global color allowed");
        return true;
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        public static final String towerServer              = AnsibleTowerProjectSyncStep.DescriptorImpl.towerServer;
        public static final String project                  = AnsibleTowerProjectSyncStep.DescriptorImpl.project;
        public static final Boolean verbose                 = AnsibleTowerProjectSyncStep.DescriptorImpl.verbose;
        public static final Boolean importTowerLogs         = AnsibleTowerProjectSyncStep.DescriptorImpl.importTowerLogs;
        public static final Boolean removeColor             = AnsibleTowerProjectSyncStep.DescriptorImpl.removeColor;
        public static final Boolean throwExceptionWhenFail  = AnsibleTowerProjectSyncStep.DescriptorImpl.throwExceptionWhenFail;
        public static final Boolean async                   = AnsibleTowerProjectSyncStep.DescriptorImpl.async;

        public DescriptorImpl() {
            super(AnsibleTowerProjectSyncStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "ansibleTowerProjectSync";
        }

        @Override
        public String getDisplayName() {
            return "Have Ansible Tower update a Tower project";
        }

        public ListBoxModel doFillTowerServerItems() {
            ListBoxModel items = new ListBoxModel();
            items.add(" - None -");
            for (TowerInstallation towerServer : AnsibleTowerGlobalConfig.get().getTowerInstallation()) {
                items.add(towerServer.getTowerDisplayName());
            }
            return items;
        }

        public boolean isGlobalColorAllowed() {
            System.out.println("Using the descriptor is global color allowed");
            return true;
        }
    }


    public static final class AnsibleTowerProjectSyncStepExecution extends AbstractSynchronousNonBlockingStepExecution<Properties> {
        private static final long serialVersionUID = 1L;

        @Inject
        private transient AnsibleTowerProjectSyncStep step;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Launcher launcher;

        @StepContextParameter
        private transient Run<?,?> run;

        @StepContextParameter
        private transient FilePath ws;

        @StepContextParameter
        private transient EnvVars envVars;

        @StepContextParameter
        private transient Computer computer;



        @Override
        protected Properties run() throws AbortException {
            if ((computer == null) || (computer.getNode() == null)) {
                throw new AbortException("The Ansible Tower Project Sync build step requires to be launched on a node");
            }

            AnsibleTowerRunner runner = new AnsibleTowerRunner();

            // Doing this will make the options optional in the pipeline step.
            String project = "";
            if(step.getProject() != null) { project = step.getProject(); }
            boolean verbose = false;
            if(step.getVerbose() != null) { verbose = step.getVerbose(); }
            boolean importTowerLogs = false;
            if(step.getImportTowerLogs() != null) { importTowerLogs = step.getImportTowerLogs(); }
            boolean removeColor = false;
            if(step.getRemoveColor() != null) { removeColor = step.getRemoveColor(); }
            boolean throwExceptionWhenFail = true;
            if(step.getThrowExceptionWhenFail() != null) { throwExceptionWhenFail = step.getThrowExceptionWhenFail(); }
            boolean async = false;
            if(step.getAsync() != null) { async = step.getAsync(); }
            Properties map = new Properties();
            boolean runResult = runner.projectSync(
                    listener.getLogger(), step.getTowerServer(), project, verbose, importTowerLogs,
                    removeColor, envVars, ws, run, map, async
            );
            if(!runResult && throwExceptionWhenFail) {
                throw new AbortException("Ansible Tower Project Sync build step failed");
            }
            return map;
        }
    }
}

