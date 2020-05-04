package sim;
import java.util.logging.Logger;
import sim.bimodal;
import sim.gshare;
import sim.hybrid;


public class Predictor {
	Logger logger;
	bimodal bim;
	gshare gsh;
	hybrid hyb;
	String name;
	String[] args;
	
	public Predictor(String[] args, Logger logger) {
		name = args[1];
		this.args = args;
		this.logger = logger;
		
		switch(name) {
		case "bimodal":
			bim = new bimodal(args, logger);
        	break;
		case "gshare":
			gsh = new gshare(args, logger);
        	break;
		case "hybrid":
			bim = new bimodal(new String[] {args[0], args[1], args[5]}, logger);
			gsh = new gshare(new String[] {args[0], args[1], args[3], args[4]}, logger);
			hyb = new hybrid(args, logger);
        	break;
		}
    }
	
	public void predict(String[] arrStr) {
		int index, cidx;
    	boolean taken=false;
    	boolean pred;
    	if(arrStr[1].equals("t"))
    		taken=true;

		switch(name) {
		case "bimodal":
        	index = bim.get_index(arrStr[0], bim.get_bhr());
        	if(logger != null) {
	        	logger.severe(String.format("	PT index:	%d", index));
	        	logger.severe(String.format("	PT value:	%d", bim.get_bc(index)));
        	}
        	
        	pred = bim.predict(index, true);
        	bim.set_bc(taken, index);
        	if(taken != pred)	bim.mispred++;
        	
        	if(logger != null) 
        		logger.severe(String.format("	New PT value:	%s", bim.get_bc(index)));
    		bim.cnt++;
        	break;
		case "gshare":
        	index = gsh.get_index(arrStr[0], gsh.get_bhr());
        	if(logger != null) {
	        	logger.severe(String.format("	PT index:	%d", index));
	        	logger.severe(String.format("	PT value:	%d", gsh.get_bc(index)));
        	}
        	
        	pred = gsh.predict(index, true);
        	gsh.set_bc(taken, index);
        	if(taken != pred) gsh.mispred++;
        	
        	if(logger != null) 
        		logger.severe(String.format("	New PT value:	%s", gsh.get_bc(index)));
        	gsh.set_bhr(taken);
        	gsh.cnt++;
			break;
		case "hybrid":
			String bin_addr = hyb.hex_to_bin(arrStr[0]);
        	cidx = hyb.get_branch_index(bin_addr);
        	
        	int bim_index, gsh_index;
        	String chosen = null;
        	
        	bim_index = bim.get_index(arrStr[0], bim.get_bhr());
        	if(logger != null) {
	        	logger.severe(String.format("	bimodal-PT index:	%d", bim_index));
	        	logger.severe(String.format("	bimodal-PT value:	%d", bim.get_bc(bim_index)));
        	}
        	boolean bim_pred = bim.predict(bim_index, false);

        	gsh_index = gsh.get_index(arrStr[0], gsh.get_bhr());
        	if(logger != null) {
	        	logger.severe(String.format("	gshare-PT index:	%d", gsh_index));
	        	logger.severe(String.format("	gshare-PT value:	%d", gsh.get_bc(gsh_index)));
        	}
        	boolean gsh_pred = gsh.predict(gsh_index, false);

        	// choose predictor
        	if(hyb.ct[cidx] >= 2) {
        		pred = gsh_pred;
        		chosen = "gshare";
        	}
        	else {
        		pred = bim_pred;
        		chosen = "bimodal";
        	}
        	if(logger != null)
        		logger.severe(String.format("	Prediction:	%s", Boolean.toString(pred)));

        	// update ct
        	if(gsh_pred != bim_pred) {
	        	if(gsh_pred == taken) {
	        		if(hyb.ct[cidx] < 3)
	        			hyb.ct[cidx]++;
	        	}
	        	else {
	        		if(hyb.ct[cidx] > 0)
	        			hyb.ct[cidx]--;
	        	}
        	}
        	
        	if(taken != pred) hyb.mispred++;
        	
    		if(chosen.equals("bimodal"))	{
    			bim.set_bc(taken, bim_index);
        		if(logger != null)
        			logger.severe(String.format("	New %s-PT value:	%s", chosen, bim.get_bc(bim_index)));
    		}
    		else {
    			gsh.set_bc(taken, gsh_index);
        		if(logger != null)
        			logger.severe(String.format("	New %s-PT value:	%s", chosen, gsh.get_bc(gsh_index)));
    		}
    		gsh.set_bhr(taken);
        	if(logger != null)
        		logger.severe(String.format("	New CT value:	%d", hyb.ct[cidx]));
        	hyb.cnt++;
			break;
		}
	}
	
	public String toString() {
		String ret = null;
		switch(name) {
		case "bimodal":
			ret = bim.toString(true);
			break;
		case "gshare":
			ret = gsh.toString(true);
			break;
		case "hybrid":
			ret = hyb.toString();
			ret += gsh.toString(false);
			ret += bim.toString(false);
			break;
		}
		return ret;
	}
}
