public class HeapMemoryCheck {
    public static void main(String[] args) {
        long maxHeapSize = Runtime.getRuntime().maxMemory();
        System.out.println("Max Heap Size: " + maxHeapSize / (1024 * 1024) + " MB");
    }
}
