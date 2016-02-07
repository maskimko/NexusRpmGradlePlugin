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

import java.io.Console;
import java.net.MalformedURLException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import ua.pp.msk.gradle.exceptions.ArtifactPromotionException;
import ua.pp.msk.gradle.ext.NexusConf;
import ua.pp.msk.gradle.http.Client;

/**
 *
 * @author Maksym Shkolnyi 
 */
public class RpmUpload extends DefaultTask {

    @TaskAction
    public void uploadArtifact() throws TaskExecutionException{
        getLogger()
                .info("Starting app info task");

        try {
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
            getLogger().debug("File " + nc.getFile());
            getLogger().debug("Has POM " + nc.isHasPom());
            if (nc.getUser() == null) {
                System.out.print("Input your username, please. ");
                String user = System.console().readLine();
                nc.setUser(user);
            }

            if (nc.getPassword() == null) {
                System.out.print("Input your password, please. ");
                Console con = System.console();
                char[] passwd = con.readPassword();
                nc.setPassword(new String(passwd));
            }

            Client c = new Client(nc.getUrl(), nc.getUser(), nc.getPassword());
            boolean isUploaded = c.upload(nc);
            if (isUploaded) {
                System.out.println("Artifact has been successfully uploaded");
            } else {
                System.err.println("Artifact has been not uploaded due to errors");
            }
        } catch (ArtifactPromotionException ex) {
            getLogger().error("Cannot promote artifact", ex);
            throw new TaskExecutionException(this, ex);
        } catch (MalformedURLException ex) {
             throw new TaskExecutionException(this, ex);
        }
    }
}
