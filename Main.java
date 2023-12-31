import java.io.*;
import java.util.*;
import java.lang.Math;

class Main {
    static long cacheSize = 1;
    static long associativity = 1;
    static long blockSize = 64;
    static String traceFilePath;
    static long totalHit = 0, totalMiss = 0;

    public static void main(String args[]) {

        if (args.length < 4) {
            System.out.println("Invalid input");
            return;
        } 
        Double cacheinkb=Double.parseDouble(args[0]);
         if (Math.floor(cacheinkb*1024) != cacheinkb*1024 || Math.floor(Double.parseDouble(args[1]))!=Double.parseDouble(args[1])|| Double.parseDouble(args[2])!=64) {
            System.out.println("Invalid input");
            return;
        }
            cacheSize = (long)(cacheinkb*1024);
            associativity = Long.parseLong(args[1]);
            blockSize= Long.parseLong(args[2]);
            traceFilePath = args[3];
            if( (cacheSize)/ (associativity*64)==0)
            {
            System.out.println("Invalid input");
            return;                
            }
        
       
        long numSets = (cacheSize)/ (associativity*64);
        long setBits = (long) (Math.log(numSets) / Math.log(2));

        List<String> addresses = new ArrayList<>();

        try {
            File file = new File(traceFilePath);
            Scanner scanner = new Scanner(file);

            // Read each line/address from the trace file
            while (scanner.hasNextLine()) {
                String address = scanner.nextLine();
                addresses.add(address);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Trace file not found: " + traceFilePath);
            return;
        }

        long[][] cache = new long[(int) numSets][(int) associativity + 1];
        int[] hit = new int[(int) numSets];
        int[] miss = new int[(int) numSets];

        for (int i = 0; i < numSets; i++) {
            for (int j = 0; j < associativity + 1; j++) {
                cache[i][j] = 0;
            }
        }

        long x, setIndex, tag;
        long mask = 1;
        for (int i = 0; i < setBits; i++) {
            mask *= 2;
        }
        mask -= 1;// will be used to find setindex
        // going through each address
        for (String address : addresses) {

            x = Long.parseLong(address,16);
            x = x >> 6;
            setIndex = x & mask;
            tag = x >> setBits;
            for (int i = 0; i < associativity; i++) {
                if (cache[(int) setIndex][i] == tag) {
                    totalHit++;
                    hit[(int) setIndex]++;
                    int j = i;
                    while (cache[(int) setIndex][j + 1] != 0) {
                        cache[(int) setIndex][j] = cache[(int) setIndex][j + 1];
                        j++;
                    }
                    cache[(int) setIndex][j] = tag;
                    break;
                } else if (cache[(int) setIndex][i] == 0) {
                    cache[(int) setIndex][i] = tag;
                    totalMiss++;
                    miss[(int) setIndex]++;
                    break;
                } else if (i == associativity - 1) {
                    int j = 0;
                    totalMiss++;
                    miss[(int) setIndex]++;
                    while (j != i) {
                        cache[(int) setIndex][j] = cache[(int) setIndex][j + 1];
                        j++;
                    }
                    cache[(int) setIndex][j] = tag;
                    break;
                }
            }
        }
        for (int i = 0; i < numSets; i++) {
            System.out.println("Total hit in set " + i + " is: " + hit[i]);
            System.out.println("Total miss in set " + i + " is: " + miss[i]);
        }
        System.out.println("Total hit in cache is: " + totalHit);
        System.out.println("Total miss in cache is: " + totalMiss);
    }
}