package com.jingqingyun.maven.nexus;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version
 *
 * @author jingqingyun
 * @date 2020/12/7
 */
@EqualsAndHashCode
public class Version implements Comparable<Version> {

    private static final Pattern SNAPHOST_VERSION_MATCH_PATTERN = Pattern.compile("\\S+-(\\d{8}\\.\\d{6})-\\d+");

    private static final String SNAPSHOT_DATETIME_REGX = "\\d{8}\\.\\d{6}-\\d+";

    private static final String SNAPSHOT_DATETIME_FMT = "yyyymmdd.HHmmSS";

    private final String version;

    private final boolean isSnapshotVersion;

    public Version(String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Version string must not be blank");
        }
        this.version = version;
        this.isSnapshotVersion = isSnapshotVersion(version);
    }

    public static boolean isSnapshotVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return false;
        }
        Matcher matcher = SNAPHOST_VERSION_MATCH_PATTERN.matcher(version);
        if (!matcher.find()) {
            return false;
        }
        String dateTime = matcher.group(1);
        if (StringUtils.isBlank(dateTime)) {
            return false;
        }
        try {
            DateUtils.parseDate(dateTime, SNAPSHOT_DATETIME_FMT);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isSnapshotVersion() {
        return isSnapshotVersion;
    }

    /**
     * 获取主版本号，不包含snapshot时间
     *
     * @return 主版本号
     */
    public String getMainVersion() {
        if (!isSnapshotVersion) {
            return version;
        }
        return version.replaceFirst('-' + SNAPSHOT_DATETIME_REGX, "");
    }

    /**
     * 获取版本号，snapshot版本号返回SNAPSHOT格式
     *
     * @return 版本号
     */
    public String getVersion() {
        if (!isSnapshotVersion) {
            return version;
        }
        return version.replaceFirst(SNAPSHOT_DATETIME_REGX, "SNAPSHOT");
    }

    /**
     * 获取原始版本号
     *
     * @return 不作处理的版本号
     */
    public String getOriginalVersion() {
        return version;
    }

    private String[] getSubVersions() {
        return this.getMainVersion().split("\\.");
    }

    private int compareVersion(String[] subVersions1, String[] subVersions2) {
        int minLen = Math.min(subVersions1.length, subVersions2.length);
        for (int i = 0; i < minLen; i++) {
            String v1Sub = subVersions1[i], v2Sub = subVersions2[i];
            if (StringUtils.isNumeric(v1Sub) && StringUtils.isNumeric(v2Sub)) {
                int v1Num = Integer.parseInt(v1Sub), v2Num = Integer.parseInt(v2Sub);
                if (v1Num != v2Num) {
                    return v1Num - v2Num;
                }
            } else {
                int compare;
                if ((compare = v1Sub.compareTo(v2Sub)) != 0) {
                    return compare;
                }
            }
        }
        // 比较完等长的子版本号仍相同，则更长的版本号更新
        return subVersions1.length - subVersions2.length;
    }

    @Override
    public int compareTo(Version o) {
        if (o == null) {
            return 1;
        }
        return compareVersion(this.getSubVersions(), o.getSubVersions());
    }

    @Override
    public String toString() {
        return "Version{" + "version='" + version + '\'' + '}';
    }

}
