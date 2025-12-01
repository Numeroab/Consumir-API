package com.exemplo.scraping.controller;


import com.exemplo.scraping.service.ScrapService;
import com.exemplo.scraping.model.ScrapRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite requisições do frontend
public class ScrapController {


   private final ScrapService scrapService;


   public ScrapController(ScrapService scrapService) {
       this.scrapService = scrapService;
   }


   @PostMapping("/scrap")
   public ResponseEntity<Map<String, Object>> rodarScraping(@RequestBody ScrapRequest request) {
       Map<String, Object> resultado = scrapService.executarScraping(request.getTermo());
       return ResponseEntity.ok(resultado);
   }
  
   @GetMapping("/scrap")
   public ResponseEntity<Map<String, Object>> rodarScrapingGet(@RequestParam String termo) {
       Map<String, Object> resultado = scrapService.executarScraping(termo);
       return ResponseEntity.ok(resultado);
   }


   @GetMapping("/teste")
   public ResponseEntity<Map<String, String>> teste() {
       return ResponseEntity.ok(Map.of(
           "status", "API funcionando!",
           "versao", "1.0",
           "endpoints", "/api/scrap (POST/GET), /api/teste (GET)"
       ));
   }
  
   @GetMapping("/saudacao/{nome}")
   public ResponseEntity<String> saudacao(@PathVariable String nome) {
       return ResponseEntity.ok("Olá, " + nome + "! A API de scraping está funcionando.");
   }
}
