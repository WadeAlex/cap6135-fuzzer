/**
 * 
 */
package fuzzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.stream.FileImageOutputStream;

/**
 *
 */
public final class ProtocolAwareFuzzer extends MutationFuzzer {

	Set<Integer> illegalValues = null;
	
	/**
	 * Construct a new MutationFuzzer object.
	 * 
	 * @param executable
	 *            executable for target application
	 * @param inputImageFilename
	 *            name of input file from which fuzzer outputs are generated.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ProtocolAwareFuzzer(String[] args)
			throws FileNotFoundException, IOException {
		super(args);
		
		illegalValues = new HashSet<Integer>(
				Arrays.asList(0x00, 0x01, 0x02,
						0x14, 0x59,
						0x9e, 0xb1,
						0xd2, 0x119, 0x139,
						0x17c));
		
	}

	/* (non-Javadoc)
	 * @see fuzzer.Fuzzer#logExecutionParameters()
	 */
	@Override
	protected void logExecutionParameters() {
		
	}
	
	private boolean legalManipulationByte(int i) {
		return !illegalValues.contains(i) && (i < 0x18a || i > 0x13fa6);
	}

	@Override
	protected void writeToOutputFile(File outputFile) throws FileNotFoundException, IOException {
		float f = 0f;
		while ((f = randomNumberGenerator.nextFloat()) > 0.05)
			;
		setMutationProbability(f);
		while ((f = randomNumberGenerator.nextFloat()) > 0.333)
			;
		setByteRemovalProbability(f);
		while ((f = randomNumberGenerator.nextFloat()) > 0.333)
			;
		setByteInsertionProbability(f);
		setByteModificationProbability(0.333);
		setMinimumManipulationLength(1);
		setMaximumManipulationLength(randomNumberGenerator.nextInt(2) + 2);
		
		// These are the limits used when randomly determining mutation type.
		double byteRemovalValueLimit = this.byteRemovalProbability;
		double byteInsertionValueLimit = this.byteRemovalProbability + this.byteInsertionProbability;
		double byteModificationValueLimit = 1.0;

		// Length of byte manipulation range.
		int byteManipulationRangeLength = this.maximumManipulationLength - this.minimumManipulationLength;

		// Create output stream.
		FileImageOutputStream outputImage = new FileImageOutputStream(outputFile);
		
		// Iterate over input stream.
		for (int i = 0; i < sourceData.length; ++i) {
			// Read the next byte from the source image.
			byte b = sourceData[i];

			// Perform mutation based on random value.
			if ( legalManipulationByte(i) && 
					this.mutationProbability != 0.0 && 
					this.randomNumberGenerator.nextFloat() <= this.mutationProbability) {
				// Use random float value to determine mutation type.
				float mutationTypeValue = this.randomNumberGenerator.nextFloat();
				
				// TODO Use a separate function to determine mutation length.
				// Skip X number of bytes in the input file.
				if (mutationTypeValue <= byteRemovalValueLimit) {
					i++;
					
				// Insert random bytes.
				} else if (mutationTypeValue <= byteInsertionValueLimit) {
					int insertionLength = this.minimumManipulationLength
							+ this.randomNumberGenerator.nextInt(byteManipulationRangeLength);
					byte[] randomByteArray = new byte[insertionLength];
					this.randomNumberGenerator.nextBytes(randomByteArray);
					outputImage.write(randomByteArray);
					
				// Modify random bytes.
				// TODO Do this on a random number of bytes and make sure we don't go past the EOF.
				} else if (mutationTypeValue <= byteModificationValueLimit) {
					byte[] randomByteArray = new byte[1];
					this.randomNumberGenerator.nextBytes(randomByteArray);
					outputImage.write(randomByteArray);
				}
			} else {
				// Write the original byte.
				outputImage.write(b);
			}
		}
		
		try {
			outputImage.close();
		} catch (IOException e) {
			// this only happens if it's already closed
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ProtocolAwareFuzzer fuzzer = new ProtocolAwareFuzzer(args);
			fuzzer.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
