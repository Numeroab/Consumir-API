package com.exemplo.scraping.model;


public class ScrapRequest {
   private String termo;
  
   public ScrapRequest() {}
  
   public ScrapRequest(String termo) {
       this.termo = termo;
   }
  
   public String getTermo() { return termo; }
   public void setTermo(String termo) { this.termo = termo; }
}
