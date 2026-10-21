
package pl.r0att.hardcoreresurrection.revival;

import java.util.Map;

public final class ItemCostCalculator {
    private ItemCostCalculator() {}

    public static boolean canAfford(
        Map<String, Integer> required,
        Map<String, Integer> available
    ) {
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            int requiredCount = entry.getValue();
            int availableCount = available.getOrDefault(entry.getKey(), 0);

            if (availableCount < requiredCount) {
                return false;
            }
        }

        return true;
    }
}
