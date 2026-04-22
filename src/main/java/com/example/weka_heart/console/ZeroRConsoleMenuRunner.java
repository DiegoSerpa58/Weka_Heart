package com.example.weka_heart.console;

import com.example.weka_heart.request.PatientDataRequest;
import com.example.weka_heart.response.PredictionResponse;
import com.example.weka_heart.service.PredictionService;
import com.example.weka_heart.service.ZeroRConsoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@ConditionalOnProperty(name = "console.menu.enabled", havingValue = "true")
public class ZeroRConsoleMenuRunner implements CommandLineRunner {

    private final ZeroRConsoleService consoleService;
    private final PredictionService   predictionService;

    public ZeroRConsoleMenuRunner(ZeroRConsoleService consoleService,
                                  PredictionService predictionService) {
        this.consoleService    = consoleService;
        this.predictionService = predictionService;
    }

    @Override
    public void run(String... args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int option = readInt(scanner, "Opción: ");
                switch (option) {
                    case 1 -> handlePrediction(scanner);
                    case 2 -> handleCrossValidation(scanner);
                    case 3 -> handlePercentageSplit(scanner);
                    case 0 -> {
                        System.out.println("Saliendo del menú. La aplicación Spring Boot continúa en ejecución.");
                        running = false;
                    }
                    default -> System.out.println("Opción inválida. Por favor elija 0, 1, 2 o 3.");
                }
            }
        }
    }

    // ── Menu handlers ─────────────────────────────────────────────────────────

    private void handlePrediction(Scanner scanner) {
        System.out.println("\n--- Predicción ZeroR para un paciente ---");
        PatientDataRequest request = readPatientData(scanner);
        try {
            String predictedClass = consoleService.predict(request);
            System.out.println("\nResultado ZeroR: " + predictedClass);
            System.out.println("Nota: ZeroR siempre predice la clase mayoritaria del dataset.");

            PredictionResponse saved = predictionService.predict(request);
            System.out.println("✅ Predicción guardada (id=" + saved.id() + "). " +
                    "Consúltala en: http://localhost:8080/api/prediction/patients");
        } catch (Exception e) {
            System.out.println("Error al realizar la predicción: " + e.getMessage());
        }
    }

    private void handleCrossValidation(Scanner scanner) {
        System.out.println("\n--- Evaluación ZeroR con Cross-Validation ---");
        int folds = readPositiveInt(scanner, "Número de folds: ");
        int seed  = readInt(scanner, "Seed: ");
        try {
            System.out.println(consoleService.evaluateCrossValidation(folds, seed));
        } catch (Exception e) {
            System.out.println("Error al realizar la evaluación: " + e.getMessage());
        }
    }

    private void handlePercentageSplit(Scanner scanner) {
        System.out.println("\n--- Evaluación ZeroR con Percentage Split ---");
        double trainPercent = readPercent(scanner, "Porcentaje de entrenamiento (ej. 70): ");
        int    seed         = readInt(scanner, "Seed: ");
        try {
            System.out.println(consoleService.evaluatePercentageSplit(trainPercent, seed));
        } catch (Exception e) {
            System.out.println("Error al realizar la evaluación: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("  Menú ZeroR - Weka Heart");
        System.out.println("========================================");
        System.out.println("1. Predicción ZeroR para un paciente");
        System.out.println("2. Evaluación ZeroR con Cross-Validation");
        System.out.println("3. Evaluación ZeroR con Percentage Split");
        System.out.println("0. Salir del menú");
        System.out.println("----------------------------------------");
    }

    private PatientDataRequest readPatientData(Scanner scanner) {
        System.out.println("Ingrese los 8 atributos del paciente:");
        return new PatientDataRequest(
                readDouble(scanner, "preg (embarazos): "),
                readDouble(scanner, "plas (glucosa en plasma): "),
                readDouble(scanner, "pres (presión arterial): "),
                readDouble(scanner, "skin (grosor de piel): "),
                readDouble(scanner, "insu (insulina): "),
                readDouble(scanner, "mass (índice de masa corporal): "),
                readDouble(scanner, "pedi (función de pedigrí de diabetes): "),
                readDouble(scanner, "age (edad): ")
        );
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número entero.");
            }
        }
    }

    private int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) return value;
            System.out.println("El valor debe ser mayor que 0.");
        }
    }

    private double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número.");
            }
        }
    }

    private double readPercent(Scanner scanner, String prompt) {
        while (true) {
            double value = readDouble(scanner, prompt);
            if (value > 0 && value < 100) return value;
            System.out.println("El porcentaje debe estar entre 0 y 100 (exclusivo).");
        }
    }
//    private final ZeroRConsoleService zeroRConsoleService;
//    private final PredictionService predictionService;
//
//    public ZeroRConsoleMenuRunner(ZeroRConsoleService zeroRConsoleService, PredictionService predictionService) {
//        this.zeroRConsoleService = zeroRConsoleService;
//        this.predictionService = predictionService;
//    }
//
//    @Override
//    public void run(String... args) {
//        Scanner scanner = new Scanner(System.in);
//        boolean running = true;
//
//        try {
//            while (running) {
//                printMenu();
//                int option = readInt(scanner, "Opción: ");
//
//                switch (option) {
//                    case 1 -> handlePrediction(scanner);
//                    case 2 -> handleCrossValidation(scanner);
//                    case 3 -> handlePercentageSplit(scanner);
//                    case 0 -> {
//                        System.out.println("Saliendo del menú. La aplicación Spring Boot continúa en ejecución.");
//                        running = false;
//                    }
//                    default -> System.out.println("Opción inválida. Por favor elija 0, 1, 2 o 3.");
//                }
//            }
//        } finally {
//            scanner.close();
//        }
//    }
//
//    private void printMenu() {
//        System.out.println("\n========================================");
//        System.out.println("  Menú ZeroR - Weka Heart");
//        System.out.println("========================================");
//        System.out.println("1. Predicción ZeroR para un paciente");
//        System.out.println("2. Evaluación ZeroR con Cross-Validation");
//        System.out.println("3. Evaluación ZeroR con Percentage Split");
//        System.out.println("0. Salir del menú");
//        System.out.println("----------------------------------------");
//    }
//
//    private void handlePrediction(Scanner scanner) {
//        System.out.println("\n--- Predicción ZeroR para un paciente ---");
//        System.out.println("Ingrese los 8 atributos del paciente:");
//
//        double preg = readDouble(scanner, "preg (embarazos): ");
//        double plas = readDouble(scanner, "plas (glucosa en plasma): ");
//        double pres = readDouble(scanner, "pres (presión arterial): ");
//        double skin = readDouble(scanner, "skin (grosor de piel): ");
//        double insu = readDouble(scanner, "insu (insulina): ");
//        double mass = readDouble(scanner, "mass (índice de masa corporal): ");
//        double pedi = readDouble(scanner, "pedi (función de pedigrí de diabetes): ");
//        double age  = readDouble(scanner, "age (edad): ");
//
//        try {
//            String predictedClass = zeroRConsoleService.predict(preg, plas, pres, skin, insu, mass, pedi, age);
//            System.out.println("\nResultado de la predicción ZeroR: " + predictedClass);
//            System.out.println("Nota: ZeroR siempre predice la clase mayoritaria del dataset, independientemente de los atributos ingresados.");
//
//            // Guardar la predicción en PredictionService para que aparezca en GET /api/prediction/patients
//            PatientDataRequest request = new PatientDataRequest();
//            request.setPreg(preg);
//            request.setPlas(plas);
//            request.setPres(pres);
//            request.setSkin(skin);
//            request.setInsu(insu);
//            request.setMass(mass);
//            request.setPedi(pedi);
//            request.setAge(age);
//            predictionService.savePrediction(request, predictedClass);
//            System.out.println("✅ Predicción guardada en JSON. Consúltala en: http://localhost:8080/api/prediction/patients");
//        } catch (Exception e) {
//            System.out.println("Error al realizar la predicción: " + e.getMessage());
//        }
//    }
//
//    private void handleCrossValidation(Scanner scanner) {
//        System.out.println("\n--- Evaluación ZeroR con Cross-Validation ---");
//        int folds = readPositiveInt(scanner, "Número de folds: ");
//        int seed  = readInt(scanner, "Seed (número entero): ");
//
//        try {
//            String result = zeroRConsoleService.evaluateCrossValidation(folds, seed);
//            System.out.println(result);
//        } catch (Exception e) {
//            System.out.println("Error al realizar la evaluación: " + e.getMessage());
//        }
//    }
//
//    private void handlePercentageSplit(Scanner scanner) {
//        System.out.println("\n--- Evaluación ZeroR con Percentage Split ---");
//        double trainPercent = readPercent(scanner, "Porcentaje de entrenamiento (ej. 70): ");
//        int seed = readInt(scanner, "Seed (número entero): ");
//
//        try {
//            String result = zeroRConsoleService.evaluatePercentageSplit(trainPercent, seed);
//            System.out.println(result);
//        } catch (Exception e) {
//            System.out.println("Error al realizar la evaluación: " + e.getMessage());
//        }
//    }
//
//    // --- Helpers de lectura con validación ---
//
//    private int readInt(Scanner scanner, String prompt) {
//        while (true) {
//            System.out.print(prompt);
//            String line = scanner.nextLine().trim();
//            try {
//                return Integer.parseInt(line);
//            } catch (NumberFormatException e) {
//                System.out.println("Entrada inválida. Por favor ingrese un número entero.");
//            }
//        }
//    }
//
//    private int readPositiveInt(Scanner scanner, String prompt) {
//        while (true) {
//            int value = readInt(scanner, prompt);
//            if (value > 0) {
//                return value;
//            }
//            System.out.println("El valor debe ser mayor que 0.");
//        }
//    }
//
//    private double readDouble(Scanner scanner, String prompt) {
//        while (true) {
//            System.out.print(prompt);
//            String line = scanner.nextLine().trim();
//            try {
//                return Double.parseDouble(line);
//            } catch (NumberFormatException e) {
//                System.out.println("Entrada inválida. Por favor ingrese un número.");
//            }
//        }
//    }
//
//    private double readPercent(Scanner scanner, String prompt) {
//        while (true) {
//            double value = readDouble(scanner, prompt);
//            if (value > 0 && value < 100) {
//                return value;
//            }
//            System.out.println("El porcentaje debe estar entre 0 y 100 (exclusivo).");
//        }
//    }
}
