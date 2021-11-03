import pandas as pd
import matplotlib.pyplot as plt
import glob
import numpy as np

all_files = []
all_files.append("./results-1/timestamps-2-200-1.2.csv")
all_files.append("./results-2/timestamps-2-200-1.2.csv")
all_files.append("./results-3/timestamps-2-200-1.2.csv")
all_files.append("./results-4/timestamps-2-200-1.2.csv")
all_files.append("./results-5/timestamps-2-200-1.2.csv")
df1 = pd.DataFrame(columns=['t', 'n'])
for filename in all_files:
    df2 = pd.read_csv(filename, sep=';')
    df1 = pd.concat([df1, df2])
grouped_by_n = df1.groupby(['n'])

plt.errorbar(grouped_by_n['t'].mean(),
             grouped_by_n.groups.keys(),
             xerr=grouped_by_n['t'].std(), ecolor='lightblue', fmt='-o', ms=0.5)
plt.xlabel('Tiempo (s)')
plt.ylabel('Peatones evacuados')
plt.savefig('ex-b.png')
plt.show()
