package org.example.lab2.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.example.lab2.Entity.Candidate;
import org.example.lab2.Entity.Poll;
import org.example.lab2.Entity.VoteToken;
import org.example.lab2.Repository.PollRepositoryStub;
import org.example.lab2.dto.PagedResponse;
import org.example.lab2.dto.PollRequest;
import org.example.lab2.dto.VoteRequest;
import org.example.lab2.exception.ConflictException;
import org.example.lab2.exception.InvalidPatchException;
import org.example.lab2.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VotingService {

    private final PollRepositoryStub repository;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public VotingService(PollRepositoryStub repository, ObjectMapper objectMapper, Validator validator) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public List<Poll> getAllPolls() {
        return repository.findAll();
    }

    public Poll getPollById(String id) {
        return getPollOrThrow(id);
    }

    public VoteToken generateVoteToken() {
        VoteToken token = new VoteToken(UUID.randomUUID().toString(), false);
        repository.saveToken(token);
        return token;
    }

    public Poll createPoll(String title, String owner, List<String> candidateNames) {
        List<Candidate> candidates = candidateNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> new Candidate(null, name.trim(), 0))
                .collect(Collectors.toList());
        Poll poll = new Poll(null, title, null, owner, true, candidates);
        return repository.save(poll);
    }

    public void vote(String pollId, String candidateId, String username) {
        Poll poll = getPollOrThrow(pollId);
        if (!poll.isActive()) {
            throw new IllegalStateException("Голосування закрите");
        }
        String tokenKey = pollId + ":" + username;
        if (repository.findToken(tokenKey).isPresent()) {
            throw new IllegalStateException("Користувач уже проголосував");
        }
        Candidate candidate = poll.getCandidates().stream()
                .filter(c -> Objects.equals(c.getId(), candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Кандидата не знайдено"));
        candidate.setVotes(candidate.getVotes() + 1);
        repository.saveToken(new VoteToken(tokenKey, System.currentTimeMillis()));
        repository.save(poll);
    }

    public void togglePollStatus(String id, String owner) {
        Poll poll = getPollOrThrow(id);
        if (poll.getOwner() != null && !poll.getOwner().equals(owner)) {
            throw new IllegalArgumentException("Тільки власник може змінювати статус голосування");
        }
        poll.setActive(!poll.isActive());
        repository.save(poll);
    }

    public PagedResponse<Poll> getPolls(String title, Boolean active, Integer minCandidates,
                                        int page, int size, String sortBy, String direction) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new IllegalArgumentException("Некоректні параметри пагінації: page >= 0, 1 <= size <= 100");
        }

        Comparator<Poll> comparator = "title".equalsIgnoreCase(sortBy)
                ? Comparator.comparing(Poll::getTitle, String.CASE_INSENSITIVE_ORDER)
                : Comparator.comparing(Poll::getId);

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        List<Poll> filtered = repository.findAll().stream()
                .filter(p -> title == null || title.isBlank() ||
                        p.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(p -> active == null || p.isActive() == active)
                .filter(p -> minCandidates == null ||
                        (p.getCandidates() != null && p.getCandidates().size() >= minCandidates))
                .sorted(comparator)
                .toList();

        long totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());

        List<Poll> pageContent = filtered.subList(fromIndex, toIndex);
        boolean isLast = page >= totalPages - 1 || totalPages == 0;

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, isLast);
    }

    public Poll getPollOrThrow(String id) {
        return repository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Опитування з ID '" + id + "' не знайдено"));
    }

    public Poll createPoll(PollRequest request) {
        boolean exists = repository.findAll().stream()
                .anyMatch(p -> p.getTitle().equalsIgnoreCase(request.getTitle().trim()));
        if (exists) {
            throw new ConflictException("Опитування з назвою '" + request.getTitle() + "' вже існує");
        }

        List<Candidate> candidates = request.getCandidates().stream()
                .map(c -> new Candidate(null, c.getName().trim(), 0))
                .collect(Collectors.toList());

        Poll poll = new Poll(
                null,
                request.getTitle().trim(),
                request.getDescription(),
                request.getOwner() != null ? request.getOwner() : "admin",
                request.getActive() != null ? request.getActive() : true,
                candidates
        );
        return repository.save(poll);
    }

    public Poll updatePoll(String id, PollRequest request) {
        Poll existing = getPollOrThrow(id);

        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription());
        if (request.getOwner() != null) {
            existing.setOwner(request.getOwner());
        }
        if (request.getActive() != null) {
            existing.setActive(request.getActive());
        }

        List<Candidate> updatedCandidates = request.getCandidates().stream()
                .map(c -> new Candidate(null, c.getName().trim(), 0))
                .collect(Collectors.toList());
        existing.setCandidates(updatedCandidates);

        return repository.save(existing);
    }

    public Poll applyJsonPatch(String id, String patchJson) {
        Poll existing = getPollOrThrow(id);
        try {
            JsonNode patchNode = objectMapper.readTree(patchJson);
            JsonPatch patch = JsonPatch.fromJson(patchNode);
            JsonNode targetNode = objectMapper.valueToTree(existing);
            JsonNode patchedNode = patch.apply(targetNode);
            Poll patchedPoll = objectMapper.treeToValue(patchedNode, Poll.class);
            patchedPoll.setId(id);
            validatePatchedPoll(patchedPoll);
            return repository.save(patchedPoll);
        } catch (JsonPatchException e) {
            throw new InvalidPatchException("Неможливо застосувати JSON Patch (RFC 6902): " + e.getMessage());
        } catch (IOException e) {
            throw new IllegalArgumentException("Некоректний формат JSON Patch: " + e.getMessage());
        }
    }

    public Poll applyMergePatch(String id, String mergePatchJson) {
        Poll existing = getPollOrThrow(id);
        try {
            JsonNode mergePatchNode = objectMapper.readTree(mergePatchJson);
            JsonMergePatch mergePatch = JsonMergePatch.fromJson(mergePatchNode);
            JsonNode targetNode = objectMapper.valueToTree(existing);
            JsonNode patchedNode = mergePatch.apply(targetNode);
            Poll patchedPoll = objectMapper.treeToValue(patchedNode, Poll.class);
            patchedPoll.setId(id);
            validatePatchedPoll(patchedPoll);
            return repository.save(patchedPoll);
        } catch (JsonPatchException e) {
            throw new InvalidPatchException("Неможливо застосувати JSON Merge Patch (RFC 7386): " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Некоректний формат JSON Merge Patch: " + e.getMessage());
        }
    }

    private void validatePatchedPoll(Poll poll) {
        Set<ConstraintViolation<Poll>> violations = validator.validate(poll);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Результат часткового оновлення не пройшов валідацію: " + errors);
        }
    }

    public void deletePoll(String id) {
        if (!repository.deleteById(Long.valueOf(id))) {
            throw new ResourceNotFoundException("Опитування з ID '" + id + "' не знайдено");
        }
    }

    public Poll castVote(String pollId, VoteRequest voteRequest) {
        Poll poll = getPollOrThrow(pollId);
        if (!poll.isActive()) {
            throw new ConflictException("Опитування закрите для голосування");
        }

        VoteToken token = repository.findToken(voteRequest.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Токен голосування не знайдено"));

        if (token.isUsed()) {
            throw new ConflictException("Цей токен уже був використаний для голосування");
        }

        Candidate candidate = poll.getCandidates().stream()
                .filter(c -> Objects.equals(c.getId(), voteRequest.getCandidateId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Кандидата з ID '" + voteRequest.getCandidateId() + "' не знайдено в опитуванні " + pollId));

        candidate.setVotes(candidate.getVotes() + 1);
        token.setUsed(true);
        return repository.save(poll);
    }
}