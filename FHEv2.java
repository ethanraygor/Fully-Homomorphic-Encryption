import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class FHEv2 {
    public static void main(String args[]){
        switch(args[0]){
            case "-k":
                if(args.length!=5){
                    printUsage();
                    return;
                }
                generateKeys(args);
                break;
            case "-p":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                String p = padding(args);
                System.out.println(p);
                break;
            case "-e":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                String e = encrypt(args);
                System.out.println(e);
                break;
                case "-d":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                String m = decrypt(args);
                System.out.println(m);
                break;
            case "-b":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                both(args);
                break;
            case "-a":
                if(args.length<4 || args.length>6){
                    printUsage();
                    return;
                }
                String a = add(args);
                System.out.println(a);
                break;
            case "-m":
                if(args.length<4 || args.length>6){
                    printUsage();
                    return;
                }
                String mu = multiply(args);
                System.out.println(mu);
                break;
            default:
                printUsage();
                return;
        }
    }

    private static String multiply(String[] args) {
        String cipherFinal = "error";
        String message1 = "";
        String cMessage1 = "";
        String message2 = "";
        String cMessage2 = "";
        String keyFileName;
        String[] eargs = new String[3];
        File file;
        Scanner reader;
        BigInteger N;
        BigInteger e1;
        BigInteger e2;
        BigInteger eFinal;

        //parse args
        try{
            if(args[1].equals("-e")){
                message1 = args[2];
                if(args[3].equals("-e")){
                    message2 = args[4];
                    keyFileName = args[5];
                }else{
                    cMessage2 = args[3];
                    keyFileName = args[4];
                }
            }else{
                cMessage1 = args[1];
                if(args[2].equals("-e")){
                    message2 = args[3];
                    keyFileName = args[4];
                }else{
                    cMessage2 = args[2];
                    keyFileName = args[3];
                }
            }
        }catch (Exception e){
            printUsage();
            return "error";
        }

        //encrypt if needed
        eargs[0] = "-e";
        eargs[2] = keyFileName;
        if(message1!=""){
            eargs[1] = message1;
            cMessage1 = encrypt(eargs);
        }
        if(message2!=""){
            eargs[1] = message2;
            cMessage2 = encrypt(eargs);
        }

        //read file
        try{
            file = new File(keyFileName);
            reader = new Scanner(file);
            reader.nextLine();
            N = new BigInteger(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }

        //add
        e1 = new BigInteger(cMessage1);
        e2 = new BigInteger(cMessage2);
        eFinal = e1.multiply(e2);
        eFinal = eFinal.mod(N);
        cipherFinal = eFinal.toString();

        return cipherFinal;
    }

    private static String add(String[] args) {
        String cipherFinal = "error";
        String message1 = "";
        String cMessage1 = "";
        String message2 = "";
        String cMessage2 = "";
        String keyFileName;
        String[] eargs = new String[3];
        File file;
        Scanner reader;
        BigInteger N;
        BigInteger e1;
        BigInteger e2;
        BigInteger eFinal;

        //parse args
        try{
            if(args[1].equals("-e")){
                message1 = args[2];
                if(args[3].equals("-e")){
                    message2 = args[4];
                    keyFileName = args[5];
                }else{
                    cMessage2 = args[3];
                    keyFileName = args[4];
                }
            }else{
                cMessage1 = args[1];
                if(args[2].equals("-e")){
                    message2 = args[3];
                    keyFileName = args[4];
                }else{
                    cMessage2 = args[2];
                    keyFileName = args[3];
                }
            }
        }catch (Exception e){
            printUsage();
            return "error";
        }

        //encrypt if needed
        eargs[0] = "-e";
        eargs[2] = keyFileName;
        if(message1!=""){
            eargs[1] = message1;
            cMessage1 = encrypt(eargs);
        }
        if(message2!=""){
            eargs[1] = message2;
            cMessage2 = encrypt(eargs);
        }

        //read file
        try{
            file = new File(keyFileName);
            reader = new Scanner(file);
            reader.nextLine();
            N = new BigInteger(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }

        //add
        e1 = new BigInteger(cMessage1);
        e2 = new BigInteger(cMessage2);
        eFinal = e1.add(e2);
        eFinal = eFinal.mod(N);
        cipherFinal = eFinal.toString();

        return cipherFinal;
    }

    private static void both(String[] args) {
        String message;
        String keyFileName;
        String[] eargs = new String[3];
        String e;
        String[] dargs = new String[3];
        String d;

        message = args[1];
        keyFileName = args[2];

        System.out.println(message);
        eargs[0] = "-e";
        eargs[2] = message;
        eargs[3] = keyFileName;
        e = encrypt(eargs);
        System.out.println(e);
        dargs[0] = "-d";
        dargs[1] = e;
        dargs[2] = keyFileName;
        d = decrypt(dargs);
        System.out.println(d);

    }

    private static String decrypt(String[] args) {
        String cipher;
        String keyFileName;
        String message = "error";
        File file;
        Scanner reader;
        BigInteger P1;
        int w;
        BigInteger W2;
        BigInteger e;
        BigInteger m;

        
        //parse args
        cipher = args[1];
        if(cipher==null || cipher==""){
            printUsage();
            return "error";
        }
        keyFileName = args[2];
        if(keyFileName==null || keyFileName==""){
            printUsage();
            return "error";
        }

        //read file
        try{
            file = new File(keyFileName);
            reader = new Scanner(file);
            P1 = new BigInteger(reader.nextLine());
            reader.nextLine();
            w = Integer.parseInt(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }
        W2 = new BigInteger("2");
        W2 = W2.pow(w);

        //decrypt
        e = new BigInteger(cipher);
        m = e.mod(P1);
        m = m.mod(W2);
        message = m.toString();

        return message;
    }

    private static String encrypt(String[] args) {
        String message;
        String keyFileName;
        String cipher = "error";
        File file;
        Scanner reader;
        BigInteger P1;
        BigInteger N;
        Random rand;
        String[] pargs = new String[3];
        String padded;
        BigInteger p;
        BigInteger r;
        BigInteger e;

        //parse args
        message = args[1];
        if(message==null || message==""){
            printUsage();
            return "error";
        }
        keyFileName = args[2];
        if(keyFileName==null || keyFileName==""){
            printUsage();
            return "error";
        }

        //read file
        try{
            file = new File(keyFileName);
            reader = new Scanner(file);
            P1 = new BigInteger(reader.nextLine());
            N = new BigInteger(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }

        //encrypt
        rand = new Random();
        pargs[0] = "-p";
        pargs[1] = message;
        pargs[2] = keyFileName;
        padded = padding(pargs);
        p = new BigInteger(padded);
        r = new BigInteger(P1.bitCount(), rand);
        e = r.multiply(P1);
        e = e.add(p);
        e = e.mod(N);
        cipher = e.toString();

        return cipher;
    }

    private static String padding(String[] args) {
        String message;
        String keyFileName;
        String padded = "error";
        File file;
        Scanner reader;
        Random rand = new Random();
        BigInteger m;
        int w;
        int z;
        BigInteger R;
        BigInteger W2;
        BigInteger tmp;

        //parse args
        message = args[1];
        keyFileName = args[2];

        //read file
        try{
            file = new File(keyFileName);
            reader = new Scanner(file);
            reader.nextLine();
            reader.nextLine();
            w = Integer.parseInt(reader.nextLine());
            z = Integer.parseInt(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }

        //padding
        m = new BigInteger(message);
        R = new BigInteger(z, rand);
        W2 = new BigInteger("2");
        W2 = W2.pow(w);
        tmp = R.multiply(W2);
        m = m.add(tmp);
        padded = m.toString();

        return padded;
    }

    private static void generateKeys(String[] args) {
        int keySize;
        int w;
        int z;
        String keyFileName;
        Random r = new Random();
        BigInteger P1;
        BigInteger P2;
        BigInteger N;
        int pBits;
        File file;
        FileWriter writer;
        
        //parse args
        try{
            keySize = Integer.parseInt(args[1]);
        }catch(Exception e){
            System.out.println("Second arguement b=must be an integer");
            printUsage();
            return;
        }
        if(keySize<1){
            printUsage();
            return;
        }
        w = Integer.parseInt(args[2]);
        z = Integer.parseInt(args[3]);
        keyFileName = args[4];
        if(keyFileName==null || keyFileName==""){
            printUsage();
            return;
        }

        //generate keys
        pBits = (keySize+1)*(w+z);
        P1 = BigInteger.probablePrime(pBits, r);
        P2 = BigInteger.probablePrime(keySize, r);
        /*
        Q = P1.multiply(new BigInteger("2"));
        Q = P1.add(new BigInteger("1"));
        while(!Q.isProbablePrime(1)){
            P1 = BigInteger.probablePrime(keySize, r);
            Q = P1.multiply(new BigInteger("2"));
            Q = P1.add(new BigInteger("1"));
        }
        */
        N = P1.multiply(P2);
        /*
        P3 = BigInteger.probablePrime(keySize, r);
        T = Q.multiply(P3);
        hBits = T.bitCount();
        h1 = new BigInteger(hBits, r);
        h2 = new BigInteger(hBits, r);
        tmp = P3.subtract(new BigInteger("1"));
        tmp = tmp.multiply(new BigInteger("2"));
        g1 = h1.modPow(tmp, T);
        g2 = h2.modPow(tmp, T);
        */
        



        //create file
        try{
            file = new File(keyFileName);
            file.createNewFile();
            writer = new FileWriter(file);
            writer.write(P1.toString()+"\n");
            writer.write(N.toString()+"\n");
            writer.write(w+"\n");
            writer.write(z+"\n");
            writer.close();
        }catch(Exception e){
            System.out.println("Error writing to "+keyFileName+"\n");
            printUsage();
            return;
        }
        
    }

    private static void printUsage(){
        System.out.println("See README for Usage");
    }
}
