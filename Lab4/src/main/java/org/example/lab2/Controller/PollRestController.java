package org.example.lab2.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.lab2.Entity.Poll;
import org.example.lab2.Service.VotingService;
import org.example.lab2.dto.ErrorResponse;
import org.example.lab2.dto.PagedResponse;
import org.example.lab2.dto.PollRequest;
import org.example.lab2.dto.VoteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/polls")
@Tag(name = "Polls API", description = "RESTful API для управління опитуваннями, кандидатами та процесом голосування")
public class PollRestController {

    private final VotingService votingService;

    public PollRestController(VotingService votingService) {
        this.votingService = votingService;
    }

    @Operation(
            summary = "Отримати список опитувань (із фільтрацією та пагінацією)",
            description = "Повертає сторінковий список опитувань із підтримкою фільтрації, пагінації та сортування."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список опитувань успішно отримано"),
            @ApiResponse(responseCode = "400", description = "Некоректні параметри фільтрації або пагінації",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<Poll>> getAllPolls(
            @Parameter(description = "Фільтр за частиною назви опитування", example = "старости")
            @RequestParam(required = false) String title,
            @Parameter(description = "Фільтр за статусом активності", example = "true")
            @RequestParam(required = false) Boolean active,
            @Parameter(description = "Мінімальна кількість кандидатів", example = "2")
            @RequestParam(required = false) Integer minCandidates,
            @Parameter(description = "Номер сторінки (з 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки (1..100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле сортування (id або title)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Напрямок сортування (asc або desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                votingService.getPolls(title, active, minCandidates, page, size, sortBy, direction)
        );
    }

    @Operation(
            summary = "Отримати опитування за ідентифікатором",
            description = "Повертає детальну інформацію про конкретне опитування за його ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опитування знайдено"),
            @ApiResponse(responseCode = "404", description = "Опитування із вказаним ID не існує",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPollById(
            @Parameter(description = "Унікальний ідентифікатор опитування", example = "1")
            @PathVariable String id
    ) {
        return ResponseEntity.ok(votingService.getPollOrThrow(id));
    }

    @Operation(
            summary = "Створити нове опитування",
            description = "Створює нове опитування з переліком кандидатів і повертає статус 201 Created."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Опитування успішно створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації вхідних даних",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Опитування з такою назвою вже існує",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Poll> createPoll(@Valid @RequestBody PollRequest request) {
        Poll created = votingService.createPoll(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Повністю оновити опитування (PUT)",
            description = "Повністю замінює дані існуючого опитування за його ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опитування успішно оновлено"),
            @ApiResponse(responseCode = "400", description = "Невалідні дані в тілі запиту",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Опитування з вказаним ID не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(
            @Parameter(description = "Ідентифікатор опитування", example = "1")
            @PathVariable String id,
            @Valid @RequestBody PollRequest request
    ) {
        return ResponseEntity.ok(votingService.updatePoll(id, request));
    }

    @Operation(
            summary = "Часткове оновлення опитування — RFC 6902 (JSON Patch)",
            description = "Застосовує масив інструкцій JSON Patch (Content-Type: application/json-patch+json)."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Масив операцій RFC 6902 JSON Patch",
            required = true,
            content = @Content(
                    mediaType = "application/json-patch+json",
                    examples = @ExampleObject(
                            name = "Зміна назви та статусу",
                            value = "[{\"op\": \"replace\", \"path\": \"/title\", \"value\": \"Оновлена назва опитування\"}, {\"op\": \"replace\", \"path\": \"/active\", \"value\": false}]"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Часткове оновлення RFC 6902 успішно виконано"),
            @ApiResponse(responseCode = "400", description = "Некоректний JSON або порушення правил валідації",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Опитування не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "415", description = "Непідтримуваний Content-Type",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Операцію JSON Patch неможливо застосувати",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Poll> patchPollJsonPatch(
            @Parameter(description = "Ідентифікатор опитування", example = "1")
            @PathVariable String id,
            @RequestBody String patchOperations
    ) {
        return ResponseEntity.ok(votingService.applyJsonPatch(id, patchOperations));
    }

    @Operation(
            summary = "Часткове оновлення опитування — RFC 7386 (JSON Merge Patch)",
            description = "Оновлює лише передані поля ресурсу згідно зі стандартом RFC 7386 (Content-Type: application/merge-patch+json)."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Фрагмент JSON-об'єкта для злиття згідно з RFC 7386",
            required = true,
            content = @Content(
                    mediaType = "application/merge-patch+json",
                    examples = @ExampleObject(
                            name = "Закриття опитування та очищення опису",
                            value = "{\"active\": false, \"description\": null}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Часткове оновлення RFC 7386 успішно виконано"),
            @ApiResponse(responseCode = "400", description = "Некоректний JSON або порушення обмежень полів",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Опитування не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "415", description = "Непідтримуваний Content-Type",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Помилка застосування Merge Patch",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Poll> patchPollMergePatch(
            @Parameter(description = "Ідентифікатор опитування", example = "1")
            @PathVariable String id,
            @RequestBody String mergePatchDocument
    ) {
        return ResponseEntity.ok(votingService.applyMergePatch(id, mergePatchDocument));
    }

    @Operation(
            summary = "Видалити опитування",
            description = "Видаляє опитування за його ідентифікатором і повертає 204 No Content."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Опитування успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Опитування з вказаним ID не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoll(
            @Parameter(description = "Ідентифікатор опитування", example = "1")
            @PathVariable String id
    ) {
        votingService.deletePoll(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Проголосувати в опитуванні",
            description = "Реєструє голос за обраного кандидата за допомогою одноразового токена виборця."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Голос успішно зараховано"),
            @ApiResponse(responseCode = "400", description = "Некоректні дані запиту голосування",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Опитування, кандидата або токен не знайдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Опитування закрите або токен уже використано",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/votes")
    public ResponseEntity<Poll> vote(
            @Parameter(description = "Ідентифікатор опитування", example = "1")
            @PathVariable String id,
            @Valid @RequestBody VoteRequest voteRequest
    ) {
        return ResponseEntity.ok(votingService.castVote(id, voteRequest));
    }
}