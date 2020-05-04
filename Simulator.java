package sim;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.*;
import sim.Predictor;


public class Simulator {
	private static class MyCustomFormatter extends Formatter {
		 
        @Override
        public String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            sb.append(record.getMessage());
            sb.append("\n");
            return sb.toString();
        }
    }
	
    public static void main(String[] args) {
    	args = new String[]{
    			"sim",
    			"hybrid",
    			"8",
    			"14",
    			"10",
    			"5",
    			"D:/Users/mgcha/Dropbox/Studymaterials/Graduate/UCF/Courswork/CDA5106/project/MachineProblem2/traces/gcc_trace.txt"};
    			
    	boolean debug = false;
    	Logger logger = null;
    	if(debug) {
	    	// logger
	    	logger = Logger.getLogger(Simulator.class.getName());
	    	FileHandler fh;
	    	try {
	    		fh = new FileHandler("dbg_hybrid_1.txt");
	    		logger.addHandler(fh);
	    		fh.setFormatter(new MyCustomFormatter());
	    		logger.setLevel(Level.SEVERE);
	    	} catch (SecurityException e) {
	    		e.printStackTrace();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
    	}
    	
    	String fpath = args[args.length-1];
    	String message = "./";
    	for(int i=0; i<args.length-1; i++)
    		message += args[i] + ' ';
    	message += args[args.length-1];
    	
    	if(debug) {
	    	logger.severe("COMMAND");
	    	logger.severe(message);
    	}
    	else {
    		System.out.println(message);
    	}
    	
    	try {
        	File f = new File(fpath);
			BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";

            Predictor predictor = new Predictor(args, logger);
            
            int lineNum = 0;
            while ((readLine = b.readLine()) != null) {
            	if(lineNum >= 100000) break;
            	
	        	String[] arrStr = readLine.split(" ", 2);
	        	if(debug)
	        		logger.severe(String.format("\n<Line #%d>	%s	%s", lineNum++, arrStr[0], arrStr[1]));
            	predictor.predict(arrStr);
            }
            if(debug)
            	logger.severe(String.format("%s", predictor.toString()));
            else
            	System.out.println(String.format("%s", predictor.toString()));
    	}
    	catch (IOException e) {
            e.printStackTrace();
        }
    }
}
