import java.util.ArrayList;
import java.util.List;

public class DeliveryProblem {
    private static final int NUM_CITIES = 3;
    private static final int NUM_VILLAGES = 3;

    public static void main(String[] args) {
        List<List<Integer>> paths = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();

        findPaths(0, 0, paths, currentPath);

        System.out.println("Possible paths:");
        for (List<Integer> path : paths) {
            System.out.println(path);
        }
    }

    private static void findPaths(int currentCity, int currentVillage, List<List<Integer>> paths, List<Integer> currentPath) {
        currentPath.add(currentCity);

        if (currentPath.size() == NUM_VILLAGES + 1 && currentPath.get(NUM_VILLAGES) == 0) {
            // Found a complete path
            paths.add(new ArrayList<>(currentPath));
        } else {
            for (int i = 0; i < NUM_CITIES; i++) {
                if (i != currentCity) {
                    findPaths(i, (currentVillage + 1) % NUM_CITIES, paths, currentPath);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
    }
}
