package analysis

import static org.junit.jupiter.api.Assertions.*
import static org.junit.jupiter.api.DynamicTest.*
import static org.junit.jupiter.api.DynamicContainer.*
import org.junit.jupiter.api.*
import java.util.stream.*
import org.apache.commons.math3.stat.regression.SimpleRegression

public class Module6 {
	static final MAP_OPERATIONS	= 250_000,
				 SKIP_BEFORE	= 5_000,
				 PLOT_INTERVAL	= 500,
				 SLOPE_BOUND	= 0.2,
				 RANDOM_SEED	= 2019_08,
				 HIT_RATE		= 0.8,
				 HIT_CACHE		= 100

	static final BATTERY_LOG = null
	// DO NOT LOG CALLS:       null
	// LOG CALLS TO CONSOLE:   System.out
	// LOG CALLS TO FILE:	   new PrintStream('map.log')
	
	static storage.AbstractMap subject
	static java.util.AbstractMap exemplar
	static java.util.Random RNG
	
	@BeforeAll
	static void setup() {
		subject = new storage.HashMap<String, Integer>()
		exemplar = new java.util.HashMap<String, Integer>()
		RNG = new Random(RANDOM_SEED)
	}
	
	@DisplayName('Time Estimate')
	@TestFactory
	def analysis() {
		final plot = new SimpleRegression(),
			cache = [RANDOM_SEED.toString()] as ArrayList,
			key = {
				if (RNG.nextDouble() < HIT_RATE) {
					cache[RNG.nextInt(cache.size())]
				}
				else {
					if (cache.size() >= HIT_CACHE)
						cache.remove(RNG.nextInt(cache.size()))
					cache.add(new BigInteger(128, RNG).toString(Character.MAX_RADIX))
					cache[-1]
				}
			},
			val = {RNG.nextInt()},
			test = {method, ...args ->
				final call = "$method(${args ? args.inspect()[1..-2] : ''})"
				BATTERY_LOG?.println("map.$call;".replace("'", '"'))
				assertEquals(
					exemplar."$method"(*args),
					subject."$method"(*args),
					"$call must return correct results"
				)
			}
			
		def skip = false
		
		BATTERY_LOG?.println("Map map = new storage.HashMap();")
		IntStream.rangeClosed(1, MAP_OPERATIONS).filter({n -> n % PLOT_INTERVAL == 0 && !skip}).mapToObj({ upto -> 
			dynamicTest(String.format("%,d ops [%s]", upto, upto >= SKIP_BEFORE ? 'correctness, analysis' : 'correctness only'), {
				final started = System.nanoTime()
				
				RNG.doubles(PLOT_INTERVAL).forEach({ p ->
					if      (p < 0.60) test('put', key(), val())
					else if (p < 0.75) test('get', key())
					else if (p < 0.90) test('remove', key())
					else if (p < 0.93) test('size')
					else if (p < 0.95) test('isEmpty')
					else			   test('containsKey', key())
				})
				
				final elapsed = System.nanoTime() - started
				final average = elapsed / PLOT_INTERVAL
				
				plot.addData(upto, average)
				
				final slope = plot.getSlope()
				final alpha = plot.getSignificance()
				
				final report = String.format(
					"%,8d ops: %,8.3f ms/op %8.3f slope %8.3f alpha\n",
					upto,
					average / 1_000,
					slope,
					alpha
				)
				if (upto < SKIP_BEFORE || slope <= SLOPE_BOUND) {
					System.out.printf(report)
				}
				else {
					System.err.printf(report)
					skip = true
					fail(String.format(
						"Average runtime slope %.3f exceeds %.3f bound, all remaining tests will be skipped",
							slope,
							SLOPE_BOUND
						)
					)
				}
			})
		})
	}
}
