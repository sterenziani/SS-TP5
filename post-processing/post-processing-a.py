import pandas as pd
import matplotlib.pyplot as plt
import glob

all_files = []
all_files.append("./results-1/timestamps-2-200-1.2.csv")
all_files.append("./results-2/timestamps-2-200-1.2.csv")
all_files.append("./results-3/timestamps-2-200-1.2.csv")
all_files.append("./results-4/timestamps-2-200-1.2.csv")
all_files.append("./results-5/timestamps-2-200-1.2.csv")

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
