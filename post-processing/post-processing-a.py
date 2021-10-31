import pandas as pd
import matplotlib.pyplot as plt
import glob

all_files = glob.glob("./*.csv")

i = 0
for filename in all_files:
    i += 1
    print(filename)
    df = pd.read_csv(filename, sep=';')
    plt.plot(df['t'], df['n'])
plt.xlabel('Tiempo (s)')
plt.ylabel('Peatones evacuados')
plt.savefig('ex-a.png')
plt.show()
