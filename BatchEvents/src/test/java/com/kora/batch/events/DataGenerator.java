package com.kora.batch.events;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {

    private static final String FILE_NAME = "src/main/resources/persons.csv";
    private static final int TOTAL_RECORDS = 5000;
    
    private static final String[] FIRST_NAMES = {"Jean", "Marie", "Lucas", "Chloé", "Thomas", "Camille", "Nicolas"};
    private static final String[] LAST_NAMES = {"Dupont", "Lefebvre", "Martin", "Moreau", "Laurent", "Girard"};
    private static final String[] CITIES = {"Paris", "Lyon", "Marseille", "Bordeaux", "Nantes", "Lille"};

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Write Header
            writer.write("personId,firstName,lastName,siret,city,salary,category");
            writer.newLine();

            for (int i = 1; i <= TOTAL_RECORDS; i++) {
                writer.write(generateRow(i));
                writer.newLine();
            }
            
            System.out.println("Generated " + TOTAL_RECORDS + " entries in " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRow(int id) {
        String firstName = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
        String city = CITIES[ThreadLocalRandom.current().nextInt(CITIES.length)];
        
        // Generate a random 14-digit SIRET
        long siret = ThreadLocalRandom.current().nextLong(10000000000000L, 99999999999999L);
        
        // Generate salary between 15000 and 90000
        double salary = ThreadLocalRandom.current().nextDouble(15000, 90000);

        return String.format("%d,%s,%s,%d,%s,%.2f,EMP", id, firstName, lastName, siret, city, salary);
    }
}