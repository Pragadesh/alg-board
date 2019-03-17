import org.junit.Test;

public class PercolationStatsTest {
	
	@Test
	public void testPercolationStats() {
		findPercolationParameters(200, 100);
	}
	
	private void findPercolationParameters(int n, int trials) {
		PercolationStats stats = new PercolationStats(n, trials);
		System.out.println("Mean: "+stats.mean());
		System.out.println("stdDev: "+stats.stddev());
		System.out.println(String.format("95% confidence interval: [%d, %d]", stats.confidenceLo(), stats.confidenceHi()));
	}
}
