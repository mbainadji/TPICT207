package com.tpict207.web.dto;

public final class CoursDtos {
    private CoursDtos() {}

    public record CoursDto(Long id, String nom, String code) {}

    public record CreateCoursRequest(String nom, String code) {}
}

