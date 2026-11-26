package org.example.lab2.Controller;

import org.example.lab2.Entity.Poll;
import org.example.lab2.Service.VotingService;
import org.example.lab2.dto.PollRequest;
import org.example.lab2.dto.VoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class VotingController {

    private final VotingService votingService;

    @Autowired
    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("polls", votingService.getAllPolls());
        return "index";
    }

    @GetMapping("/poll/{id}")
    public String viewPoll(@PathVariable String id, Model model) {
        Poll poll = votingService.getPollById(id);
        model.addAttribute("poll", poll);
        model.addAttribute("voteToken", votingService.generateVoteToken());
        return "poll";
    }

    @PostMapping("/poll/{id}/vote")
    public String castVote(@PathVariable String id,
                           @RequestParam String candidateId,
                           @RequestParam String username) {
        VoteRequest request = new VoteRequest();
        request.setCandidateId(candidateId);
        request.setToken(username.startsWith("TOKEN-") ? username : "TOKEN-2026-001");
        votingService.castVote(id, request);
        return "redirect:/poll/" + id + "/results";
    }

    @GetMapping("/poll/{id}/results")
    public String viewResults(@PathVariable String id, Model model) {
        Poll poll = votingService.getPollById(id);
        model.addAttribute("poll", poll);
        return "results";
    }

    @GetMapping("/poll/create")
    public String createPollForm() {
        return "create-poll";
    }

    @PostMapping("/poll/create")
    public String createPoll(@RequestParam String title,
                             @RequestParam String owner,
                             @RequestParam String candidatesString) {
        List<String> candidateNames = Arrays.asList(candidatesString.split(","));
        votingService.createPoll(title, owner, candidateNames);
        return "redirect:/";
    }

    @PostMapping("/poll/{id}/toggle")
    public String togglePoll(@PathVariable String id, @RequestParam String owner) {
        votingService.togglePollStatus(id, owner);
        return "redirect:/";
    }
}
