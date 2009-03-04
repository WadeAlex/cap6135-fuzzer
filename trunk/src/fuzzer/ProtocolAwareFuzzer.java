/**
 * 
 */
package fuzzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 */
public final class ProtocolAwareFuzzer extends Fuzzer {

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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fuzzer.Fuzzer#logExecutionParameters()
	 */
	@Override
	protected void logExecutionParameters() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fuzzer.Fuzzer#writeToOutputFile(java.io.File)
	 */
	@Override
	protected void writeToOutputFile(File outputFile) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
