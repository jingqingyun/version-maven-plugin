package com.jingqingyun.maven.nexus;

import lombok.Data;

/**
 * Assert
 *
 * @author jingqingyun
 * @date 2020/12/3
 */
@Data
public class Assert {

    private String id;

    private String downloadUrl;

    private String path;

    private String repository;

    private String format;

    private Checksum checksum;

}
