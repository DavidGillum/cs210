package grade

import static org.junit.jupiter.api.Assertions.*
import static org.junit.jupiter.api.DynamicTest.*
import static org.junit.jupiter.api.DynamicContainer.*
import org.junit.jupiter.api.*
import java.util.stream.*
import java.lang.reflect.*
import java.nio.file.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Module7 {
	static final PAGE_OPS			= 200,
				 RANDOM_SEED		= 2019_08
	
	static double grade
	static storage.AbstractPage subject
	static Object[] exemplar
	static List<String> field_types
	static int key_index, page_length
	static java.util.Random RNG
	
	@BeforeAll
	static void setup() {
		grade = 0
		RNG = new Random(RANDOM_SEED)
	}
	
	@TestFactory
	@DisplayName('Compliance [Audit]')
	@Order(1)
	final compliance() {
		return IntStream.rangeClosed(1, 3).mapToObj({step ->
			if (step == 1) {
				dynamicTest('Standard Constructor', {
					try {
						final Class<?> clazz = Class.forName('storage.Page');
						clazz.getDeclaredConstructor(Path.class, List.class, int.class);
					}
					catch (NoSuchMethodException e) {
						fail('Expected a standard constructor, but required constructor is missing');
					}
				})
			}
			else if (step == 2) {
				dynamicTest('Data Folder Accessible', {
					try {
						if (!Files.exists(Paths.get('data')))
							Files.createDirectory(Paths.get('data'));
						if (Files.exists(Paths.get('data', 'test')))
							Files.walk(Paths.get('data', 'test'))
								.sorted(Comparator.reverseOrder())
								.forEach({path -> Files.delete(path)});
						Files.createDirectory(Paths.get('data', 'test'));
					}
					catch (Exception e) {
						fail('Expected accessible test folder in data folder, but access fails', e);
					}
				})
			}
			else if (step == 3) {
				dynamicTest('Class Instantiable', {
					try {
						subject = new storage.Page(
							Paths.get('data', 'test', 'page_0.bin'),
							['string', 'integer', 'boolean'],
							0
						)
					}
					catch (Exception e) {
						fail('Expected to instantiate a page, but the page constructor fails', e);
					}
				})
			}
		})
	}
	
	@TestFactory
	@DisplayName('Pattern (s*, i, b) [20%]')
	@Order(2)
	final pattern1() {
		if (subject == null)
			fail('Depends on compliance tests')
		
		field_types = ['string', 'integer', 'boolean']
		key_index = 0
		page_length = 5
		
		tests(1)
	}
	
	@TestFactory
	@DisplayName('Pattern (i, b, b, i, i*, b) [20%]')
	@Order(3)
	final pattern2() {
		if (grade < 15)
			fail('Depends on meeting 15% cumulatively')
		
		field_types = ['integer', 'boolean', 'boolean', 'integer', 'integer', 'boolean']
		key_index = 4
		page_length = 10
		
		tests(2)
	}
	
	@TestFactory
	@DisplayName('Pattern (b, i, s*) [20%]')
	@Order(4)
	final pattern3() {
		if (grade < 30)
			fail('Depends on meeting 30% cumulatively')
		
		field_types = ['boolean', 'integer', 'string']
		key_index = 2
		page_length = 20
		
		tests(3)
	}
	
	@TestFactory
	@DisplayName('Pattern (s, s*, s) [20%]')
	@Order(5)
	final pattern4() {		
		if (grade < 45)
			fail('Depends on meeting 45% cumulatively')
		
		field_types = ['string', 'string', 'string']
		key_index = 1
		page_length = 50
		
		tests(4)
	}
	
	@TestFactory
	@DisplayName('Pattern (s, i, i, b, b, s*, i, b, s) [20%]')
	@Order(6)
	final pattern5() {
		if (grade < 60)
			fail('Depends on meeting 60% cumulatively')
		
		field_types = ['string', 'integer', 'integer', 'boolean', 'boolean', 'string', 'integer', 'boolean', 'string']
		key_index = 5
		page_length = 100
		
		tests(5)
	}
	
	static final tests(def batch) {
		subject = new storage.Page(
			Paths.get('data', 'test', "page_${batch}.bin"),
			field_types,
			key_index
		)
		subject.length(page_length)
		
		exemplar = new Object[page_length]
		
		RNG.doubles(PAGE_OPS).mapToObj({ p ->
			if      (p < 0.30) test('write', index(), entry(field_types))
			else if (p < 0.45) test('writeNull', index())
			else if (p < 0.90) test('read', index())
			else if (p < 0.95) test('isEntry', index())
			else			   test('isNull', index())
		})
	}
	
	static final test(def method, def index, def entry = null) {
		final call = "$method(${index}${entry != null ? ', <' + entry.getKey().inspect() + '=' + entry.getValue().inspect() + '>' : ''})".replace("'", '"')
		
		return dynamicTest(call, {
			if (method == 'write') {
				subject.write(index, entry)
				exemplar[index] = entry
			}
			else if (method == 'writeNull') {
				subject.writeNull(index)
				exemplar[index] = null
			}
			else if (method == 'read') {
				if (exemplar[index] != null) {
					assertEquals(
						exemplar[index],
						subject.read(index),
						"$call must yield correct results"
					)
				}
				else {
					assertThrows(
						IllegalStateException.class, 
						{subject.read(index)},
						"$call must throw required exception for null entry"
					)
				}
			}
			else if (method == 'isEntry') {
				assertEquals(
					exemplar[index] != null,
					subject.isEntry(index),
					"$call must yield correct results"
				)
			}
			else if (method == 'isNull') {
				assertEquals(
					exemplar[index] == null,
					subject.isNull(index),
					"$call must yield correct results"
				)
			}
			
			grade += (1 / PAGE_OPS) * 20
		})
	}
	
	@AfterAll
	static final void report() {
		System.out.println("[M7 PASSED ${Math.round(grade)}% OF WEIGHTED TESTS]")
	}
	
	static final index() {
		RNG.nextInt(page_length)
	}
	
	static final STR_ALPHABET = 'abcdefghijklmnopqrstuvwxyz'
	static final entry(def types) {
		def values = types.collect({t ->
			if (RNG.nextDouble() < 0.02)
				null
			else if (t == 'string') 
				RNG.ints(RNG.nextInt(32)).mapToObj({i -> STR_ALPHABET[i % STR_ALPHABET.size()]}).collect(Collectors.joining())
			else if (t == 'integer') 
				RNG.nextInt()
			else if (t == 'boolean') 
				RNG.nextBoolean()
			else null
		})
		
		new java.util.AbstractMap.SimpleEntry<Object, List<Object>>(
			values[key_index],
			values
		)
	}
}