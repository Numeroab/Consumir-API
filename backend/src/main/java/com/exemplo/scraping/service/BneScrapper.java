package com.exemplo.scraping.service;

import com.exemplo.scraping.model.Vaga;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BneScrapper {
    
    private static final String FONTE = "BNE";

    public static List<Vaga> buscar(WebDriver driver, String termo) {
        List<Vaga> resultados = new ArrayList<>();

        try {
            String termoFormatado = termo.replace(" ", "-").toLowerCase();
            String url = String.format(
                "https://www.bne.com.br/vagas-de-emprego-para-%s-em-cascavel-pr/?Page=1&CityName=cascavel-pr&Function=%s",
                termoFormatado, termoFormatado);
            
            System.out.println("Acessando BNE: " + url);
            driver.get(url);
            Thread.sleep(5000);
            
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements containersVaga = doc.select("section.job__card__container");
            
            System.out.println("Encontrados " + containersVaga.size() + " vagas");
            
            // Processa apenas as primeiras 3 vagas
            for (int i = 0; i < Math.min(containersVaga.size(), 3); i++) {
                try {
                    Element container = containersVaga.get(i);
                    
                    String titulo = extrairTitulo(container);
                    String empresa = extrairEmpresa(container);
                    Float salario = null; // Sem clique, sem salário
                    
                    if (!titulo.isEmpty()) {
                        resultados.add(new Vaga(titulo, empresa, salario, FONTE));
                        System.out.println(titulo + " | " + empresa);
                    }
                } catch (Exception e) {
                    System.err.println("Erro na vaga " + i + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro no BNE: " + e.getMessage());
        }
        
        return resultados;
    }

    private static String extrairTitulo(Element container) {
        Element tituloElement = container.selectFirst("div.card--list--vagas h2 strong");
        if (tituloElement != null) {
            String titulo = tituloElement.text().trim();
            titulo = titulo.replace("Vaga De", "").replace("Vaga de", "").trim();
            return titulo.isEmpty() ? "Vaga sem título" : titulo;
        }
        return "Vaga sem título";
    }

    private static String extrairEmpresa(Element container) {
        Elements h3Elements = container.select("div.info__vaga h3");
        if (h3Elements.size() >= 2) {
            String empresa = h3Elements.get(1).text().trim();
            empresa = empresa.replace("RHF Talentos", "").trim();
            return empresa.isEmpty() ? "Empresa não informada" : empresa;
        }
        return "Empresa não informada";
    }
}// package com.exemplo.scraping.service;

// import com.exemplo.scraping.model.Vaga;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.jsoup.select.Elements;
// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import java.time.Duration;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class BneScrapper {
    
//     private static final String FONTE = "BNE";
//     private static final int TIMEOUT_SECONDS = 45;

//     public static List<Vaga> buscar(WebDriver driver, String termo) {
//         List<Vaga> resultados = new ArrayList<>();
//         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));

//         try {
//             String termoFormatado = termo.replace(" ", "-").toLowerCase();
//             String url = String.format(
//                 "https://www.bne.com.br/vagas-de-emprego-para-%s-em-cascavel-pr/?Page=1&CityName=cascavel-pr&Function=%s",
//                 termoFormatado, termoFormatado);

//             System.out.println("Acessando BNE: " + url);
//             driver.get(url);
            
//             // Aguarda carregamento inicial
//             wait.until(ExpectedConditions.presenceOfElementLocated(
//                 By.cssSelector("section.job__card__container")));

//             Document doc = Jsoup.parse(driver.getPageSource());
//             Elements containersVaga = doc.select("section.job__card__container");
            
//             System.out.println("✅ Encontrados " + containersVaga.size() + " containers de vaga");

//             for (int i = 0; i < containersVaga.size() && i < 10; i++) { // Limite de 10 vagas
//                 try {
//                     WebElement vagaElement = wait.until(ExpectedConditions.elementToBeClickable(
//                         By.cssSelector("section.job__card__container:nth-of-type(" + (i + 1) + ")")));
                    
//                     // Clica na vaga usando JavaScript
//                     ((org.openqa.selenium.JavascriptExecutor) driver)
//                         .executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", vagaElement);
                    
//                     Thread.sleep(2000); // Aguarda expansão
                    
//                     Document docExpandido = Jsoup.parse(driver.getPageSource());
                    
//                     String titulo = extrairTituloExato(containersVaga.get(i));
//                     String empresa = extrairEmpresaExata(containersVaga.get(i));
//                     Float salario = extrairSalarioExpandido(docExpandido);
                    
//                     if (!titulo.isEmpty()) {
//                         resultados.add(new Vaga(titulo, empresa, salario, FONTE));
                        
//                         System.out.println("📌 " + titulo + " | " + empresa + 
//                                          " | Salário: " + (salario != null ? String.format("R$ %.2f", salario) : "A combinar"));
//                     }
                    
//                     // Fecha modal clicando fora
//                     WebElement body = driver.findElement(By.tagName("body"));
//                     body.click();
//                     Thread.sleep(1000);
                    
//                 } catch (Exception e) {
//                     System.err.println("❌ Erro na vaga " + (i + 1) + ": " + e.getMessage());
//                     continue;
//                 }
//             }

//         } catch (Exception e) {
//             System.err.println("❌ Erro geral no BNE: " + e.getMessage());
//         }
        
//         return resultados;
//     }

//     // Métodos auxiliares permanecem IGUAIS (extrairTituloExato, extrairEmpresaExata, etc.)
//     private static String extrairTituloExato(Element container) {
//         Element tituloElement = container.selectFirst("div.card--list--vagas h2 strong");
//         if (tituloElement != null) {
//             String titulo = tituloElement.text().trim();
//             titulo = titulo.replace("Vaga De", "").replace("Vaga de", "").trim();
//             return titulo.isEmpty() ? "Vaga sem título" : titulo;
//         }
//         return "Vaga sem título";
//     }

//     private static String extrairEmpresaExata(Element container) {
//         Elements h3Elements = container.select("div.info__vaga h3");
//         if (h3Elements.size() >= 2) {
//             String empresa = h3Elements.get(1).text().trim();
//             empresa = empresa.replace("RHF Talentos", "").trim();
//             return empresa.isEmpty() ? "Empresa não informada" : empresa;
//         }
//         return "Empresa não informada";
//     }

//     private static Float extrairSalarioExpandido(Document docExpandido) {
//         try {
//             String[] seletores = {
//                 ".core__vip__text-grey",
//                 ".job__open__infos-item",
//                 "[class*='salario']",
//                 "[class*='salary']",
//                 "[class*='money']",
//                 "[class*='valor']",
//                 ".vaga-detalhes",
//                 ".job-details"
//             };

//             for (String seletor : seletores) {
//                 Elements elementos = docExpandido.select(seletor);
//                 for (Element el : elementos) {
//                     String texto = el.text();
//                     if (texto.contains("R$")) {
//                         Float salario = extrairValorNumerico(texto);
//                         if (salario != null) return salario;
//                     }
//                 }
//             }

//             String textoCompleto = docExpandido.text();
//             if (textoCompleto.contains("R$")) {
//                 return extrairValorNumerico(textoCompleto);
//             }

//         } catch (Exception e) {
//             System.err.println("Erro ao extrair salário: " + e.getMessage());
//         }
//         return null;
//     }

//     private static Float extrairValorNumerico(String texto) {
//         try {
//             Pattern padrao = Pattern.compile("R\\$\\s*([0-9]{1,3}(?:\\.[0-9]{3})*(?:,[0-9]{2})?)");
//             Matcher matcher = padrao.matcher(texto);
            
//             if (matcher.find()) {
//                 String valor = matcher.group(1).replace(".", "").replace(",", ".");
//                 return Float.parseFloat(valor);
//             }
            
//         } catch (Exception e) {
//             // Ignora erro de conversão
//         }
//         return null;
//     }
// }