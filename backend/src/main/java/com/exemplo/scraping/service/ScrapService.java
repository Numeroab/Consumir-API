package com.exemplo.scraping.service;

import com.exemplo.scraping.*;
import com.exemplo.scraping.model.Vaga;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ScrapService {

    public Map<String, Object> executarScraping(String termo) {
        Map<String, Object> resultado = new HashMap<>();
        List<Object> vagas = new ArrayList<>();
        
        try {
            // Configura o driver do Chrome
            System.setProperty("webdriver.chrome.driver", "caminho/para/chromedriver.exe");
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--disable-gpu", "--disable-software-rasterizer",
                    "--window-size=1920,1080", "--disable-blink-features=AutomationControlled");
            
            WebDriver driver = new ChromeDriver(options);
            
            try {
                // Executa o scraping do BNE
                List<Vaga> bneVagas = BneScraper.buscar(driver, termo.replace(" ", "+"));
                
                // Converte para formato JSON
                for (Vaga vaga : bneVagas) {
                    Map<String, Object> vagaMap = new HashMap<>();
                    vagaMap.put("titulo", vaga.getTitulo());
                    vagaMap.put("empresa", vaga.getEmpresa());
                    vagaMap.put("salario", vaga.getSalario());
                    vagaMap.put("salarioFormatado", vaga.getSalarioFormatado());
                    vagaMap.put("fonte", "BNE");
                    vagas.add(vagaMap);
                }
                
                resultado.put("sucesso", true);
                resultado.put("totalVagas", vagas.size());
                resultado.put("vagas", vagas);
                resultado.put("termo", termo);
                
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
            
        } catch (Exception e) {
            resultado.put("sucesso", false);
            resultado.put("erro", e.getMessage());
        }
        
        return resultado;
    }
}