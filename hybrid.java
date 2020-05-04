package sim;

import java.util.logging.Logger;

public class hybrid extends Util {
	private Logger logger;
	
	public hybrid(String[] args, Logger logger) {
		super(logger);
		this.logger = logger;

		name = args[1];
		k = Integer.parseInt(args[2]);
		m = Integer.parseInt(args[3]);
		n = Integer.parseInt(args[4]);		
		
		lim = (1 << n) - 1;
		// branch predict register
		bc = new int[1 << m];
		for(int i=0; i<bc.length; i++)
			bc[i] = 4;
		
		// counter table
		ct = new int[1 << k];
		for(int i=0; i<ct.length; i++)
			ct[i] = 1;
	}

	public void set_bc(boolean taken, int index) {
		if(taken && bc[index] < 7) {
			bc[index]++;
		}
		else if(!taken && bc[index] > 0){
			bc[index]--;
		}
	}
	
	public boolean predict(int index) {
		boolean ret;
		if(bc[index] >= 4) 	ret = true;
		else 				ret = false;
		if(logger != null)
			logger.severe(String.format("	Prediction:	%s", Boolean.toString(ret)));
		return ret;
	}

	public int get_branch_index(String address) {
		String index = address.substring(22-k, 22);
		int idx = Integer.parseInt(index, 2);
		if(logger != null) {
			logger.severe(String.format("	CT index:	%d", idx));
			logger.severe(String.format("	CT value:	%d", ct[idx]));
		}
		return idx;
	}
	
	// implement
	public int get_index(String address, int bhr) {
		int init_bit = 24;
		
    	String bin_addr = hex_to_bin(address);
		String upper, lower;
		int lowermost;
		
		upper = bin_addr.substring(init_bit - m, init_bit - n);
		lower = bin_addr.substring(init_bit - n, init_bit);
		lowermost = Integer.parseInt(lower, 2);
		lowermost ^= bhr;
		
		lower = binaryStringWithZeroPad(Integer.toBinaryString(lowermost), n);
		return Integer.parseInt(upper+lower, 2);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("OUTPUT\n");
		sb.append(String.format("number of predictions:		%d\n", cnt));
		sb.append(String.format("number of mispredictions:	%d\n", mispred));
		sb.append(String.format("misprediction rate:		%.2f%%\n", get_misrate()));

		sb.append("FINAL CHOOSER CONTENTS\n");
		for(int i=0; i<ct.length; i++) {
			sb.append(String.format("%d	%d\n", i, (int) ct[i]));
		}
		return sb.toString();
	}

}
