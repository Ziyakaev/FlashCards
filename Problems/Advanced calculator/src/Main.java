import java.util.*;

/* Please, do not rename it */
class Problem {

    public static void main(String[] args) {
        String operator = args[0];

        List<String> list = new ArrayList<>(Arrays.asList(args));
        OptionalInt result = OptionalInt.of(0);
        switch (operator){
            case "MAX":
                result= list.stream()
                        .skip(1)
                        .mapToInt(t-> Integer.parseInt(t))
                        .max();
                break;
            case "MIN":
                result = list.stream()
                        .skip(1)
                        .mapToInt(t-> Integer.parseInt(t))
                        .min();
                break;
            case "SUM":
                result = OptionalInt.of(list.stream()
                        .skip(1)
                        .mapToInt(t-> Integer.parseInt(t))
                        .sum());
                break;
        }
        System.out.println(result.getAsInt());
        // write your code here
    }
}