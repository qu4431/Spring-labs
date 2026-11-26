package org.example.lab2.Repository;

import org.example.lab2.Entity.Candidate;
import org.example.lab2.Entity.Poll;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PollRepositoryStub {

    private final Map<String, Poll> polls = new ConcurrentHashMap<>();
    private final Set<String> userVotes = ConcurrentHashMap.newKeySet(); // for unique votes

    public PollRepositoryStub() {
        // example data
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("c1", "Понеділок", 0));
        candidates.add(new Candidate("c2", "Вівторок", 0));
        candidates.add(new Candidate("c3", "Середа", 0));
        candidates.add(new Candidate("c4", "Четвер", 0));
        candidates.add(new Candidate("c5", "П'ятниця", 0));
        candidates.add(new Candidate("c6", "Субота", 0));
        candidates.add(new Candidate("c7", "Неділя", 0));

        Poll defaultPoll = new Poll("poll-1", "Сьогоднішній день", "example owner name", true, candidates);
        polls.put(defaultPoll.getId(), defaultPoll);
    }

    public List<Poll> findAll() {
        return new ArrayList<>(polls.values());
    }

    public Optional<Poll> findById(String id) {
        return Optional.ofNullable(polls.get(id));
    }

    public void save(Poll poll) {
        polls.put(poll.getId(), poll);
    }

    public boolean hasUserVoted(String pollId, String username) {
        return userVotes.contains(pollId + "_" + username);
    }

    public void recordVote(String pollId, String username) {
        userVotes.add(pollId + "_" + username);
    }
}