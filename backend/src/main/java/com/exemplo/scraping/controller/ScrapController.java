package com.exemplo.scraping.controller;

import com.exemplo.scraping.service.ScrapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

    @GetMapping("/api/scrap")
    public String rodarScraping() {
        return scrapService.executarScraping();
    }

    @GetMapping("/api/teste")
    public String teste() {
        return "API funcionando!";
    }
}
