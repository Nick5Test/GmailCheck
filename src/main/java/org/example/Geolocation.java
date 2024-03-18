package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Geolocation {

    public static void main(String[] args) {
        try {
            // Crea un URL per richiedere la tua posizione
            URL url = new URL("http://ip-api.com/json");

            // Apre una connessione URL

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Legge la risposta dall'API di geolocalizzazione
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Analizza la risposta JSON per ottenere i dettagli sulla posizione
            String jsonResponse = response.toString();
            System.out.println("Risposta JSON: " + jsonResponse);

            // Chiude la connessione
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
