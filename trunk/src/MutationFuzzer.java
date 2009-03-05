

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;

/**
 * The mutation fuzzer creates test files by "mutating" the input file based on
 * random numbers and input parameters.
 */
public class MutationFuzzer extends Fuzzer {

	/**
	 * Mutation probability.
	 */
	protected double mutationProbability;

	/**
	 * Byte removal probability.
	 */
	protected double byteRemovalProbability;

	/**
	 * Byte insertion probability.
	 */
	protected double byteInsertionProbability;

	/**
	 * Byte modification probability.
	 */
	private double byteModificationProbability;

	/**
	 * Minimum manipulation length.
	 */
	protected int minimumManipulationLength;

	/**
	 * Maximum manipulation length.
	 */
	protected int maximumManipulationLength;
	
	protected byte[] sourceData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fuzzer.Fuzzer#logExecutionParameters()
	 */
	@Override
	protected void logExecutionParameters() throws IOException {
		this.logWriter.write("Results from using mutation fuzzer.");
		this.logWriter.newLine();
		this.logWriter.write("Mutation probability: " + this.mutationProbability);
		this.logWriter.newLine();
		this.logWriter.write("Byte removal probability: " + this.byteRemovalProbability);
		this.logWriter.newLine();
		this.logWriter.write("Byte insertion probability: " + this.byteInsertionProbability);
		this.logWriter.newLine();
		this.logWriter.write("Byte modification probability: " + this.byteModificationProbability);
		this.logWriter.newLine();
		this.logWriter.write("Minimum manipulation length: " + this.minimumManipulationLength);
		this.logWriter.newLine();
		this.logWriter.write("Maximum manipulation length: " + this.maximumManipulationLength);
		this.logWriter.newLine();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fuzzer.Fuzzer#writeToOutputFile(java.io.File)
	 */
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
			if (this.mutationProbability != 0.0 && this.randomNumberGenerator.nextFloat() <= this.mutationProbability) {
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
					byte[] singleByteArray = new byte[1];
					this.randomNumberGenerator.nextBytes(singleByteArray);
					outArray[j++] = singleByteArray[0];
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
	 * Construct a new MutationFuzzer object.
	 * 
	 * @param executable
	 *            executable for target application
	 * @param inputImageFilename
	 *            name of input file from which fuzzer outputs are generated.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MutationFuzzer(String[] args) throws FileNotFoundException, IOException {
		super(args);
		
		//buffer the input image
		sourceData = new byte[(int)sourceImage.length()];
		for (int i = 0; i < sourceImage.length(); ++i) {
			sourceData[i] = sourceImage.readByte();
		}
		sourceImage.close();
	}

	/**
	 * Sets the probability that a mutation will occur.
	 * 
	 * @param mutationProbability
	 *            the mutationProbability to set
	 */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	/**
	 * Sets the probability that a given mutation will be a byte removal.
	 * 
	 * @param byteRemovalProbability
	 *            the byteRemovalProbability to set
	 */
	public void setByteRemovalProbability(double byteRemovalProbability) {
		this.byteRemovalProbability = byteRemovalProbability;
	}

	/**
	 * Sets the probability that a given mutation will be a byte insertion.
	 * 
	 * @param byteInsertionProbability
	 *            the byteInsertionProbability to set
	 */
	public void setByteInsertionProbability(double byteInsertionProbability) {
		this.byteInsertionProbability = byteInsertionProbability;
	}

	/**
	 * Sets the probability that a given mutation will be a byte modification.
	 * 
	 * @param byteModificationProbability
	 *            the byteModificationProbability to set
	 */
	public void setByteModificationProbability(double byteModificationProbability) {
		this.byteModificationProbability = byteModificationProbability;
	}

	/**
	 * Sets the minimum length of a mutation.
	 * 
	 * @param minimumManipulationLength
	 *            the minimumManipulationLength to set
	 */
	public void setMinimumManipulationLength(int minimumManipulationLength) {
		this.minimumManipulationLength = minimumManipulationLength;
	}

	/**
	 * Sets the maximum length of a mutation.
	 * 
	 * @param maximumManipulationLenght
	 *            the maximumManipulationLenght to set
	 */
	public void setMaximumManipulationLength(int maximumManipulationLenght) {
		this.maximumManipulationLength = maximumManipulationLenght;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MutationFuzzer fuzzer = new MutationFuzzer(args);
			fuzzer.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
