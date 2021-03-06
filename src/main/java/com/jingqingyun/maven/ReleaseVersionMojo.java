package com.jingqingyun.maven;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.Mojo;

import javax.annotation.concurrent.ThreadSafe;

/**
 * ReleaseVersionMojo
 *
 * @author jingqingyun
 * @date 2020/12/3
 */
@ThreadSafe
@Mojo(name = "release", threadSafe = true, instantiationStrategy = InstantiationStrategy.SINGLETON)
public class ReleaseVersionMojo extends VersionMojo {

    @Override
    protected DeploymentRepository getRepository() throws MojoFailureException {
        DistributionManagement distributionManagement = this.project.getDistributionManagement();
        if (distributionManagement == null) {
            throw new MojoFailureException("POM not configure DistributionManagement");
        }
        return distributionManagement.getRepository();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String version = this.searchComponents().getLatestReleaseVersion();
        getLog().info("Latest release verion: " + version);
    }

}
