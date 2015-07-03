/**
 * This is a manifestation of the Continuous Delivery pipeline as described by Jez Humble and Dave Farley
 * See: http://www.amazon.co.uk/dp/0321601912?tag=contindelive-20
 */

def projectKey = 'PROJ_KEY'
def gitUrl = 'https://github.com/willis7/job-dsl-gradle-example.git'

/**
 * JOB 1 - Build and Unit test
 */
job("${projectKey}-unit-tests") {
    description 'Commit phase - This job is responsible for ensuring the source code compiles and all unit tests pass.'
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps {
        gradle('clean check')
    }
    publishers {
        archiveJunit 'build/test-results/*.xml'
        downstreamParameterized {
            trigger("${projectKey}-integ-tests") {
                gitRevision()
            }
        }
    }
}

/**
 * JOB 2 - Integration test
 * Depends On - Success of 'unit-tests'
 */
job("${projectKey}-integ-tests") {
    description 'Commit phase - Longer running tests which require an environment to test against.'
    scm {
        git(gitUrl)
    }
    steps {
        gradle('integTest')
    }
    publishers {
        archiveJunit 'build/test-results/integTest/*.xml'
        downstreamParameterized {
            trigger("${projectKey}-code-analysis") {
                gitRevision()
            }
        }
    }
}

/**
 * JOB 3 - Code analysis
 * Depends On - Success of 'integ-tests'
 */
job("${projectKey}-code-analysis") {
    description 'Perform a code health check and fail if low quality'
    scm {
        git(gitUrl)
    }
    steps {
        gradle('sonarRunner')
    }
}

/**
 * JOB 4 - Assemble
 * Depends On - Success of 'code-analysis'
 */
job("${projectKey}-assemble-dist") {
    description 'Commit phase - Assemble the distribution'
    scm {
        git(gitUrl)
    }
    steps {
        gradle('assemble')
    }
    publishers {
        archiveArtifacts 'build/libs/*.jar'
    }
}

/**
 * JOB 5 - Publish
 * Depends On - Success of 'assemble-dist'
 */
job("${projectKey}-publish-dist") {
    description 'Commit phase - Publish to a binary repository'
    scm {
        git(gitUrl)
    }
    steps {
        gradle('publish')
    }
}