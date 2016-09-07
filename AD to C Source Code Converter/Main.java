/**
 * AD���� �ۼ��� �ڵ带 C��� �ڵ�� ��ȯ�ϴ� ���α׷�
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
	 * ���� �Է�: test.ad ������ ������Ʈ ��ο� ������ �����Ͽ��� �Ѵ�.
	 * ���� ���: test.c �Է� ���ϰ� ���� ��ο� ������ ����ȴ�.
	 */
	public static void main(String... args) {
		try {
			new Compiler("test.ad", "test.c").compile();
		} catch (IOException error) {
			System.out.println(error.toString());
		}
	}
}