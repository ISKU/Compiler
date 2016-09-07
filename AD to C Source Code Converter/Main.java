/**
 * AD언어로 작성된 코드를 C언어 코드로 변환하는 프로그램
 * 
 * Execution Environment
 * OS: window7
 * Tool: Eclipse Neon
 * JAVA Compiler: compliance level 1.8
 * C Compiler: gcc (ubuntu 14.04LTS) 4.8.4
 * 
 * @author Kim Min-Ho
 */

import java.io.IOException;

public class Main {
	
	/**
	 * 파일 입력: test.ad 동일한 프로젝트 경로에 파일이 존재하여야 한다.
	 * 파일 출력: test.c 입력 파일과 같은 경로에 파일이 저장된다.
	 */
	public static void main(String... args) {
		try {
			new Compiler("test.ad", "test.c").compile();
		} catch (IOException error) {
			System.out.println(error.toString());
		}
	}
}