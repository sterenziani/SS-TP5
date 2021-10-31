import pandas as pd
import matplotlib.pyplot as plt
import glob
import numpy as np

all_files = glob.glob("./*.csv")

df1 = pd.DataFrame(columns=['t', 'n'])

for filename in all_files:
    df2 = pd.read_csv(filename, sep=';')
    df1 = pd.concat([df1, df2])

grouped_by_n = df1.groupby(['n'])

plt.errorbar(grouped_by_n.groups.keys(),
             grouped_by_n['t'].mean(),
             yerr=grouped_by_n['t'].std())

plt.ylabel('t(n) (s)')
plt.xlabel('n')
plt.legend()

plt.savefig('ex-b.png')
plt.show()
