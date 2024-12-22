package com.nhnacademy.bookapi.controller.bookcreator;

import com.nhnacademy.bookapi.entity.BookCreator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class BookCreatorResponseDTO {


    private List<String> creators;

    public BookCreatorResponseDTO(List<BookCreator> creators) {
        this.creators = new ArrayList<>();

        // Role별로 그룹화하고 문자열 생성
        Map<String, List<String>> groupedByRole = creators.stream()
            .collect(Collectors.groupingBy(
                creator -> creator.getRole().name(), // Role 이름으로 그룹화
                Collectors.mapping(BookCreator::getName, Collectors.toList()) // 이름만 수집
            ));

        // 그룹별 문자열 생성
        groupedByRole.forEach((role, names) -> {
            // 각 그룹의 이름을 콤마로 연결
            String joinedNames = String.join(", ", names);
            // 마지막에 Role의 Description 추가
            String roleString = joinedNames + " (" + findRoleDescription(creators, role) + ")";
            this.creators.add(roleString);
        });
    }

    private String findRoleDescription(List<BookCreator> creators, String roleName) {
        // 해당 Role의 Description 반환
        return creators.stream()
            .filter(creator -> creator.getRole().name().equals(roleName))
            .map(creator -> creator.getRole().getDescription())
            .findFirst()
            .orElse("");
    }
}
