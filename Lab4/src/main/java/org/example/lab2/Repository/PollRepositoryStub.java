package org.example.lab2.Repository;

import org.example.lab2.Entity.Candidate;
import org.example.lab2.Entity.Poll;
import org.example.lab2.Entity.VoteToken;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PollRepositoryStub {

    private final Map<String, Poll> polls = new ConcurrentHashMap<>();
    private final Map<String, VoteToken> tokens = new ConcurrentHashMap<>();
    private final AtomicLong pollIdSequence = new AtomicLong(1);
    private final AtomicLong candidateIdSequence = new AtomicLong(100);

    public void saveToken(VoteToken token) {
        tokens.put(token.getToken(), token);
    }

    public PollRepositoryStub() {
        save(new Poll(null, "Вибір старости групи", "Голосування за старосту на 2026 навчальний рік", true,
                new ArrayList<>(List.of(
                        new Candidate(null, "Олена Коваленко", 5),
                        new Candidate(null, "Максим Бондаренко", 3)
                ))));
        save(new Poll(null, "Мова програмування для курсового проєкту", "Оберіть основний стек", true,
                new ArrayList<>(List.of(
                        new Candidate(null, "Java / Spring Boot", 12),
                        new Candidate(null, "Python / FastAPI", 8),
                        new Candidate(null, "TypeScript / NestJS", 6)
                ))));
        save(new Poll(null, "Дата проведення хакатону", "Архівне опитування", false,
                new ArrayList<>(List.of(
                        new Candidate(null, "Субота", 15),
                        new Candidate(null, "Неділя", 9)
                ))));

        tokens.put("TOKEN-2026-001", new VoteToken("TOKEN-2026-001", false));
        tokens.put("TOKEN-2026-002", new VoteToken("TOKEN-2026-002", false));
        tokens.put("TOKEN-USED", new VoteToken("TOKEN-USED", true));
    }

    public List<Poll> findAll() {
        return new ArrayList<>(polls.values());
    }

    public Optional<Poll> findById(Long id) {
        return Optional.ofNullable(polls.get(String.valueOf(id)));
    }

    public Poll save(Poll poll) {
        if (poll.getId() == null) {
            poll.setId(String.valueOf(pollIdSequence.getAndIncrement()));
        }
        if (poll.getCandidates() != null) {
            for (Candidate c : poll.getCandidates()) {
                if (c.getId() == null) {
                    c.setId(String.valueOf(candidateIdSequence.getAndIncrement()));
                }
            }
        }
        polls.put(poll.getId(), poll);
        return poll;
    }

    public boolean deleteById(Long id) {
        return polls.remove(String.valueOf(id)) != null;
    }

    public Optional<VoteToken> findToken(String tokenValue) {
        return Optional.ofNullable(tokens.get(tokenValue));
    }
}