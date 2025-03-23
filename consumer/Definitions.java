public interface Definitions {

    public static final boolean tracing = false;

    // Maximum array lengths:
    public static final int maxFqnLength = 100;
    public static final int maxNameLength = 100;
    public static final int maxStaticsLength = 65535;
    public static final int maxPushPopArg = 100;

    // Record types:
    public static final int rtypeLength = 8;
    public static final String rtypeBeginFrame = "FRMBEG";
    public static final String rtypeEndFrame = "FRMEND";
    public static final String rtypeI64Change = "CHGI64";
    public static final String rtypeF64Change = "CHGF64";
    public static final String rtypeStatics = "STATICS";
    public static final String rtypeOpCode = "OPCODE";
    public static final String rtypePush = "PUSH";
    public static final String rtypePop = "POP";
    public static final String rtypeGfuncCall = "GCALL";
    public static final String rtypeGfuncReturn = "GRETURN";

    // Field types:
    public static final int ftypeLocal = 1;
    public static final int ftypeInstance = 2;
    public static final int ftypeStatic = 3;

    // Maximums for controlling analysis behaviour:
    public static final int maxDisplaySamples = 20;

}
