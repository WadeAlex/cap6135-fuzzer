

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

/**
 *
 */
public final class ProtocolAwareFuzzer extends MutationFuzzer {
	
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
		
	}
	
	private boolean legalManipulationByte(int i) {
		return (i > 1) && (i < 0x18a);
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
		byte[] outArray = new byte[sourceData.length * 2];
		int j = 0;
		
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
					int deletionLength = this.minimumManipulationLength + this.randomNumberGenerator.nextInt(byteManipulationRangeLength);
					long seekLocation = i + deletionLength;
					if(seekLocation >= sourceData.length) {
						break;
					} else {
						//sourceImage.seek(seekLocation);
						i = (int)seekLocation;
					}
					
				// Insert random bytes.
				} else if (mutationTypeValue <= byteInsertionValueLimit) {
					int insertionLength = this.minimumManipulationLength
							+ this.randomNumberGenerator.nextInt(byteManipulationRangeLength);
					byte[] randomByteArray = new byte[insertionLength];
					this.randomNumberGenerator.nextBytes(randomByteArray);
					for(int k = 0; k<randomByteArray.length; k++) {
						outArray[j++] = randomByteArray[k];
					}
					
				// Modify random bytes.
				// TODO Do this on a random number of bytes and make sure we don't go past the EOF.
				} else if (mutationTypeValue <= byteModificationValueLimit) {
					byte[] randomByteArray = new byte[1];
					this.randomNumberGenerator.nextBytes(randomByteArray);
					outArray[j++] = randomByteArray[0];
				}
			} else {
				// Write the original byte.
				outArray[j++] = b;
			}
		}
		
		try {
			outputImage.write(outArray, 0, j);
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
