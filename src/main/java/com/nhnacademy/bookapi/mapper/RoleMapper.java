package com.nhnacademy.bookapi.mapper;

import com.nhnacademy.bookapi.entity.Role;
import java.util.HashMap;
import java.util.Map;

public class RoleMapper {
    private static final Map<String, Role> ROLE_MAP = new HashMap<>();

    static {
        ROLE_MAP.put("지은이", Role.AUTHOR);
        ROLE_MAP.put("그림", Role.ILLUSTRATOR);
        ROLE_MAP.put("엮은이", Role.EDITOR);
        ROLE_MAP.put("원작", Role.ORIGINAL_AUTHOR);
        ROLE_MAP.put("옮긴이", Role.TRANSLATOR);
        ROLE_MAP.put("감수", Role.SUBTRAHEND);
        ROLE_MAP.put("글",Role.WRITER);
        ROLE_MAP.put("사진",Role.PHOTO);
        ROLE_MAP.put("빠숑",Role.PASSON);

    }

    public static Role getRole(String suffix) {
        return ROLE_MAP.getOrDefault(suffix, Role.AUTHOR);
    }
}