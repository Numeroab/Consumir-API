package com.exemplo.scraping;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import com.exemplo.scraping.model.Vaga;
import com.exemplo.scraping.service.BneScrapper;
import com.exemplo.scraping.service.HtmlGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App {


   public static void main(String[] args) {
       Scanner sc = new Scanner(System.in);
       try {
           System.out.println(
                   "Digite a área de trabalho (ex: Técnico em informática, Engenheiro de Software, Recepcionista):");
           String termo = sc.nextLine().trim().replace(" ", "+");


           ChromeOptions options = new ChromeOptions();
           options.addArguments("--headless=new", "--disable-gpu", "--disable-software-rasterizer",
                   "--window-size=1920,1080", "--disable-blink-features=AutomationControlled");
           WebDriver driver = new ChromeDriver(options);


           try {
               System.out.println("\n=== BNE ===");
               List<Vaga> bne = BneScrapper.buscar(driver, termo);


               HtmlGenerator.gerarRelatorio(termo, new ArrayList<>(), bne, new ArrayList<>());
               // HtmlGenerator.gerarRelatorio(termo, new ArrayList<>(), new ArrayList<>(),
               // indeed);
               // HtmlGenerator.gerarRelatorio(termo, glassdoor, bne, indeed);


           } finally {
               driver.quit();
           }


           System.out.println("\n Finalizado. Abra o arquivo resultado.html");


       } catch (Exception e) {
           System.err.println("Erro geral: " + e.getMessage());
       } finally {
           sc.close();
       }
   }


}
