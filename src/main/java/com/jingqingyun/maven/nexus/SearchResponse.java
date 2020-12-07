package com.jingqingyun.maven.nexus;

import lombok.Data;

import java.util.List;

/**
 * SearchResponse
 *
 * @author jingqingyun
 * @date 2020/12/3
 */
@Data
public class SearchResponse {

    private List<Component> items;

    private String continuationToken;

    public String getLatestReleaseVersion() {
        return getLatestVersion(false);
    }

    public String getLatestSnapshotVersion() {
        return getLatestVersion(true);
    }

    private String getLatestVersion(boolean wantSnapshot) {
        Version latestVersion = null;
        if (items == null || items.isEmpty()) {
            return null;
        }

        for (Component component : items) {
            Version version = component.getVersionObj();
            boolean isSnapshot = isSnapshotRepository(component.getRepository()) && version.isSnapshotVersion();
            if (!wantSnapshot && isSnapshot || wantSnapshot && !isSnapshot) {
                continue;
            }
            if (version.compareTo(latestVersion) > 0) {
                latestVersion = version;
            }
        }
        return latestVersion != null ? latestVersion.getVersion() : null;
    }

    private boolean isSnapshotRepository(String repository) {
        return repository.toLowerCase().contains("snapshot");
    }

}
