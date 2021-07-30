import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class main {

    public static int initialLength = 3;
    public static int nRuns = 10000;

    public static int[] initialOptions = new int[initialLength];
    public static int eliminatedOptionIndex;

    public static int correctOptionIndex;
    public static int initialPickIndex;
    public static int secondPickIndex;

    public static int successCountSwitch = 0;
    public static int successCountStick = 0;

    // success array - first row switch second row stick
    // each result is added to previous before appending array
    public static int[][] cumulativeSuccessesArray = new int[2][nRuns];

    public static void main(String[] args) throws PythonExecutionException, IOException {
/*      List<Double> x = NumpyUtils.linspace(-3, 3, 100);
        List<Double> y = x.stream().map(xi -> Math.sin(xi) +Math.random()).collect(Collectors.toList());

        Plot plt = Plot.create();
        plt.plot().add(x, y, "o").label("sin");
        plt.legend().loc("upper right");
        plt.title("scatter");
        plt.show();*/
        encapsulateSim();
        Double[] proportionCorrectSwitch = new Double[nRuns];

        for (int l = 0; l < nRuns -1 ; l++){
            proportionCorrectSwitch[l] = ((double) cumulativeSuccessesArray[0][l] / (l + 1));

        }

        Double[] proportionCorrectStick = new Double[nRuns];

        for (int l = 0; l < nRuns -1 ; l++){
            proportionCorrectStick[l] = ((double) cumulativeSuccessesArray[1][l] / (l + 1));
        }

        List<Integer> x = Arrays.stream(IntStream.rangeClosed(1, nRuns).toArray()).boxed().collect(Collectors.toList());
        List<Double> y = x.stream().map(xi -> proportionCorrectSwitch[xi -1]).collect(Collectors.toList());

        Plot plt = Plot.create();
        plt.plot().add(x, y).label("Proportion of success");
        plt.legend().loc("upper right");
        plt.title("scatter");
        plt.show();
        System.out.println("success count = " + String.valueOf(successCountSwitch));
    }

    public static int encapsulateSim(){
        // simulate switch n times, returns 1 for success 0 for fail
        cumulativeSuccessesArray[0][0] =  simulateSwitch();
        for(int j = 1; j < nRuns; j++){
            cumulativeSuccessesArray[0][j] = simulateSwitch() + cumulativeSuccessesArray[0][j - 1];
        }
        // simulate stick n times, returns 1 for success 0 for fail
        cumulativeSuccessesArray[1][0] =  simulateStick();
        for(int k = 1; k < nRuns; k++){
            cumulativeSuccessesArray[1][k] = simulateStick() + cumulativeSuccessesArray[1][k - 1];
        }
        System.out.println(Arrays.deepToString(cumulativeSuccessesArray));
        return 0;
    }

    public static int simulateSwitch(){
        generateNewPlacement();
        generateInitialPick();
        /*if(initialOptions[initialPickIndex] == 0) {
            return 1;
        } else {
            return 0;
        }*/
        generateEliminatedOption();
        generateSecondPick();

        return checkSuccess(secondPickIndex);
    }

    public static int simulateStick(){
        // generate random success placement
        generateNewPlacement();
        // generate random initialPickIndex
        generateInitialPick();
        // generate random eliminatedOption
        generateEliminatedOption();
        // assign second pick to first pick!
        generateSecondPick(initialPickIndex);
        // return success or fail
        return checkSuccess(secondPickIndex);
    }

    public static void generateNewPlacement(){
        Arrays.fill(initialOptions, 0);
        correctOptionIndex = ThreadLocalRandom.current().nextInt(0, initialLength);
        initialOptions[correctOptionIndex] = 1;
    }

    public static void generateInitialPick(){
        initialPickIndex = ThreadLocalRandom.current().nextInt(0, initialLength);
    }
    
    public static void generateEliminatedOption(){
        do {
            eliminatedOptionIndex = ThreadLocalRandom.current().nextInt(0, initialLength);
        }while (eliminatedOptionIndex != initialPickIndex && eliminatedOptionIndex != correctOptionIndex);
    }

    public static void generateSecondPick(){
        do {
            secondPickIndex = ThreadLocalRandom.current().nextInt(0, initialLength);
        } while(secondPickIndex != initialPickIndex && secondPickIndex != eliminatedOptionIndex);
    }

    public static void generateSecondPick(int firstPick){
        secondPickIndex = firstPick;
    }

    public static int checkSuccess(int finalChoice){
        if(finalChoice == correctOptionIndex){
            System.out.println("Success! player wins");
        }
        return (finalChoice == correctOptionIndex) ? 1 : 0;
    }
}
