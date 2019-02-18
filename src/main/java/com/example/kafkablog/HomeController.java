package com.example.kafkablog;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @GetMapping({"/"})
    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        model.addAttribute("post", new Post());

        List<Post> posts = PostsListener.findAll();
        model.addAttribute("posts", posts);

        return "home";
    }

    @PostMapping({"/posts/new"})
    public String newPost(Model model, PostsProducer p, @ModelAttribute Post post) {
        model.addAttribute("name", post.getTitle());
        UUID uuid = UUID.randomUUID();
        post.setId(uuid.toString());
        p.produce(post);
        return "home";
    }
}
