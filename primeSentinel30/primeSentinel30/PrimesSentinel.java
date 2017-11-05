package primeSentinel30;

import java.text.NumberFormat;

public class PrimesSentinel {

	private Offsets30 offset = new Offsets30();
	private int[] endNumber = {1,7,11,13,17,19,23,29};
	private final int[] wages = {1,2,4,8,16,32,64,128};
	private final int[] columns = {7, 0, 7, 1, 7, 7, 7, 2, 7, 3};
	private byte[] table;
	private long sieveTime;

	public PrimesSentinel(long maxValue) {

		table = new byte[(int) (maxValue/30+1)];
		table[0] = 0b00000001;   /*	mark that number one isn't prime	*/

		String value = NumberFormat.getInstance().format( maxValue );
		String length = NumberFormat.getInstance().format( table.length );

		System.out.println("     value: " + value);
		System.out.println("table size:  " + length);
		System.out.println("size in MB: " + table.length/1024/1024);
		
		long start = System.currentTimeMillis();
		sieveUntil((int) Math.sqrt(maxValue));
		sieveTime = System.currentTimeMillis() - start; // save time of sieving
	}
	/**
	 * 
	 * @param
	 * 			number to check
	 * 
	 * @return
	 * 			0 - mean that this is prime
	 * 			1 - mean that it isn't prime
	 */
	public int isPrime(int num) {
		if(num == 2) return 0;
		if(num == 3) return 0;
		if(num == 5) return 0;
		int row = (int) num/30;
		int col = columns[num%30];
		if(col<=3) {
			return table[row]&wages[col];
		}
		return 1;
	}

	private void sieveUntil(int numberSqrt) {

		int max_i = (int) numberSqrt/30+1;

		for(int i=0; i<max_i; i++) {
			
			for(int j=0; j<8; j++) {

				if( (table[i] & wages[j]) == 0) {	/*	if number is prime	*/
					
					int numberValue = i*30+endNumber[j];
//					if(numberValue <= numberSqrt) {
						markDividersOfANumber(numberValue, j);
//					}
				}
			}
		}
	}

	private int findMaxValueInIntArray(int[] array) {
		int max = 0;
		for(int v : array)
			if(v > max) max = v;
		return max;
	}

	private int[] calculateDiffs(int[] offsets, int maxValue) {
		
		int[] diffs = new int[8];
		diffs[0] = maxValue - offsets[0];
		diffs[1] = maxValue - offsets[1];
		diffs[2] = maxValue - offsets[2];
		diffs[3] = maxValue - offsets[3];
		diffs[4] = maxValue - offsets[4];
		diffs[5] = maxValue - offsets[5];
		diffs[6] = maxValue - offsets[6];
		diffs[7] = maxValue - offsets[7];
		
		return diffs;
	}

	private void markDividersOfANumber(int number, int row) {

		int[] offsets = offset.calculateOffsets(number, row);

		/*	optimization code	*/
		int maxOffset = findMaxValueInIntArray(offsets);
		int[] diffs = calculateDiffs(offsets, maxOffset);

		while(maxOffset<table.length) {

			table[ maxOffset - diffs[0] ] |= 0b00000001;
			table[ maxOffset - diffs[1] ] |= 0b00000010;
			table[ maxOffset - diffs[2] ] |= 0b00000100;
			table[ maxOffset - diffs[3] ] |= 0b00001000;
			table[ maxOffset - diffs[4] ] |= 0b00010000;
			table[ maxOffset - diffs[5] ] |= 0b00100000;
			table[ maxOffset - diffs[6] ] |= 0b01000000;
			table[ maxOffset - diffs[7] ] |= 0b10000000;
			
			maxOffset+=number;
		}

		/*	endind	*/
		for(int i=maxOffset - diffs[0]; i<table.length; i+=number) {
			table[i] |= 0b00000001;
		}

		for(int i=maxOffset - diffs[1]; i<table.length; i+=number) {
			table[i] |= 0b00000010;
		}

		for(int i=maxOffset - diffs[2]; i<table.length; i+=number) {
			table[i] |= 0b00000100;
		}

		for(int i=maxOffset - diffs[3]; i<table.length; i+=number) {
			table[i] |= 0b00001000;
		}

		for(int i=maxOffset - diffs[4]; i<table.length; i+=number) {
			table[i] |= 0b00010000;
		}

		for(int i=maxOffset - diffs[5]; i<table.length; i+=number) {
			table[i] |= 0b00100000;
		}

		for(int i=maxOffset - diffs[6]; i<table.length; i+=number) {
			table[i] |= 0b01000000;
		}

		for(int i=maxOffset - diffs[7]; i<table.length; i+=number) {
			table[i] |= 0b10000000;
		}

	}

	public void printExcludedNumbers() {

		for(int i=0; i<table.length; i++) {
			for(int j=0; j<8; j++) {
				if((table[i]&wages[j])!=0) {
					//int calculateNumber = i * 30 + endNumber[j];
					System.out.println(i + endNumber[j]);
				}
			}
		}
	}

	public void printPossiblePrimes() {

		for(int i=0; i<table.length; i++) {
			
			System.out.format("%06d              ", i*30);
			
			for(int j=0; j<8; j++) {
				
				int number = i * 30 + endNumber[j];

				if((table[i]&wages[j])==0) {
					System.out.format( "%6d",number );
				}
				else {
					System.err.format( "%6d",number );
				}
			}
			System.out.println();
		}
	}

	public void printCols(int modulo, int maxValue) {

		for(int k=0; k<maxValue/modulo; k++) {
			System.out.format("%06d              ", k*modulo);
			for(int i=k*(modulo/30); i<k*(modulo/30)+(modulo/30); i++){
				for(int j=0; j<8; j++) {
					System.out.print( ((table[i]&wages[j])==0) ? "." : "O" );
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	public long count() {

		long possiblePrimes = 0;

		for(int i=0; i<table.length; i++) {
			possiblePrimes += (8 - Integer.bitCount( table[i] & 0xff ) );
		}
		return possiblePrimes + 3;	/*	add prime numbers 2, 3 and 5	*/
	}

	public double getSieveTime() {
		return (double) sieveTime/1000;
	}
}




