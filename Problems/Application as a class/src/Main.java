import java.util.Arrays;

class Application {

    String name;

    void run(String[] args) {
        System.out.println(name);
        Arrays.stream(args).forEach(t-> System.out.println(t));
        // implement me
    }
}