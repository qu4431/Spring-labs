package org.example.lab2.Service;


import org.example.lab2.Entity.Candidate;
import org.example.lab2.Entity.Poll;
import org.example.lab2.Repository.PollRepositoryStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VotingService {

    private final PollRepositoryStub pollRepository;

    @Autowired
    public VotingService(PollRepositoryStub pollRepository) {
        this.pollRepository = pollRepository;
    }

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    public Optional<Poll> getPollById(String id) {
        return pollRepository.findById(id);
    }

    public Poll createPoll(String title, String owner, List<String> candidateNames) {
        String pollId = UUID.randomUUID().toString().substring(0, 6);
        List<Candidate> candidates = new java.util.ArrayList<>();
        for (int i = 0; i < candidateNames.size(); i++) {
            candidates.add(new Candidate("c_" + i, candidateNames.get(i), 0));
        }
        Poll poll = new Poll(pollId, title, owner, true, candidates);
        pollRepository.save(poll);
        return poll;
    }

    public boolean vote(String pollId, String candidateId, String username) {
        Poll poll = pollRepository.findById(pollId).orElse(null);
        if (poll == null || !poll.isActive()) {
            return false;
        }

        if (pollRepository.hasUserVoted(pollId, username)) {
            return false;
        }

        for (Candidate c : poll.getCandidates()) {
            if (c.getId().equals(candidateId)) {
                c.setVotesCount(c.getVotesCount() + 1);
                pollRepository.recordVote(pollId, username);
                pollRepository.save(poll);
                return true;
            }
        }
        return false;
    }

    public void togglePollStatus(String pollId, String owner) {
        Poll poll = pollRepository.findById(pollId).orElse(null);
        if (poll != null && poll.getOwner().equals(owner)) {
            poll.setActive(!poll.isActive());
            pollRepository.save(poll);
        }
    }
}