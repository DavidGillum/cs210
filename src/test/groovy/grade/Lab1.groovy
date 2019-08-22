package grade

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*

class Lab1 {
	static int passed_cases = 0
	static int total_cases = 0
	
	@BeforeAll
	static void explain() {
		System.out.println('Confirming changed student details in the Server class...')
	}
	
	@Test
	void testChangedName() {
		total_cases++;
		
		assertNotEquals(
			'Your Name',
			core.Server.STUDENT_NAME,
			'You must set your full name in the Server class'
		)
		
		System.out.println("[Name]  ${core.Server.STUDENT_NAME}")
		passed_cases++;
	}
	
	@Test
	void testChangedIDNum() {
		total_cases++;

		assertNotEquals(
			'000000000',
			core.Server.STUDENT_IDNUM,
			'You must set your student ID number in the Server class'
		)
		
		System.out.println("[IDNum] ${core.Server.STUDENT_IDNUM}")
		passed_cases++;
	}
	
	@Test
	void testChangedEmail() {
		total_cases++;

		assertNotEquals(
			'email@mix.wvu.edu',
			core.Server.STUDENT_EMAIL,
			'You must set your email address in the Server class'
		)
		
		System.out.println("[Email] ${core.Server.STUDENT_EMAIL}")
		passed_cases++;
	}
	
	@AfterAll
	static void report() {
		if (passed_cases == total_cases)
			System.out.println('\nStudent detail changes confirmed in the Server class.\nThis does not confirm the accuracy of those details.\nCheck the details for accuracy before submitting.\n')
		else
			System.err.println('\nYou must accurately set all the student details in the Server class.\n')
		
		final double rate = passed_cases / (double) total_cases
		System.out.println(
			'[L1 PASSED ' + Math.round(rate * 100) + '% OF UNIT TESTS]',
		)
	}
}