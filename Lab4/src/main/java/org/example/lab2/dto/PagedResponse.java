package org.example.lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Сторінкова відповідь зі списком елементів та метаданими пагінації")
public class PagedResponse<T> {

    @Schema(description = "Елементи поточної сторінки")
    private List<T> content;

    @Schema(description = "Номер поточної сторінки (починаючи з 0)", example = "0")
    private int page;

    @Schema(description = "Розмір сторінки", example = "10")
    private int size;

    @Schema(description = "Загальна кількість знайдених елементів", example = "25")
    private long totalElements;

    @Schema(description = "Загальна кількість сторінок", example = "3")
    private int totalPages;

    @Schema(description = "Ознака останньої сторінки", example = "false")
    private boolean last;

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<T> getContent() {
        return content;
    }
    public int getPage() {
        return page;
    }
    public int getSize() {
        return size;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public boolean isLast() {
        return last;
    }
}