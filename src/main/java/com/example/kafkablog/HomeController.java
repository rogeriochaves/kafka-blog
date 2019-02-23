package com.example.kafkablog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @GetMapping({"/"})
    public String home(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        model.addAttribute("post", new Post());

        List<Post> posts = PostsStream.getInstance().findAll();
        model.addAttribute("posts", posts);

        return "home";
    }

    @GetMapping({"/exciting"})
    public String exciting(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        model.addAttribute("post", new Post());

        List<Post> posts = PostsStream.getInstance().findAllExciting();
        model.addAttribute("posts", posts);

        return "home";
    }

    @GetMapping({"/posts/{id}/edit"})
    public String edit(Model model, @PathVariable String id) {
        Post post = PostsStream.getInstance().find(id);
        model.addAttribute("name", post.getTitle());
        model.addAttribute("post", post);

        List<Post> posts = PostsStream.getInstance().findAll();
        model.addAttribute("posts", posts);

        return "home";
    }

    @PostMapping({"/posts/save"})
    public String save(@ModelAttribute Post post) {
        if (post.getId().isEmpty()) {
            UUID uuid = UUID.randomUUID();
            post.setId(uuid.toString());
        }
        PostsStream.getInstance().produce(post);

        return "redirect:/";
    }
}
