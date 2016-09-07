import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * AD���� �ۼ��� �ڵ带 C���� �ۼ��Ͽ� ���Ϸ� �����ϴ� Ŭ����
 * 
 * @author Kim Min-Ho
 */
public class Compiler {

	private final String inputFileName;
	private final String outputFileName;
	private BufferedReader reader;
	private BufferedWriter writer;

	/**
	 * Compiler Class Constructor.
	 * 
	 * @param inputFileName AD���� �ۼ��� ���� �̸�
	 * @param outputFileName C���� ������ ���� �̸�
	 */
	public Compiler(String inputFileName, String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
	}

	/**
	 * AD���� �ۼ��� ������ �� �پ� �о� C���� �ۼ��Ͽ� �����ϴ� �޼ҵ�
	 * ����ڴ� ��ü ���� �� �� �޼ҵ常�� ����Ͽ� �ڵ带 ��ȯ�Ѵ�.
	 * 
	 * @throws IOException
	 */
	public void compile() throws IOException {
		reader = new BufferedReader(new FileReader(inputFileName));
		writer = new BufferedWriter(new FileWriter(outputFileName));
		String line = null;

		writeInitializeCode();
		while ((line = reader.readLine()) != null)
			lineParser(line);
		writeTerminateCode();

		reader.close();
		writer.close();
	}

	/**
	 * C��� �ڵ��� �ʱ�ȭ �޼ҵ�
	 * stdio.h�� main �Լ��� �⺻������ �ۼ��ȴ�.
	 *
	 * @throws IOException
	 */
	private void writeInitializeCode() throws IOException {
		writer.write(Sentence.stdioHeader);
		writer.write(Sentence.mainFunction);
		writer.write(Sentence.openFunction);
	}

	/**
	 * C��� �ڵ� ����ÿ� �ۼ��� �ڵ� �ʱ�ȭ �޼ҵ�
	 * return 0; �ڵ�� main �Լ��� �ݴ� �±װ� �⺻������ �ۼ��ȴ�.
	 * 
	 * @throws IOException
	 */
	private void writeTerminateCode() throws IOException {
		writer.write(Sentence.returnZero);
		writer.write(Sentence.closeFunction);
	}

	/**
	 * AD���� �ۼ��� �ڵ带 ���� �� �о� �ڵ带 �м��Ͽ� C���� ��ȯ �� �ۼ��Ѵ�.
	 * StringTokenizer�� ����Ͽ� �ĺ��ڸ� �и��ϰ�, �ĺ��ڿ� ���� �ڵ带 �ٸ��� �ۼ��Ѵ�.
	 * 
	 * @param line AD���� �ۼ��� ���Ͽ������� �о�� ���ڿ��� �ش� ���ڿ��� �м��Ѵ�.
	 * @throws IOException
	 */
	private void lineParser(String line) throws IOException {
		StringTokenizer code = new StringTokenizer(removeBracket(line));
		int indexOfIdentifier = parseIdentifier(code.nextToken());

		if (indexOfIdentifier == Identifier.DEF)
			defineArray(code.nextToken(), line);
		else if (indexOfIdentifier == Identifier.REDUCE)
			defineReduce(code);
		else if (indexOfIdentifier == Identifier.PRINT)
			definePrint(code.nextToken());
	}

	/**
	 * array �ڷ����� ���� �� �ʱ�ȭ�ϴ� C�ڵ带 �ۼ��ϴ� �޼ҵ�
	 * 
	 * @param name �迭�� �̸�
	 * @param line ���� ���Ŀ� �ʱ�ȭ �� �迭 ���� ����Ʈ�� �Ľ��Ѵ�.
	 * @throws IOException
	 */
	private void defineArray(String name, String line) throws IOException {
		String[] elementArray = stringArrayToElementArray(line);
		int arraySize = elementArray.length;

		writer.write(createArrayCode(name, parseIntegerArray(line)));
		writer.write(createVariableAssignmentCode("_" + name + "_size", arraySize));
	}

	/**
	 * �ĺ��ڰ� reduce�� �� �迭�� ���Ҹ� ����ϴ� C�ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example: (reduce list1 + 0 res)
	 * arrayName: list1
	 * operand: +
	 * initialValue: 0
	 * result: res
	 * 
	 * @param code StringTokenizer�� �ĺ��� ���Ŀ� ��Ÿ���� token�� �Ľ��Ѵ�.
	 * @throws IOException
	 */
	private void defineReduce(StringTokenizer code) throws IOException {
		String arrayName = code.nextToken();
		String operand = code.nextToken();
		int initialValue = Integer.parseInt(code.nextToken());
		String result = code.nextToken();

		writer.write(createVariableAssignmentCode(Sentence.iterator, 0));
		writer.write(createVariableAssignmentCode(result, initialValue));
		writer.write(createReduceCode(arrayName, operand, result));
	}

	/**
	 * �ĺ��ڰ� print�� �� ������ ���� ����ϴ� C�ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example: (print res), res�� ����� ������ ���� ����Ѵ�.
	 * 
	 * @param vaiableName ����� ������ �̸�
	 * @throws IOException
	 */
	private void definePrint(String vaiableName) throws IOException {
		writer.write(createPrintCode(vaiableName));
	}

	/**
	 * ������ ���� �Ҵ��ϴ� C�ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example output: result = 0;
	 * 
	 * @param variableName ������ ���� �̸�
	 * @param value ����� ������ �ʱ�ȭ�� ������ ��
	 * @return
	 */
	private String createVariableAssignmentCode(String variableName, int value) {
		return new StringBuilder()
				.append(Sentence.integerType)
				.append(variableName)
				.append(Sentence.equal)
				.append(value)
				.append(Sentence.terminator)
				.toString();
	}

	/**
	 * �迭 ������ ���� �Ҵ��ϴ� C�ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example output: int list1[] = { 1, 2, 3 };
	 * 
	 * @param variableName �迭 ������ �̸�
	 * @param value �迭�� �ʱ�ȭ �� ������ ����Ʈ
	 * @return
	 */
	private String createArrayCode(String variableName, String value) {
		return new StringBuilder()
				.append(Sentence.integerType)
				.append(variableName)
				.append(Sentence.arrayType)
				.append(Sentence.equal)
				.append("{ " + value + " }")
				.append(Sentence.terminator)
				.toString();
	}

	/**
	 * �ĺ��ڰ� reduce�� �� �̸� ����ϴ� for loop C��� �ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example output: for(_AD_i = 0; _AD_i < _list1_size; _AD_i++){result += list1[_AD_i];}
	 * 
	 * @param arrayName �迭 ������ �̸�
	 * @param operand ����� ������
	 * @param result ������� ����� ������ �̸�
	 * @return
	 */
	private String createReduceCode(String arrayName, String operand, String result) {
		return new StringBuilder()
				.append(Sentence.forFunction)
				.append(Sentence.openBracket)
				.append(Sentence.iterator)
				.append(Sentence.equal)
				.append(0)
				.append(Sentence.terminaterNoLineBreak)
				.append(Sentence.iterator)
				.append(Sentence.gt)
				.append("_" + arrayName + "_size")
				.append(Sentence.terminaterNoLineBreak)
				.append(Sentence.iterator + "++")
				.append(Sentence.closeBracket)
				.append(Sentence.openFunction)
				.append(result)
				.append(operand.equals("+") ? Sentence.sumEqual : Sentence.multiplicationEqual)
				.append(arrayName + "[" + Sentence.iterator + "]")
				.append(Sentence.terminator)
				.append(Sentence.closeFunction)
				.toString();
	}

	/**
	 * ������ ���� ����ϴ� C��� �ڵ带 �ۼ��ϴ� �޼ҵ�
	 * Example output: printf("%d", result);
	 * 
	 * @param variableName ����� ������ �̸�
	 * @return
	 */
	private String createPrintCode(String variableName) {
		return new StringBuilder()
				.append(Sentence.printfFunction)
				.append(Sentence.openBracket)
				.append(Sentence.quote)
				.append(Sentence.printInteger)
				.append(Sentence.quote)
				.append(Sentence.comma)
				.append(variableName)
				.append(Sentence.closeBracket)
				.append(Sentence.terminator)
				.toString();
	}

	/**
	 * def, reduce, print �ĺ��ڸ� ������ �� ����ϴ� �޼ҵ�
	 * ��ȯ���� �� �ĺ��ڿ� �����Ǵ� integer ���̴�.
	 * 
	 * @param identifier
	 * @return
	 */
	private int parseIdentifier(String identifier) {
		switch (identifier) {
		case "def":
			return Identifier.DEF;
		case "reduce":
			return Identifier.REDUCE;
		case "print":
			return Identifier.PRINT;
		default:
			return 0;
		}
	}

	/**
	 * �Է¹��� ���ڿ��� ��ȣ�� �����Ͽ� ��ȯ�ϴ� �޼ҵ�
	 * 
	 * @param line
	 * @return
	 */
	private String removeBracket(String line) {
		return line.substring(line.indexOf("(") + 1, line.indexOf(")"));
	}

	/**
	 * �Է¹��� ���ڿ����� �迭 ������ ����Ʈ�� ��ȯ�ϴ� �޼ҵ�
	 * 
	 * @param line
	 * @return
	 */
	private String parseIntegerArray(String line) {
		return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
	}

	/**
	 * �Է¹��� ���ڿ����� �迭 ������ ����Ʈ �߿��� ��ȣ�� �����Ͽ� ��ȯ�ϴ� �޼ҵ�
	 * 
	 * @param line
	 * @return
	 */
	private String[] stringArrayToElementArray(String line) {
		return parseIntegerArray(line).split(", ");
	}
}
