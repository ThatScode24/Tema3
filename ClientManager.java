import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

class Client {
    String nume;
    int varsta;
    double sumaCont;
    Optional<String> tipClient;

    public Client(String nume, int varsta, double sumaCont, Optional<String> tipClient) {
        this.nume = nume;
        this.varsta = varsta;
        this.sumaCont = sumaCont;
        this.tipClient = tipClient;
    }

    @Override
    public String toString() {
        return nume + " - " + varsta + " ani - " + sumaCont + " RON - " + tipClient.orElse("Necunoscut");
    }
}

public class ClientManager {
    public static void main(String[] args) {
        List<Client> clienti = List.of(
                new Client("Ana", 22, 1200, Optional.of("VIP")),
                new Client("Mihai", 30, 5400, Optional.of("Standard")),
                    new Client("Beatrice", 28, 9000, Optional.of("VIP")),
                new Client("Aurelian", 19, 400, Optional.empty()),
                new Client("Radu", 25, 1500, Optional.of("Nou")),
                new Client("Ama", 35, 8500, Optional.of("VIP")),
                new Client("Andrei", 40, 2000, Optional.of("Standard")),
                new Client("Claudia", 18, 300, Optional.empty()),
                new Client("Victor", 27, 7500, Optional.of("VIP")),
                new Client("Simona", 24, 2100, Optional.of("Nou")),
                new Client("Vali", 29, 4700, Optional.of("Standard")),
                new Client("Oana", 33, 5100, Optional.of("Nou"))
        ); // cei 12 clienti

        double media = clienti.stream().mapToDouble(c -> c.sumaCont).average().orElse(0);

        Predicate<Client> isVipAndAboveAvg = c -> c.tipClient.orElse("").equals("VIP") && c.sumaCont > media;   // daca e vip si cu sumaCont peste media
        Function<Client, String> mapare = c -> c.nume + " - " + c.varsta + " ani";
        BinaryOperator<Double> adunare = Double::sum;
        Supplier<Map<String, Long>> supplier = () -> clienti.stream().collect(Collectors.groupingBy(c -> c.tipClient.orElse("Necunoscut"), Collectors.counting()));

        System.out.println("VIP peste medie:");
        clienti.stream().filter(isVipAndAboveAvg).forEach(System.out::println);  // filtrare cu stream pentru vip peste medie

        List<String> mapati = clienti.stream().map(mapare).collect(Collectors.toList());
        mapati.forEach(System.out::println);

        double totalCont = clienti.stream().map(c -> c.sumaCont).reduce(0.0, adunare);   // suma totala a sumelor conturilor
        System.out.println("Suma totală: " + totalCont);

        Map<String, Long> grupati = supplier.get();
        grupati.forEach((tip, count) -> System.out.println(tip + ": " + count));    // numaram cati clienti sunt pentru fiecare tip 

        String tineri = clienti.stream()   // cu stream
                .filter(c -> c.varsta < 25)
                .map(c -> c.nume)
                .collect(Collectors.joining(", "));    // colectam numele clientilor care au sub 25 de ani
        System.out.println("Clienți < 25 ani: " + tineri);
    }
}
