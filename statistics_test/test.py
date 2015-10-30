from __future__ import division

def main():
	
	# simulated data

	print "Doing regression"

	ones = np.ones(5)
	x1 = [1.250682, 8.456252, 6.022818, 8.320444, 4.742932]
	x2 = [7.1063800, 2.9656994, 7.3939348, 0.6975759, 8.1522249]

	
	X = np.column_stack((x1, x2))
	X = sm.add_constant(X)

	print X

	Y = [2.0689760, 2.0831714, 3.2515604, 0.2919905, 3.5080655]

	results = sm.OLS(Y, X).fit()
	
	print()
	print("Parameters")
	print(results.params)
	print(results.summary())

if __name__=='__main__':
	import sys
	import os
	from statsmodels.stats.outliers_influence import OLSInfluence
	import math
	import matplotlib.pyplot as plt
	import numpy as np
	import statsmodels.api as sm
	import statsmodels.stats.api as sms
	from scipy import stats
	main()