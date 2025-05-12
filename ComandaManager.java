import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Comanda implements Serializable {
    private int id;
    private String numeClient;
    private double valoare;
    private boolean finalizata;

    public Comanda(int id, String numeClient, double valoare, boolean finalizata) {
        this.id = id;
        this.numeClient = numeClient;
        this.valoare = valoare;
        this.finalizata = finalizata;
    }

    public int getId() { return id; }
    public String getNumeClient() { return numeClient; }
    public double getValoare() { return valoare; }
    public boolean isFinalizata() { return finalizata; }
    public void setFinalizata(boolean val) { this.finalizata = val; }

    @Override
    public String toString() {
        return id + " - " + numeClient + " - " + valoare + " RON - Finalizata: " + finalizata;
    }
}

public class ComandaManager {
    public static void main(String[] args) {
        String fisier = "comenzi.dat";
        List<Comanda> comenzi = new ArrayList<>();

        // cream 15 comenzi
        for (int i = 0; i < 15; i++) {
            comenzi.add(new Comanda(i, "Client" + (i % 5), i * 800, false));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fisier))) {
            for (Comanda c : comenzi) {
                oos.writeObject(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // citim si actualizam comenzile de peste 5000
        List<Comanda> actualizate = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fisier))) {
            while (true) {
                try {
                    Comanda c = (Comanda) ois.readObject();
                    if (c.getValoare() > 5000) {
                        c.setFinalizata(true);
                    }
                    actualizate.add(c);
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        // scriem inapoi in fisier cu oss
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fisier))) {
            for (Comanda c : actualizate) {
                oos.writeObject(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // procesare comenzi finalizate
        List<Comanda> finalizate = actualizate.stream()
                .filter(Comanda::isFinalizata)
                .collect(Collectors.toList());

        double sumaFinalizate = finalizate.stream()   // facem suma comenzilor finalizate
                .mapToDouble(Comanda::getValoare)
                .sum();

        System.out.println("Suma comenzilor finalizate: " + sumaFinalizate);

        Map<String, List<Comanda>> grupate = finalizate.stream()
                .collect(Collectors.groupingBy(Comanda::getNumeClient));  // grupam pe client

        grupate.forEach((client, comenziClient) -> {
            System.out.println("\nClient: " + client);
            comenziClient.forEach(System.out::println);
        });
    }
}
