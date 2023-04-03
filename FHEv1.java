import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class FHEv1{
    public static void main(String[] args){
        //parse args
        if(args.length<3 || args.length>6){
            printUsage();
            return;
        }
        switch(args[0]){
            case "-k":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                generateKeys(args);
                break;
            case "-e":
                if(args.length!=3){
                    printUsage();
                    return;
                }
                String c = encrypt(args);
                System.out.println(c);
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
            case "-t":
                if(args.length<4 || args.length>6){
                    printUsage();
                    return;
                }
                boolean e = equality(args);
                System.out.println(e);
                break;
            default:
                printUsage();
        }
        
    }

    private static void generateKeys(String[] args){
        int keySize;
        String keyFileName;
        Random r = new Random();
        BigInteger P1;
        BigInteger P2;
        BigInteger P3;
        BigInteger Q;
        BigInteger N;
        BigInteger g1;
        BigInteger g2;
        BigInteger T;
        int hBits;
        BigInteger h1;
        BigInteger h2;
        BigInteger tmp;
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
        keyFileName = args[2];
        if(keyFileName==null || keyFileName==""){
            printUsage();
            return;
        }

        //generate keys
        P1 = BigInteger.probablePrime(keySize, r);
        P2 = BigInteger.probablePrime(keySize, r);
        Q = P1.multiply(new BigInteger("2"));
        Q = P1.add(new BigInteger("1"));
        /*while(!Q.isProbablePrime(1)){
            P1 = BigInteger.probablePrime(keySize, r);
            Q = P1.multiply(new BigInteger("2"));
            Q = P1.add(new BigInteger("1"));
        }*/
        N = P1.multiply(P2);
        P3 = BigInteger.probablePrime(keySize, r);
        T = Q.multiply(P3);
        hBits = T.bitCount();
        h1 = new BigInteger(hBits, r);
        h2 = new BigInteger(hBits, r);
        tmp = P3.subtract(new BigInteger("1"));
        tmp = tmp.multiply(new BigInteger("2"));
        g1 = h1.modPow(tmp, T);
        g2 = h2.modPow(tmp, T);
        



        //create file
        try{
            file = new File(keyFileName);
            file.createNewFile();
            writer = new FileWriter(file);
            writer.write(P1.toString()+"\n");
            writer.write(N.toString()+"\n");
            writer.write(g1.toString()+"\n");
            writer.write(g2.toString()+"\n");
            writer.write(T.toString()+"\n");
            writer.close();
        }catch(Exception e){
            System.out.println("Error writing to "+keyFileName+"\n");
            printUsage();
            return;
        }
        
    }

    private static String encrypt(String[] args){
        String message;
        String keyFileName;
        String cipher = "error";
        File file;
        Scanner reader;
        BigInteger P1;
        BigInteger N;
        Random rand;
        BigInteger m;
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
        m = new BigInteger(message);
        r = new BigInteger(P1.bitCount(), rand);
        e = r.multiply(P1);
        e = e.add(m);
        e = e.mod(N);
        cipher = e.toString();

        return cipher;
    }

    private static String decrypt(String[] args){
        String cipher;
        String keyFileName;
        String message = "error";
        File file;
        Scanner reader;
        BigInteger P1;
        BigInteger N;
        Random rand;
        BigInteger e;
        BigInteger r;
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
            N = new BigInteger(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return "error";
        }
        rand = new Random();

        //decrypt
        e = new BigInteger(cipher);
        r = new BigInteger(P1.bitCount(), rand);
        m = P1.multiply(r);
        m = m.add(e);
        m = m.mod(N);
        m = m.mod(P1);
        message = m.toString();

        return message;
    }

    private static void both(String[] args){
        String message;
        String keyFileName;
        String[] cipherArgs = new String[3];
        String cipher;
        String message2;

        //parse args
        message = args[1];
        if(message==null || message==""){
            printUsage();
            return;
        }
        keyFileName = args[2];
        if(keyFileName==null || keyFileName==""){
            printUsage();
            return;
        }

        //og message
        System.out.println(message);
        //encrypt
        cipher = encrypt(args);
        cipherArgs[0] = "-d";
        cipherArgs[1] = cipher;
        cipherArgs[2] = keyFileName;
        System.out.println(cipher);
        //decrypt
        message2 = decrypt(cipherArgs);
        System.out.println(message2);
    }

    private static String add(String[] args){
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

    private static String multiply(String[] args){
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

    private static boolean equality(String[] args){
        String message1 = "";
        String cMessage1 = "";
        String message2 = "";
        String cMessage2 = "";
        String keyFileName;
        String[] eargs = new String[3];
        File file;
        Scanner reader;
        BigInteger g1;
        BigInteger g2;
        BigInteger T;
        BigInteger e1;
        BigInteger e2;
        BigInteger tmp;

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
            return false;
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
            reader.nextLine();
            g1 = new BigInteger(reader.nextLine());
            g2 = new BigInteger(reader.nextLine());
            T = new BigInteger(reader.nextLine());
            reader.close();
        }catch(Exception ex){
            System.out.println("Error reading "+keyFileName+"\n");
            printUsage();
            return false;
        }

        //test equality
        e1 = new BigInteger(cMessage1);
        e2 = new BigInteger(cMessage2);
        tmp = e1.subtract(e2);
        tmp = tmp.abs();
        g1 = g1.modPow(tmp, T);
        if(g1.intValue()!=1){
            return false;
        }
        g2 = g2.modPow(tmp, T);
        if(g2.intValue()!=1){
            return false;
        }

        return true;
    }

    private static void printUsage(){
        System.out.println("See README for Usage");
    }
}