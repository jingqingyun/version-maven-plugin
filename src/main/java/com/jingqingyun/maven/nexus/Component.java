package com.jingqingyun.maven.nexus;

import lombok.Data;

import java.util.List;

/**
 * ComponentItem
 *
 * @author jingqingyun
 * @date 2020/12/3
 */
@Data
public class Component {

    private String id;

    private String name;

    private String repository;

    private String format;

    private String group;

    private String version;

    private List<Assert> assets;

    public Version getVersionObj() {
        return new Version(version);
    }

}
