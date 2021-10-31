import pandas as pd
import matplotlib.pyplot as plt
import glob

all_files = glob.glob("./*.csv")

i = 0
for filename in all_files:
    i += 1
    print(filename)
    df = pd.read_csv(filename, sep=';')
    print(df.head())
    plt.plot(df['t'], df['n'], label='Simulacion' + str(i))
plt.xlabel('t (s)')
plt.ylabel('n(t)')
plt.legend()
plt.savefig('ex-a.png')
plt.show()
