package com.example.kafkablog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @GetMapping({"/"})
    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        model.addAttribute("post", new Post());
        return "home";
    }

    @PostMapping({"/posts/new"})
    public String newPost(Model model, PostsProducer p, @ModelAttribute Post post) {
        model.addAttribute("name", post.getTitle());
        p.produce(post);
        return "home";
    }
}
