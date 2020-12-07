package com.jingqingyun.maven;

import kong.unirest.GenericType;
import kong.unirest.Unirest;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import com.jingqingyun.maven.nexus.SearchResponse;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * VersionMojo
 *
 * @author jingqingyun
 * @date 2020/12/3
 */
public abstract class VersionMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "settings", readonly = true, required = true)
    protected Settings settings;

    @Parameter(property = "groupId", defaultValue = "${project.groupId}")
    private String groupId;

    @Parameter(property = "artifactId", defaultValue = "${project.artifactId}")
    private String artifactId;

    protected abstract DeploymentRepository getRepository() throws MojoFailureException;

    protected SearchResponse searchComponents() throws MojoExecutionException, MojoFailureException {
        String repositoryId = this.getRepository().getId();
        Server server = settings.getServer(repositoryId);
        if (server == null) {
            throw new MojoFailureException("No such Settings Server: " + repositoryId);
        }
        String username = server.getUsername(), password = server.getPassword();
        return this.requestRestApi(username, password, getHost(), repositoryId, this.groupId, this.artifactId);
    }

    private SearchResponse requestRestApi(String username, String password, String host, String repository,
            String groupId, String artifactId) {
        SearchResponse searchResponse = Unirest.get("http://" + host + "/service/rest/v1/search")
                .basicAuth(username, password)
                .queryString("repository", repository)
                .queryString("group", groupId)
                .queryString("name", artifactId)
                .asObject(new GenericType<SearchResponse>() {
                })
                .getBody();
        getLog().debug("Search response: " + searchResponse);
        return searchResponse;
    }

    private String getHost() throws MojoFailureException {
        String repositoryUrl = this.getRepository().getUrl();
        getLog().info("Maven repository url: " + repositoryUrl);
        try {
            return new URL(repositoryUrl).getHost();
        } catch (MalformedURLException e) {
            throw new MojoFailureException("Illegal respository url", e);
        }
    }

}
