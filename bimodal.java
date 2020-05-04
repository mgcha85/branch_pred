package sim;

import java.util.logging.Logger;

public class bimodal extends Util {
	private Logger logger;

	public bimodal(String[] args, Logger logger) {
		super(logger);
		this.logger = logger;

		name = args[1];
		m = Integer.parseInt(args[2]);
		
		// branch predict register
		bc = new int[1 << m];
		for(int i=0; i<bc.length; i++)
			bc[i] = 4;		
	}

	public int get_index(String address, int bhr) {
		int init_bit = 22;		
    	String bin_addr = hex_to_bin(address);
    	return Integer.parseInt(bin_addr.substring(init_bit - m, init_bit), 2);
	}

	public boolean predict(int index, boolean print) {
		boolean ret;
		if(bc[index] >= 4) 	ret = true;
		else 				ret = false;
		
		if(print) {
			if(logger != null)
				logger.severe(String.format("	Prediction:	%s", Boolean.toString(ret)));
		}
		
		return ret;
	}
	
	public String toString(boolean print) {
		StringBuffer sb;			
		if(print) {
			sb = new StringBuffer("OUTPUT\n");
			sb.append(String.format("number of predictions:		%d\n", cnt));
			sb.append(String.format("number of mispredictions:	%d\n", mispred));
			sb.append(String.format("misprediction rate:		%.2f%%\n", get_misrate()));
		}
		else {
			sb = new StringBuffer();
		}
		
		sb.append("FINAL BIMODAL CONTENTS\n");
		for(int i=0; i<bc.length; i++) {
			sb.append(String.format("%d	%d\n", i, (int) bc[i]));
		}
		return sb.toString();
	}
}
