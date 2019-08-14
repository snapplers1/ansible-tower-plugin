
stage('Deploy with tower') {
    node('master') {
        ansibleTower(
                importTowerLogs: true,
                inventory: 'Jenkins Inventory',
                jobTags: '',
                jobTemplate: 'Jenkins Simple Test',
                limit: '',
                removeColor: true,
                towerServer: 'Tower 3.3.0',
                verbose: true,
                credential: '',
                extraVars: '''---
my_var: "Jenkins Test"'''
        )
    }
}