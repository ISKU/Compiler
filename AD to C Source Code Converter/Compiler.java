import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * AD언어로 작성된 코드를 C언어로 작성하여 파일로 저장하는 클래스
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
	 * @param inputFileName AD언어로 작성된 파일 이름
	 * @param outputFileName C언어로 저장할 파일 이름
	 */
	public Compiler(String inputFileName, String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
	}

	/**
	 * AD언어로 작성된 파일을 한 줄씩 읽어 C언어로 작성하여 저장하는 메소드
	 * 사용자는 객체 생성 후 이 메소드만을 사용하여 코드를 변환한다.
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
	 * C언어 코드의 초기화 메소드
	 * stdio.h와 main 함수가 기본적으로 작성된다.
	 *
	 * @throws IOException
	 */
	private void writeInitializeCode() throws IOException {
		writer.write(Sentence.stdioHeader);
		writer.write(Sentence.mainFunction);
		writer.write(Sentence.openFunction);
	}

	/**
	 * C언어 코드 종료시에 작성될 코드 초기화 메소드
	 * return 0; 코드와 main 함수를 닫는 태그가 기본적으로 작성된다.
	 * 
	 * @throws IOException
	 */
	private void writeTerminateCode() throws IOException {
		writer.write(Sentence.returnZero);
		writer.write(Sentence.closeFunction);
	}

	/**
	 * AD언어로 작성된 코드를 한줄 씩 읽어 코드를 분석하여 C언어로 변환 및 작성한다.
	 * StringTokenizer를 사용하여 식별자를 분리하고, 식별자에 대한 코드를 다르게 작성한다.
	 * 
	 * @param line AD언어로 작성된 파일에서부터 읽어온 문자열로 해당 문자열을 분석한다.
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
	 * array 자료형을 선언 및 초기화하는 C코드를 작성하는 메소드
	 * 
	 * @param name 배열의 이름
	 * @param line 변수 이후에 초기화 될 배열 원소 리스트를 파싱한다.
	 * @throws IOException
	 */
	private void defineArray(String name, String line) throws IOException {
		String[] elementArray = stringArrayToElementArray(line);
		int arraySize = elementArray.length;

		writer.write(createArrayCode(name, parseIntegerArray(line)));
		writer.write(createVariableAssignmentCode("_" + name + "_size", arraySize));
	}

	/**
	 * 식별자가 reduce일 때 배열의 원소를 계산하는 C코드를 작성하는 메소드
	 * Example: (reduce list1 + 0 res)
	 * arrayName: list1
	 * operand: +
	 * initialValue: 0
	 * result: res
	 * 
	 * @param code StringTokenizer로 식별자 이후에 나타나는 token을 파싱한다.
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
	 * 식별자가 print일 때 변수의 값을 출력하는 C코드를 작성하는 메소드
	 * Example: (print res), res에 저장된 변수의 값을 출력한다.
	 * 
	 * @param vaiableName 출력할 변수의 이름
	 * @throws IOException
	 */
	private void definePrint(String vaiableName) throws IOException {
		writer.write(createPrintCode(vaiableName));
	}

	/**
	 * 변수에 값을 할당하는 C코드를 작성하는 메소드
	 * Example output: result = 0;
	 * 
	 * @param variableName 선언할 변수 이름
	 * @param value 선언된 변수에 초기화될 변수의 값
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
	 * 배열 변수에 값을 할당하는 C코드를 작성하는 메소드
	 * Example output: int list1[] = { 1, 2, 3 };
	 * 
	 * @param variableName 배열 변수의 이름
	 * @param value 배열에 초기화 될 원소의 리스트
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
	 * 식별자가 reduce일 때 이를 계산하는 for loop C언어 코드를 작성하는 메소드
	 * Example output: for(_AD_i = 0; _AD_i < _list1_size; _AD_i++){result += list1[_AD_i];}
	 * 
	 * @param arrayName 배열 변수의 이름
	 * @param operand 사용할 연산자
	 * @param result 결과값이 저장될 변수의 이름
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
	 * 변수의 값을 출력하는 C언어 코드를 작성하는 메소드
	 * Example output: printf("%d", result);
	 * 
	 * @param variableName 출력할 변수의 이름
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
	 * def, reduce, print 식별자를 구분할 때 사용하는 메소드
	 * 반환값은 각 식별자에 대응되는 integer 값이다.
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
	 * 입력받은 문자열에 괄호를 제거하여 반환하는 메소드
	 * 
	 * @param line
	 * @return
	 */
	private String removeBracket(String line) {
		return line.substring(line.indexOf("(") + 1, line.indexOf(")"));
	}

	/**
	 * 입력받은 문자열에서 배열 원소의 리스트만 반환하는 메소드
	 * 
	 * @param line
	 * @return
	 */
	private String parseIntegerArray(String line) {
		return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
	}

	/**
	 * 입력받은 문자열에서 배열 원소의 리스트 중에서 괄호를 제거하여 반환하는 메소드
	 * 
	 * @param line
	 * @return
	 */
	private String[] stringArrayToElementArray(String line) {
		return parseIntegerArray(line).split(", ");
	}
}
