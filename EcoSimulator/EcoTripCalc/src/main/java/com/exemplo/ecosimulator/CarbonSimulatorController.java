package com.exemplo.ecosimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*") // Permite que o HTML local acesse a API
public class CarbonSimulatorController {

	private static final double PRECO_POR_TONELADA_BRL = 150.00; // Preço fictício de mercado
	
    public static void main(String[] args) {
        SpringApplication.run(CarbonSimulatorController.class, args);
    }

    // Endpoint da API
    @PostMapping("/api/calcular")
    public ResultadoCalculo calcularImpacto(@RequestBody DadosViagem dados) {
        double fatorEmissao = obterFatorEmissao(dados.getTransporte());
        
        // Cálculo: Distância * Fator
        double totalCo2Kg = dados.getDistancia() * fatorEmissao;
        
        // 1 Crédito de Carbono = 1000 kg de CO2
        double creditosNecessarios = totalCo2Kg / 1000.0;
        
        // 2. Calcula o Custo Financeiro (Novo!)
        double custoCompensacao = creditosNecessarios * PRECO_POR_TONELADA_BRL;

        return new ResultadoCalculo(totalCo2Kg, creditosNecessarios, custoCompensacao);
    }

    private double obterFatorEmissao(String transporte) {
        switch (transporte.toLowerCase()) {
            case "bicicleta": return 0.0;
            case "onibus": return 0.08;
            case "moto": return 0.12;
            case "carro": return 0.19;
            default: return 0.0;
        }
    }

    // Classes auxiliares (DTOs)
    static class DadosViagem {
        private String transporte;
        private double distancia;

        // Getters e Setters
        public String getTransporte() { return transporte; }
        public void setTransporte(String transporte) { this.transporte = transporte; }
        public double getDistancia() { return distancia; }
        public void setDistancia(double distancia) { this.distancia = distancia; }
    }

    static class ResultadoCalculo {
        private double co2EmitidoKg;
        private double creditosCarbono;
        private double custoEstimadoReais;

        public ResultadoCalculo(double co2EmitidoKg, double creditosCarbono, double custoEstimadoReais) {
            this.co2EmitidoKg = co2EmitidoKg;
            this.creditosCarbono = creditosCarbono;
            this.custoEstimadoReais = custoEstimadoReais;
            
        }

        public double getCo2EmitidoKg() { return co2EmitidoKg; }
        public double getCreditosCarbono() { return creditosCarbono; }
        public double getCustoEstimadoReais() { return custoEstimadoReais; }
    }
}
