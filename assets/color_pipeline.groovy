node {
    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: "xterm"]) {
        ansibleTower(
                towerServer: 'Tower 3.3.0',
                jobTemplate: 'Jenkins Simple Test',
                importTowerLogs: true,
                inventory: 'Jenkins Inventory',
                jobTags: '',
                limit: '',
                removeColor: false,
                verbose: true,
                credential: '',
                extraVars: '''---
my_var: "Jenkins Test"'''
        )
    }
}
