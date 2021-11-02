import pandas as pd
import matplotlib.pyplot as plt
import glob

all_files = []
all_files.append(glob.glob("./velocity-1/*.csv"))
all_files.append(glob.glob("./velocity-2/*.csv"))
all_files.append(glob.glob("./velocity-3/*.csv"))
all_files.append(glob.glob("./velocity-4/*.csv"))

final_df = pd.DataFrame(columns=['v', 't'])
for folder in all_files:
    velocities = []
    times = []
    for filename in folder:
        filename_parsed = filename.split('-')
        velocity = float(filename_parsed[2])
        df = pd.read_csv(filename, sep=';')
        time = float(df[-1:].t)
        final_df = final_df.append(
            {'v': velocity, 't': time}, ignore_index=True)
print(final_df)

grouped_by_v = final_df.groupby('v')
plt.errorbar(grouped_by_v.groups.keys(), grouped_by_v.t.mean(),
             marker='o', yerr=grouped_by_v.t.std(), fmt='+')
plt.ylabel('Tiempo total de evacuacion promedio (s)')
plt.xlabel('Velocidad')
plt.savefig('velocities.png')
plt.show()
