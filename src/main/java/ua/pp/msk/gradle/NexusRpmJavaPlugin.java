/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.pp.msk.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import ua.pp.msk.gradle.rpm.RpmUpload;

/**
 *
 * @author Maksym Shkolnyi aka maskimko
 */
public class NexusRpmJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getTasks().create("uploadRpm", RpmUpload.class);
        target.getExtensions().create("nexusConf", NexusConf.class);
    }

}
