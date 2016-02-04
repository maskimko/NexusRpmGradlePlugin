/*
 * Copyright 2016 Maksym Shkolnyi self project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.pp.msk.gradle.rpm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import ua.pp.msk.gradle.ext.NexusConf;


/**
 *
 * @author Maksym Shkolnyi aka Maksym Shkolnyi <mshkolnyi@ukr.net> aka maskimko
 */
public class RpmUpload extends DefaultTask {

    @TaskAction
    public void uploadArtifact() {
        getLogger()
                .info("Starting app info task");

        //  try {
        NexusConf nc = getProject().getExtensions().findByType(NexusConf.class);
        if (nc == null) {
            nc = new NexusConf();
        }

        getLogger().debug("Nexus URL " + nc.getUrl());
        getLogger().debug("Rpository " + nc.getRepository());
        getLogger().debug("Nexus user " + nc.getUser());
        getLogger().debug("Group " + nc.getGroup());
        getLogger().debug("Artifact " + nc.getArtifact());
        getLogger().debug("Version " + nc.getVersion());
        getLogger().debug("Extension " + nc.getExtension());
        getLogger().debug("Packaging " + nc.getPackaging());
        getLogger().debug("Has POM " + nc.isHasPom());
//        if (aix.getOutput() == null || aix.getOutput().length() == 0) {
//            output = System.out;
//            getLogger().debug("CliQr Info output to stdout");
//        } else {
//            File of = new File(aix.getOutput());
//            of.getParentFile().mkdirs();
//            output = new PrintStream(new FileOutputStream(of));
//            getLogger().debug("CliQr Info output to file " + aix.getOutput());
//        }
//
//        String response = null;
//        AppInfo ai = new AppInfoImpl(cx.getHost(), cx.getUser(), cx.getApiKey());
//        if (aix.getAppId() >= 0) {
//            response = ai.getAppInfo(aix.getAppId());
//        } else {
//            response = ai.getAppInfo();
//        }
//        output.print(response);
//    }
//    catch (FileNotFoundException ex
//
//    
//        ) {
//           getLogger().error("Cannot find file", ex);
//    }
//    catch (MalformedURLException | ClientSslException | ResponseException ex
//
//    
//        ) {
//            getLogger().error("Cannot get response", ex);
//    }
//
//    
//        finally {
//            if (output != null) {
//            output.close();
//        }
//    }
    }
}
