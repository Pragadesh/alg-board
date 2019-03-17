import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
	
	double[] percolationThreshold;
	
	// perform trials independent experiments on an n-by-n grid
	public PercolationStats(int n, int trials) {
		this.percolationThreshold = new double[trials];
		for(int i=0; i<trials; i++) {
			percolationThreshold[i] = findPercolationThreshold(n);
		}
	}
	
	private double findPercolationThreshold(int n) {
		Percolation percolation = new Percolation(n);
		while(!percolation.percolates()) {
			int row = StdRandom.uniform(n)+1;
			int col = StdRandom.uniform(n)+1;
			percolation.open(row, col);
		}
		return (percolation.numberOfOpenSites() / Math.pow(n, 2));
	}
	
	// sample mean of percolation threshold
	public double mean() { 
		return StdStats.mean(percolationThreshold);
	}
	
	// sample standard deviation of percolation threshold
	public double stddev() { 
		return StdStats.stddev(percolationThreshold);
	}
	
	// low  endpoint of 95% confidence interval
	public double confidenceLo() {
		return (mean() - getRangeTerm());
	}
	
	// high endpoint of 95% confidence interval
	public double confidenceHi() {
		return (mean() + getRangeTerm());
	}
	
	private double getRangeTerm() {
		return (1.96 * stddev()) / Math.sqrt(percolationThreshold.length);
	}
	
	public static void main(String[] args) {
		if(args.length >= 2) {
			int n = Integer.parseInt(args[0]);
			int trial = Integer.parseInt(args[1]);
			findPercolationParameters(n, trial);
		}
//		findPercolationParameters(200, 100);
//		findPercolationParameters(200, 100);
//		findPercolationParameters(2, 10000);
//		findPercolationParameters(2, 100000);
	}
	
	private static void findPercolationParameters(int n, int trials) {
		PercolationStats stats = new PercolationStats(n, trials);
		System.out.println("Mean: "+stats.mean());
		System.out.println("stdDev: "+stats.stddev());
		System.out.println(String.format("95-percent confidence interval: [%.10f, %.10f]", stats.confidenceLo(), stats.confidenceHi()));
	}

}
