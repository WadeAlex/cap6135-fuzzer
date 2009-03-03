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
public class MutationFuzzer extends Fuzzer {

	/**
	 * @param executable
	 * @param inputImageFilename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MutationFuzzer(String executable, String inputImageFilename)
			throws FileNotFoundException, IOException {
		super(executable, inputImageFilename);
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
