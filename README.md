NexusRpmGradlePlugin
====================
Simple Gradle Plugin for uploading RPM Artifacts to Nexus 

Usage:
------
```
ext {
   rpmRelease = '0'
   rpmArch = 'NOARCH'
}

buildscript {
       dependencies {
    
        classpath group: 'ua.pp.msk.gradle', name: 'NexusRpmGradlePlugin', version: '1.0.0'
        classpath group: 'org.apache.httpcomponents', name: 'httpmime', version: '4.2.2'
        classpath 'org.apache.httpcomponents:httpclient:4.2.2'

    }
}

apply plugin: 'NexusRpmGradlePlugin'

uploadRpm {
//dependsOn  buildRpm
nexusConf.repository "<Nexus RPM Repository ID>"
nexusConf.file "${buildDir}/distributions/${project.name}-${project.version}-${rpmRelease}.${rpmArch.toLowerCase()}.rpm"
nexusConf.user "<Nexus User"
nexusConf.password "Nexus User Password"
nexusConf.artifact project.name
nexusConf.version project.version
nexusConf.extension "rpm"
nexusConf.packaging "rpm"
nexusConf.classifier rpmRelease
nexusConf.hasPom false
nexusConf.group "Maven group is required options by Nexus"
nexusConf.url "http://<Nexus hostname>:<Nexus port 8081>/nexus/service/local/artifact/maven/content"
}
```