import java.io.*;
import java.util.*;
import java.util.stream.*;

class Persoana {
    String nume;
    int varsta;
    String oras;

    public Persoana(String nume, int varsta, String oras) {
        this.nume = nume;
        this.varsta = varsta;
        this.oras = oras;
    }

    @Override
    public String toString() { return nume + " - " + varsta + " - " + oras; }
}

public class PersoanaManager {
    public static void main(String[] args) {
        List<Persoana> persoane = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("date.txt"))) {
            persoane = reader.lines()
                    .map(l -> l.split(";"))   // procesam persoanele scrise sub formatul nume;varsta;oras
                    .filter(arr -> arr.length == 3)
                    .map(arr -> new Persoana(arr[0], Integer.parseInt(arr[1]), arr[2]))
                    .collect(Collectors.toList());
        } catch (IOException e) { e.printStackTrace(); }

        List<Persoana> filtrate = persoane.stream()
                .filter(p -> p.varsta > 30 && p.oras.startsWith("B"))
                .sorted(Comparator.comparing((Persoana p) -> p.nume).thenComparingInt(p -> p.varsta))
                .collect(Collectors.toList());   // persoane de peste 30 ani care locuiesc în orașe care încep cu litera B


        Map<String, List<Persoana>> grupate = persoane.stream().collect(Collectors.groupingBy(p -> p.oras));

        Map<String, Double> mediaVarste = grupate.entrySet().stream()   // calculam mediile de varsta pe oras
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().mapToInt(p -> p.varsta).average().orElse(0)));

        Optional<Persoana> maxVarsta = persoane.stream().max(Comparator.comparingInt(p -> p.varsta));    // Persoana cu varsta maxima

        try (PrintWriter writer = new PrintWriter("rezultat.txt")) {
            writer.println("Persoane filtrate și sortate:");
            filtrate.forEach(writer::println);
            writer.println("\nMedia de vârstă pe oraș:");
            mediaVarste.forEach((oras, media) -> writer.println(oras + ": " + media));
        } catch (IOException e) { e.printStackTrace(); }

        maxVarsta.ifPresent(p -> System.out.println("Persoana cu vârsta maximă: " + p));
    }
}
