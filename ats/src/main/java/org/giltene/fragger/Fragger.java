//
//  Fragger.java
//
// Written by Gil Tene, and released to the public domain, as explained at
// http://creativecommons.org/licenses/publicdomain
// http://www.azulsystems.com/resources/tools
// V 1.0.7

package org.giltene.fragger;

import java.util.*;
import java.util.jar.*;
import java.util.concurrent.atomic.*;
import java.lang.management.*;
import java.lang.reflect.*;
import java.net.*;
import java.io.*;

// fragger: A heap fragmentation inducer, meant to induce compaction of
// the heap on a regular basis using a limited (and settable) amount of
// CPU and memory resources.
//
// The purpose of  fragger is [among other things] to aid application
// testers in inducing inevitable-but-rare garbage collection events,
// such that they would occur on a regular and more frequent and reliable
// basis. Doing so allows the characterization of system behavior, such
// as response time envelope, within practical test cycle times.
//
// fragger works on the simple basis of repeatedly generating large sets
// of objects of a given size, pruning each set down to a much smaller
// remaining live set, and increasing the object size between passes such
// that is becomes unlikely to fit in the areas freed up by objects
// released in a previous pass without some amount of compaction. Fragger
// ages object sets before pruning them down in order to bypass potential
// artificial early compaction by young generation collectors.
//
// By the time enough passes are done such that aggregate space allocated
// by the passes roughly matches the heap size (although a much smaller
// percentage is actually alive), some level of compaction likely or
// inevitable.
//
// fragger's resource consumption is completely tunable, it will throttle
// itself to a tunable rate of allocation, and limit it's heap footprint
// to configurable level. When run with default settings, fragger will
// occupy ~10% of the total heap space, and allocate objects at a rate
// of 20MB/sec.
//
// Altering the heap occupancy ratio (which by default changes the number
// of passes in a compaction-inducing iteration), and the target
// allocation rate will change the frequency with which compactions
// occur.The main (common) settable items are:
//
// allocMBsPerSec [-a, default: 20]: Allocation rate - controls the CPU %
// fragger occupies, and affects compaction event freq.
//
// maxPassHeapFraction [-f, default: 0.1]: Drives the % of heap that
// would be used by fragger for it's peak live set.
//
// genChurnHeapFraction [-g, default: 0.1]: Controls the % of heap to be
// churned through (just churned, near-zero being alive) between passes.
// This should be set high enough to ensure the objects allocated in each
// pass become "old" and get promoted before being pruned.
//
// heapMBtoSitOn [-s, default: 0]: Useful for experimenting with the
// effects of varying heap occupancies on compaction times. Causes
// fragger to pre-allocate an additional static live set of the given
// size.
//
// For convenience in testing, fragger can be run as a wrapper, such that
// it executes another class or jar with arbitrary parameters using the
// -exec option. For example, if your program were normally executed as:
//
// java UsefulProgram -a -b -c
//
// or:
//
// java -jar UsefulProgram.jar -a -b -c
//
// This is how you would execute the same program so that fragger would
// churn along while it is running:
//
// java -jar Fragger.jar -exec UsefulProgram -a -b -c
//
// or:
//
// java -jar Fragger.jar -exec UsefulProgram.jar -a -b -c
//
// (or the more generic:)
// java org.giltene.fragger.Fragger -exec UsefulProgram -a -b -c


public class Fragger extends Thread {

    static final int MB = 1024 * 1024;

    class FraggerConfiguration {
        public long allocMBsPerSec = 20;
        public double maxPassHeapFraction = 0.1;
        public double genChurnHeapFraction = 0.1;

        public long numIterations = 0;
        public int numStorePasses = (int) (1.0 / maxPassHeapFraction) + 1;

        public long pruneRatio = 50;
        public long pruneOpsPerSec = 1000 * 1000;
        public long yielderMillis = 5;
        public long maxYieldCreditMs = 30;
        public long yieldCountBetweenReports = 20;

        public long pauseThresholdMs = 350;

        public int initialFragObjectSize = 32;
        public int estimatedArrayOverheadInBytes = 16;
        public double fragObjectSizeMultiplier = 1.0;
        public long fragObjectSizeIncrement = 32;

        public long estimatedHeapMB = 0;
        public int fragStoreBucketCount = 100;
        public int numBytesPerChurningByteArray = 256;
        public boolean verbose = false;
        public int heapMBtoSitOn = 0;

        public String execClassName = null;
        public String[] execArgs = null;

        void estimateHeapSize() {
            MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
            MemoryUsage memoryUsage = mxbean.getHeapMemoryUsage();
            estimatedHeapMB = (int) (memoryUsage.getMax() / (1024 * 1024));
        }

        public void parseArgs(String[] args) {
            estimateHeapSize();
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-v")) {
                    setVerbose(true);
                } else if (args[i].equals("-a")) {
                    setAllocMBsPerSec(Long.parseLong(args[++i]));
                } else if (args[i].equals("-f")) {
                    setMaxPassHeapFraction(Double.parseDouble(args[++i]));
                } else if (args[i].equals("-g")) {
                    setGenChurnHeapFraction(Double.parseDouble(args[++i]));
                } else if(args[i].equals("-s")) {
                    setHeapMBtoSitOn(Integer.parseInt(args[++i]));
                } else if (args[i].equals("-n")) {
                    setNumIterations(Long.parseLong(args[++i]));
                } else if (args[i].equals("-p")) {
                    setNumStorePasses(Integer.parseInt(args[++i]));
                } else if (args[i].equals("-t")) {
                    setPauseThresholdMs(Long.parseLong(args[++i]));
                } else if (args[i].equals("-i")) {
                    setFragObjectSizeIncrement(Long.parseLong(args[++i]));
                } else if (args[i].equals("-m")) {
                    setFragObjectSizeMultiplier(Double.parseDouble(args[++i]));
                } else if (args[i].equals("-r")) {
                    setPruneRatio(Long.parseLong(args[++i]));
                } else if (args[i].equals("-y")) {
                    setYielderMillis(Long.parseLong(args[++i]));
                } else if (args[i].equals("-e")) {
                    setEstimatedHeapMB(Integer.parseInt(args[++i]));
                } else if (args[i].equals("-o")) {
                    setPruneOpsPerSec(Long.parseLong(args[++i]));
                } else if (args[i].equals("-exec")) {
                    execClassName = args[++i];
                    i++;
                    execArgs = new String[args.length - i];
                    System.arraycopy(args, i, execArgs, 0, (args.length - i));
                    i = args.length;
                } else {
                    System.out.println("Usage: java Fragger [-v] " +
                            "[-a allocMBsPerSec] [-f maxPassHeapFraction] [-g genChurnHeapFraction] " +
                            "[-n <numIterations>] [-p <numPasses>] [-t pauseThresholdMs ] " +
                            "[-i fragObjectSizeIncrement] [-m fragObjectSizeMultiplier] " +
                            "[-r pruneRatio] [-y yielderMillis] [-e estimatedHeapMB] " +
                            "[-o pruneOpsPerSec] [-s heapMBtoSitOn] [-exec ClassName {args...}]");
                    System.exit(1);
                }
            }
        }

        FraggerConfiguration() {
            estimateHeapSize();
        }
    }

    FraggerConfiguration config = new FraggerConfiguration();

    public void setAllocMBsPerSec(long allocMBsPerSec) {
        config.allocMBsPerSec = allocMBsPerSec;
    }

    public void setMaxPassHeapFraction(double maxPassHeapFraction) {
        config.maxPassHeapFraction = maxPassHeapFraction;
        config.numStorePasses = (int) (1.0 / config.maxPassHeapFraction) + 1;
    }

    public void setGenChurnHeapFraction(double genChurnHeapFraction) {
        config.genChurnHeapFraction = genChurnHeapFraction;
    }

    public void setNumIterations(long numIterations) {
        config.numIterations = numIterations;
    }

    public void setNumStorePasses(int numStorePasses) {
        config.numStorePasses = numStorePasses;
    }

    public void setPruneRatio(long pruneRatio) {
        config.pruneRatio = pruneRatio;
    }

    public void setPruneOpsPerSec(long pruneOpsPerSec) {
        config.pruneOpsPerSec = pruneOpsPerSec;
    }

    public void setYielderMillis(long yielderMillis) {
        config.yielderMillis = yielderMillis;
    }

    public void setMaxYieldCreditMs(long maxYieldCreditMs) {
        config.maxYieldCreditMs = maxYieldCreditMs;
    }

    public void setYieldCountBetweenReports(long yieldCountBetweenReports) {
        config.yieldCountBetweenReports = yieldCountBetweenReports;
    }

    public void setPauseThresholdMs(long pauseThresholdMs) {
        config.pauseThresholdMs = pauseThresholdMs;
    }

    public void setInitialFragObjectSize(int initialFragObjectSize) {
        config.initialFragObjectSize = initialFragObjectSize;
    }

    public void setEstimatedArrayOverheadInBytes(int estimatedArrayOverheadInBytes) {
        config.estimatedArrayOverheadInBytes = estimatedArrayOverheadInBytes;
    }

    public void setFragObjectSizeMultiplier(double fragObjectSizeMultiplier) {
        config.fragObjectSizeMultiplier = fragObjectSizeMultiplier;
    }

    public void setFragObjectSizeIncrement(long fragObjectSizeIncrement) {
        config.fragObjectSizeIncrement = fragObjectSizeIncrement;
    }

    public void setEstimatedHeapMB(int estimatedHeapMB) {
        config.estimatedHeapMB = estimatedHeapMB;
    }

    public void setFragStoreBucketCount(int fragStoreBucketCount) {
        config.fragStoreBucketCount = fragStoreBucketCount;
    }

    public void setNumBytesPerChurningByteArray(int numBytesPerChurningByteArray) {
        config.numBytesPerChurningByteArray = numBytesPerChurningByteArray;
    }

    public void setVerbose(boolean verbose) {
        config.verbose = verbose;
    }

    public void setHeapMBtoSitOn(int heapMBtoSitOn) {
        config.heapMBtoSitOn = heapMBtoSitOn;
    }

    class SitOnSomeHeap {
        Object addObject(ArrayList<Object> list, Object prevObj) {
            Object [] o = new Object[1];
            // o[0] = prevObj;    // This, for some reason, seems to SEGV ParallelGC.
            list.add(o);
            return o;
        }

        // Compute the actual object footprint, in number of objects per MB
        long calculateObjectCountPerMB() {
            ArrayList<Object> list = new ArrayList<Object>(1024);
            Object prevObj = null;
            long estimateObjCount = (64 * MB / 48); // roughly 64MB if per-object footprint is 48 bytes

            System.gc();
            long initialUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
            for (long i = 0; i < estimateObjCount; i++) {
                prevObj = addObject(list, prevObj);
            }

            long bytesUsed = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() - initialUsage;

            double bytesPerObject = ((double) bytesUsed)/estimateObjCount;
            return (long)(MB / bytesPerObject);
        }

        public List [] sitOnSomeHeap(int heapMBtoSitOn, boolean verbose) {
            List [] lists = new List[heapMBtoSitOn];
            long objCountPerMB = calculateObjectCountPerMB();

            if (verbose) {
                System.out.println("\t[SitOnSomeHeap: Calculated per-object footprint is " + MB/objCountPerMB + " bytes]");
                System.out.println("\t[SitOnSomeHeap: So we'll allocate a total of " + heapMBtoSitOn * objCountPerMB + " objects]");
            }

            for (int i = 0; i < heapMBtoSitOn; i++) {
                Object prevObj = null;
                // fill up a MB worth of contents in lists array slot.
                ArrayList<Object> list = new ArrayList<Object>(1024);
                lists[i] = list;
                for (int j = 0; j < objCountPerMB; j++) {
                    prevObj = addObject(list, prevObj);
                }
            }
            return lists;
        }
    }

    class PauseDetector extends Thread {
        long interval;
        long threshold;
        long lastSleepTime;
        boolean doRun;

        PauseDetector(long interval, long threshold) {
            this.interval = interval;
            this.threshold = threshold;
            doRun = true;
        }

        public void terminate() {
            doRun = false;
        }

        public void run() {
            lastSleepTime = System.currentTimeMillis();
            while (doRun) {
                long currTime = System.currentTimeMillis();
                if (currTime - lastSleepTime > threshold) {
                    System.err.println("\n*** PauseDetector detected a " +
                            (currTime - lastSleepTime) + " ms pause at " +
                            new Date() + " ***\n");
                }
                lastSleepTime = currTime;
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                    System.err.println("(No reason why I should be woken...)");
                }
            }
        }
    }

    class Yielder {
        long workPerSec;
        long yieldMillis;
        long maxCreditMs;
        long yieldsBetweenReports;
        long workCredit = 0;
        long yieldCount = 0;
        long lastYieldTimeMs;

        Yielder(long workPerSec, long yieldMillis, long maxCreditMs, long yieldsBetweenReports) {
            this.workPerSec = workPerSec;
            this.yieldMillis = yieldMillis;
            this.maxCreditMs = maxCreditMs;
            this.yieldsBetweenReports = yieldsBetweenReports;
            this.lastYieldTimeMs = System.currentTimeMillis();
        }

        public void yieldIfNeeded(int workDone) throws InterruptedException {
            workCredit -= workDone;
            while (workCredit <= 0) {
                Thread.sleep(yieldMillis);

                //Figure out work credit accumulated in yield:
                long currentTimeMs = System.currentTimeMillis();
                long timeCreditMs = currentTimeMs - lastYieldTimeMs;
                lastYieldTimeMs = currentTimeMs;
                if (timeCreditMs > maxCreditMs) timeCreditMs = maxCreditMs; // Cap accumulated credit;
                workCredit += (timeCreditMs * workPerSec) / 1000;

                if ((yieldCount++ % yieldsBetweenReports) == 0) {
                    if (config.verbose) System.out.print(".");
                }
            }
        }
    }

    class PassStore {
        ArrayList<List<Object>> passStore;
        AtomicLong count = new AtomicLong(0);

        PassStore() {
            passStore = new ArrayList<List<Object>>(config.fragStoreBucketCount);
            for (int i = 0; i < config.fragStoreBucketCount; i++) {
                passStore.add(i, Collections.synchronizedList(new ArrayList<Object>(1024)));
            }
        }

        void add(Object o) {
            // does not need synchronization, since individual buckets are made up of synchronized lists...
             passStore.get((int) (count.getAndIncrement() % config.fragStoreBucketCount)).add(o);
        }

        synchronized void prune() throws InterruptedException {
            // Prune PassStore down by prune ratio, keeping every N'th element in each bucket.
            Yielder yielder = new Yielder(config.pruneOpsPerSec, config.yielderMillis,
                    config.maxYieldCreditMs, config.yieldCountBetweenReports);
            for (int b = 0; b < config.fragStoreBucketCount; b++) {
                List<Object> survivorList = new LinkedList<Object>();
                ListIterator iter = passStore.get(b).listIterator();
                for (int i = 0; iter.hasNext(); i++) {
                    if ((i % config.pruneRatio) == 0) {
                        survivorList.add(iter.next());
                        yielder.yieldIfNeeded(1);
                    } else {
                        iter.next();
                    }
                }
                passStore.set(b, survivorList);
            }
        }
    }

    class MakeFrags {
        int storePassCount = 0;
        int fragObjectSize;
        PassStore[] passStores;
        volatile Object tempObj;

        MakeFrags() {
            fragObjectSize = config.initialFragObjectSize;
            passStores = new PassStore[config.numStorePasses];
            for (int i = 0; i < config.numStorePasses; i++) {
                passStores[i] = new PassStore();
            }
        }

        // Make a bunch of small objects
        public void doPass() throws InterruptedException {
            long targetObjCount =
                    (long) (config.estimatedHeapMB * MB * config.maxPassHeapFraction) / (long) fragObjectSize;
            int arrayLength = fragObjectSize - config.estimatedArrayOverheadInBytes;
            Yielder yielder = new Yielder(config.allocMBsPerSec * MB,
                    config.yielderMillis,
                    config.maxYieldCreditMs,
                    config.yieldCountBetweenReports);

            if (config.verbose)
                System.out.println("\nPass #" + storePassCount + ": Making " +
                        targetObjCount + " Objects of size " + fragObjectSize);
            // Create a whole bunch of small strings, to fill up MaxPassHeapFraction:
            for (int i = 0; i < targetObjCount; i++) {
                passStores[storePassCount].add(new byte[arrayLength]);
                yielder.yieldIfNeeded(fragObjectSize);
            }

            // Now churn the heap so that all this stuff will get into OldGen
            if (config.verbose) {
                System.out.println("\nChurning heap by making " +
                        (long) (config.genChurnHeapFraction * config.estimatedHeapMB) +
                        " MB made of byte arrays of " + config.numBytesPerChurningByteArray +
                        " bytes each.");
            }

            for (long bytesToAllocate = (long) (config.genChurnHeapFraction * config.estimatedHeapMB) * MB;
                 bytesToAllocate >= 0;
                 bytesToAllocate -= (config.numBytesPerChurningByteArray + config.estimatedArrayOverheadInBytes)) {
                tempObj = new int[config.numBytesPerChurningByteArray];
                yielder.yieldIfNeeded(config.numBytesPerChurningByteArray + config.estimatedArrayOverheadInBytes);
            }

            if (config.verbose) System.out.println("\nPruning frag pass by prune ratio " + config.pruneRatio);
            // Now prune the pass's Store to frag OldGen, to leave a fragmented set of objects of the current size
            passStores[storePassCount].prune();
        }

        public void frag() throws InterruptedException {
            if (config.verbose) System.out.println("Frag: Estimated Heap " + config.estimatedHeapMB + " MB");

            for (storePassCount = 0; storePassCount < config.numStorePasses; storePassCount++) {
                // Generate a fragmented set of objects of the current size in OldGen
                doPass();
                // Grow the object size by a fixed multiple and increment:
                fragObjectSize *= config.fragObjectSizeMultiplier;
                fragObjectSize += config.fragObjectSizeIncrement;
            }
        }
    }


    public Fragger(int numIterations) {
        setNumIterations(numIterations);
    }

    public Fragger() {
    }

    private Fragger(String[] args) {
        config.parseArgs(args);
    }

    public void terminate() {
        this.interrupt();
    }

    Object heapStuffRoot;

    public void run() {
        SitOnSomeHeap sitOnHeap = new SitOnSomeHeap();

        if (config.heapMBtoSitOn > 0) {
            if(config.verbose) {
                System.out.println("XXX Creating " + config.heapMBtoSitOn + "MB of Hep to sit on...");
            }
            heapStuffRoot = sitOnHeap.sitOnSomeHeap(config.heapMBtoSitOn, config.verbose);
        }

        PauseDetector pauseDetector = new PauseDetector(config.yielderMillis, config.pauseThresholdMs);
        try {
            pauseDetector.start();
            for (int i = 0; (i < config.numIterations) || (config.numIterations <= 0); i++) {
                MakeFrags f = new MakeFrags();
                if (config.verbose) System.out.println("\nStarting a MakeFrag super-pass " + i + " ...");
                f.frag();
            }
            if (config.verbose) System.out.println("\nfragger Done...");
        } catch (InterruptedException e) {
            if (config.verbose) System.out.println("fragger interrupted/terminated");
        }
        pauseDetector.terminate();
    }

    public class JarLoader extends ClassLoader {
        public Class getJarMainClass(String jarName) throws ClassNotFoundException {
            try {
                URL jarUrl = new URL("jar:file:" + jarName + "!/");
                JarURLConnection urlConnect = (JarURLConnection) jarUrl.openConnection();
                Attributes mainAttr = urlConnect.getMainAttributes();
                if (mainAttr != null) {
                    return loadClass(mainAttr.getValue(Attributes.Name.MAIN_CLASS));
                }
                System.out.println("Manifest for " + jarName + " does not have a Main attribute");
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL for jar" + jarName);
            } catch (IOException e) {
                System.out.println("IOException while Loading " + jarName);
            }
            return null;
        }
    }

    public void doExec() {
        Method mainMethod;
        Class execClass;

        try {
            if(config.execClassName.endsWith(".jar")) {
                JarLoader jarLoader = new JarLoader();
                execClass = jarLoader.getJarMainClass(config.execClassName);
            } else {
                execClass = Class.forName(config.execClassName);
            }

            try {
                mainMethod = execClass.getDeclaredMethod("main", new Class[]{String[].class});
                mainMethod.setAccessible(true);

                try {
                    if (config.verbose) {
                        System.out.print("Exec'ing: " + config.execClassName);
                        for (int i = 0; i < config.execArgs.length; i++) {
                            System.out.print(" " + config.execArgs[i]);
                        }
                        System.out.println();
                    }
                    mainMethod.invoke(null, new Object[]{config.execArgs});
                } catch (IllegalAccessException e) {
                    System.out.println(e);
                } catch (InvocationTargetException e) {
                    System.out.println(e);
                }
            } catch (NoSuchMethodException e) {
                System.out.println("Class " + config.execClassName + "has no main() method");
                System.out.println(e);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Class " + config.execClassName + " not found.");
            System.out.println(e);
        }

        // Exec'd Class's main() is done, terminate fragger
        terminate();
    }

    public static void main(String[] args) {
        Fragger frg = new Fragger(args);

        if (frg.config.verbose) {
            System.out.print("Executing: fragger");

            for (String arg : args) {
                System.out.print(" " + arg);
            }
            System.out.println("");
        }

        frg.start();

        // If given another Class and arguments to execute, do so, terminating when it is done.
        if (frg.config.execClassName != null) {
            frg.doExec();
        }

        try {
            frg.join();
        } catch (InterruptedException e) {
            if (frg.config.verbose) System.out.println("fragger main() interrupted");
        }
        // (if you wanted fragger to terminate early, call fragger.terminate() )...
    }
}