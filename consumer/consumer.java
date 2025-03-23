import java.io.*;
import java.util.*;

public class consumer implements Definitions {

    private static void showHelp() {
        System.out.println("Usage:\tjava  consumer  <count>  <data path>\n\twhere count is at least 10");
        System.exit(1);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            showHelp();
        }
        int maxValueChanges = Integer.parseInt(args[0]);
        if (maxValueChanges < 10) {
            showHelp();
        }
        String pathData = args[1];
        System.out.printf("consumer: Processing file %s\n", pathData);
        consumer(maxValueChanges, pathData);
    }

    // Implement the analysis method to read the binary file and load into a tree
    public static void consumer(int maxValueChanges, String pathData) throws IOException {
         boolean eofFlag = false;
        int recordNumber = 0;

        // Open the data file
        DataInputStream dataFile = new DataInputStream(new FileInputStream(pathData));

        // Initialise the B-tree.
        TreeMap<Integer, BtreeLeaf> bTree = new TreeMap<>();

        // Read every record from the data file
        byte[] rtypeBuffer = new byte[rtypeLength];
        while (true) {
            // Read record prefix
            RecordPrefix recordPrefix = new RecordPrefix();
            try {
                dataFile.readFully(rtypeBuffer, 0, rtypeLength);
                recordPrefix.rtype = new String(rtypeBuffer);
                recordPrefix.rtype = recordPrefix.rtype.trim();
                recordPrefix.counter = dataFile.readInt();
                recordPrefix.payloadSize = dataFile.readInt();
            } catch (EOFException e) {
                eofFlag = true;  // End of file reached
            }

            // If EOF, break out of the loop
            if (eofFlag) break;

            // Retrieve the record prefix
            if (tracing) {
                System.out.printf("consumer tracing: Read recordNumber=%d, prefix Rtype=%s, Counter=%d, PayloadSize=%d\n",
                        recordNumber, recordPrefix.rtype, recordPrefix.counter, recordPrefix.payloadSize);
            }

            // Read the payload based on the record type
            switch (recordPrefix.rtype) {
                case rtypeI64Change:
                    PayloadI64Change ri64chg = new PayloadI64Change();
                    ri64chg.fieldType = dataFile.readShort();
                    ri64chg.valueOld = dataFile.readLong();
                    ri64chg.valueNew = dataFile.readLong();
                    ri64chg.index = dataFile.readLong();
                    ri64chg.nameSize = dataFile.readShort();
                    dataFile.readFully(ri64chg.nameBytes);
                    bTree.put(recordNumber, new BtreeLeaf(recordNumber, new BtreeLeafValue (recordPrefix, ri64chg)));
                    break;
                case rtypeF64Change:
                    PayloadF64Change rf64chg = new PayloadF64Change();
                    rf64chg.fieldType = dataFile.readShort();
                    rf64chg.valueOld = dataFile.readDouble();
                    rf64chg.valueNew = dataFile.readDouble();
                    rf64chg.index = dataFile.readLong();
                    rf64chg.nameSize = dataFile.readShort();
                    dataFile.readFully(rf64chg.nameBytes);
                    bTree.put(recordNumber, new BtreeLeaf(recordNumber, new BtreeLeafValue (recordPrefix, rf64chg)));
                    break;
                case rtypeBeginFrame:
                    PayloadBeginFrame rbfr = new PayloadBeginFrame();
                    rbfr.fqnSize = dataFile.readShort();
                    dataFile.readFully(rbfr.fqnBytes);
                    bTree.put(recordNumber, new BtreeLeaf(recordNumber, new BtreeLeafValue (recordPrefix, rbfr)));
                    break;
                case rtypeEndFrame:
                    PayloadEndFrame refr = new PayloadEndFrame();
                    refr.fqnSize = dataFile.readShort();
                    dataFile.readFully(refr.fqnBytes);
                    bTree.put(recordNumber,
                            new BtreeLeaf(recordNumber, new BtreeLeafValue (recordPrefix, refr))
                            );
                    break;
                default:
                    System.out.printf("consumer *** ERROR: unknown record type: %s, recordNumber=%d\n",
                            recordPrefix.rtype, recordNumber);
                    return;
            }

            recordNumber++;
        }

        System.out.printf("consumer: Loaded %d records into the B-tree\n", recordNumber);

        // Retrieve random records and report their contents
        for (int ix = 0; ix < maxDisplaySamples; ix++) {
            reportData(randomPal(maxValueChanges), bTree);
        }

        // Report the first record and the last record
        reportData(0, bTree);
        reportData(maxValueChanges + 1, bTree);
    }

    // Given a key, report the associated data record.
    public static void reportData(int recordNumber, TreeMap<Integer, BtreeLeaf> loadedTree) {
        BtreeLeaf btreeLeaf = loadedTree.get(recordNumber);
        if (btreeLeaf == null) {
            System.out.printf("consumer reportData *** ERROR: Cannot find index recordNumber: %d\n", recordNumber);
            return;
        }

        // Get record type
        String rtype = new String(btreeLeaf.btvalue.prefix.rtype).trim();

        // Show data
        switch (rtype) {
            case rtypeBeginFrame:
                PayloadBeginFrame rbfr = (PayloadBeginFrame) btreeLeaf.btvalue.payload;
                String fqn = new String(rbfr.fqnBytes, 0, rbfr.fqnSize);
                System.out.printf("consumer reportData: begin frame: Record %d FQN = %s\n", btreeLeaf.btkey, fqn);
                break;
            case rtypeI64Change:
                PayloadI64Change ri64chg = (PayloadI64Change) btreeLeaf.btvalue.payload;
                String fname = ri64chg.nameSize == 0 ? "" : new String(ri64chg.nameBytes, 0, ri64chg.nameSize);
                System.out.printf("consumer reportData: int64 change: Record %d, fname=\"%s\", ftype=%d, old = %d, new = %d\n",
                        btreeLeaf.btkey, fname, ri64chg.fieldType, ri64chg.valueOld, ri64chg.valueNew);
                break;
            case rtypeF64Change:
                PayloadF64Change rf64chg = (PayloadF64Change) btreeLeaf.btvalue.payload;
                fname = rf64chg.nameSize == 0 ? "" : new String(rf64chg.nameBytes, 0, rf64chg.nameSize);
                System.out.printf("consumer reportData: float64 change: Record %d, fname=\"%s\", ftype=%d, old = %f, new = %f\n",
                        btreeLeaf.btkey, fname, rf64chg.fieldType, rf64chg.valueOld, rf64chg.valueNew);
                break;
            case rtypeEndFrame:
                PayloadEndFrame refr = (PayloadEndFrame) btreeLeaf.btvalue.payload;
                fqn = new String(refr.fqnBytes, 0, refr.fqnSize);
                System.out.printf("consumer reportData: end frame: Record %d FQN = %s\n", btreeLeaf.btkey, fqn);
                break;
            default:
                System.out.printf("consumer reportData *** ERROR: unrecognizable record type: Record %d, record type = %s\n", btreeLeaf.btkey, rtype);
        }
    }

    // We don't want a random number in the range of [0, n). We actually want the range = (0, n]
    public static int randomPal(int maxValueChanges) {
        Random rand = new Random();
        int rn;
        do {
            rn = rand.nextInt(maxValueChanges + 1);
        } while (rn <= 0);
        return rn;
    }
}

// BtreeLeaf value.
class BtreeLeafValue {
    RecordPrefix prefix;
    Object payload;

    public BtreeLeafValue(RecordPrefix prefix, Object payload) {
        this.prefix = prefix;
        this.payload = payload;
    }
}

// BtreeLeaf equivalent
class BtreeLeaf /*implements Comparable<BtreeLeaf> */ {
    int btkey;  // Record number
    BtreeLeafValue btvalue;

    public BtreeLeaf(int key, BtreeLeafValue value) {
        this.btkey = key;
        this.btvalue = value;
    }
}

class RecordPrefix implements Definitions {
    String rtype;
    int counter;
    int payloadSize;
}

class PayloadBeginFrame implements Definitions {
    short fqnSize;
    byte[] fqnBytes = new byte[maxFqnLength];
}

class PayloadEndFrame implements Definitions {
    short fqnSize;
    byte[] fqnBytes = new byte[maxFqnLength];
}

class PayloadI64Change implements Definitions {
    short fieldType;
    long valueOld;
    long valueNew;
    long index;
    short nameSize;
    byte[] nameBytes = new byte[maxNameLength];
}

class PayloadF64Change implements Definitions {
    short fieldType;
    double valueOld;
    double valueNew;
    long index;
    short nameSize;
    byte[] nameBytes = new byte[maxNameLength];
}

class PayloadStatics implements Definitions {
    int tblSize;
    byte[] tblBytes = new byte[maxStaticsLength];  // Serialized statics table
}

class PayloadOpCode implements Definitions {
    int pc;
    int opCode;
}

class PayloadPush implements Definitions {
    short pushSize;
    byte[] pushBytes = new byte[maxPushPopArg];  // String-ified push operand, converted to [size]byte
}

class PayloadPop implements Definitions {
    short popSize;
    byte[] popBytes = new byte[maxPushPopArg];  // String-ified push operand, converted to [size]byte
}

// TODO: PayloadGfuncCall, PayloadGfuncReturn.

