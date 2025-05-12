import java.io.*;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class InvalidDataException extends Exception {
    public InvalidDataException(String msg) {
        super(msg);
    }
}

class Produs implements Serializable {
    private String nume;
    private double pret;
    private int stoc;

    public Produs(String nume, double pret, int stoc) throws InvalidDataException {
        if (pret < 0 || stoc < 0)  throw new InvalidDataException("Pretul sau stocul nu pot fi negative");   // valori negative interzise
        this.nume = nume;
        this.pret = pret;
        this.stoc = stoc;
    }

    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public int getStoc() { return stoc; }

    public void setStoc(int stoc) { this.stoc = stoc; }

    @Override
    public String toString() {
        return nume + " - " + pret + " RON - Stoc: " + stoc;
    }
}

public class ProdusManager {
    public static void main(String[] args) {
        List<Produs> produse = new ArrayList<>();
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("erori.log", true))) {
            for (int i = 0; i < 10; i++) {
                try {
                    produse.add(new Produs("Produs" + i, i * 10, i == 5 ? 0 : i * 3));    // cream produsele
                } catch (InvalidDataException e) {
                    logWriter.println(e.getMessage());    // logam erorile eventuale
                }
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("produse.dat"))) {
                for (Produs p : produse) oos.writeObject(p);   // scriem obiectele cu acest oos
            } catch (IOException e) {
                logWriter.println("Eroare la scriere: " + e.getMessage()); // logam erorile eventuale
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Produs> produseCitite = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("produse.dat"))) {
            while (true) {
                try {
                    produseCitite.add((Produs) ois.readObject());     // citim obiectele din produse.dat
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("Clasa negăsită: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Eroare la citire: " + e.getMessage());
        }

        List<Produs> epuizate = produseCitite.stream()
                .filter(p -> p.getStoc() == 0)  // daca stocul e 0, scriem in epuizate.txt
                .collect(Collectors.toList());
        try (PrintWriter writer = new PrintWriter("epuizate.txt")) {
            epuizate.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UnaryOperator<Produs> scadereStoc = p -> {
            p.setStoc((int)(p.getStoc() * 0.9));    // facem reducere de 10% pentru toate produsele
            return p;
        };
        produseCitite.replaceAll(scadereStoc);   // dam replace

        // aflam produsul cu cel mai mare pret
        produseCitite.stream().max(Comparator.comparingDouble(Produs::getPret)).ifPresent(System.out::println);
    }
}
