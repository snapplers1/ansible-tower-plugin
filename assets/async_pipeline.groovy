stage('Launch Tower job') {
    node('master') {
        tower_job = ansibleTower(
                async: true,
                jobTemplate: 'Jenkins Export Vars',
                templateType: 'job',
                towerServer: 'Tower 3.3.0'
        )
        println("Tower job "+ tower_job.get("JOB_ID") +" was submitted. Job URL is: "+ tower_job.get("JOB_URL"))
    }
}

stage('Something else') {
    node('master') {
        println("Doing something else")
    }
}

stage('Wait for Tower job') {
    node('master') {
        def job = tower_job.get("job", null)
        if(job == null) {
            errir("The tower job was defined as null!")
        }
        timeout(120) {
            waitUntil {
                return job.isComplete()
            }
        }
    }
}

stage('Process Tower results') {
    node('master') {
        // Def a variable to save some typing
        def job = tower_job.get("job", null)
        if(job == null) {
            error("Tower job was null")
        }

        // First lets get and display the logs
        def Vector<String> logs = job.getLogs()
        for (String line : logs) {
            println(line)
        }

        // Now lets get our exports, these depend on us calling getLogs
        def HashMap<String, String> exports = job.getExports()
        def returned_value = exports.get("value", "Not Defined")
        if(returned_value != "T-REX") {
            println("Tower job did not return a T-Rex: "+ returned_value)
        } else {
            println("Exports were as expected")
        }

        // Finally, lets see if the job was successful
        boolean successful = job.wasSuccessful()
        if(successful) {
            println("Job ran successfully")
        } else {
            error("The job did not end well")
        }
    }
}