package com.exemplo.scraping.service;

import com.exemplo.scraping.model.Vaga;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import java.util.*;

@Service
public class ScrapService {

    private final ObjectFactory<WebDriver> webDriverFactory;

    public ScrapService(ObjectFactory<WebDriver> webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public Map<String, Object> executarScraping(String termo) {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> vagas = new ArrayList<>();
        WebDriver driver = null;

        try {
            driver = webDriverFactory.getObject();
            
            // ✅ CORREÇÃO: Chamada estática (sem 'new')
            List<Vaga> bneVagas = BneScrapper.buscar(driver, termo);

            // Converte para formato de resposta
            for (Vaga vaga : bneVagas) {
                Map<String, Object> vagaMap = new HashMap<>();
                vagaMap.put("titulo", vaga.getTitulo());
                vagaMap.put("empresa", vaga.getEmpresa());
                vagaMap.put("salario", vaga.getSalario());
                vagaMap.put("salarioFormatado", vaga.getSalarioFormatado());
                vagaMap.put("fonte", vaga.getFonte());
                vagas.add(vagaMap);
            }

            resultado.put("sucesso", true);
            resultado.put("totalVagas", vagas.size());
            resultado.put("vagas", vagas);
            resultado.put("termo", termo);
            resultado.put("timestamp", new Date());

        } catch (TimeoutException e) {
            resultado.put("sucesso", false);
            resultado.put("erro", "Timeout ao acessar o site. Tente novamente.");
            resultado.put("detalhes", e.getMessage());
        } catch (WebDriverException e) {
            resultado.put("sucesso", false);
            resultado.put("erro", "Erro no WebDriver. Verifique se o Chrome está instalado.");
            resultado.put("detalhes", e.getMessage());
        } catch (Exception e) {
            resultado.put("sucesso", false);
            resultado.put("erro", "Erro inesperado durante o scraping.");
            resultado.put("detalhes", e.getMessage());
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("Erro ao fechar driver: " + e.getMessage());
                }
            }
        }

        return resultado;
    }
}