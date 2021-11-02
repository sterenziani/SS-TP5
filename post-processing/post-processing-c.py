import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import math

def n(t, dt, df):
    Nt = df[df['t'] == df[df['t'].lt(t)]['t'].max()]['n'].max()
    Ntdt = df[df['t'] == df[df['t'].lt(t+dt)]['t'].max()]['n'].max()
    return (Ntdt - Nt)/dt

def main():
    df200 = pd.DataFrame(columns=['t', 'n'])
    df260 = pd.DataFrame(columns=['t', 'n'])
    df320 = pd.DataFrame(columns=['t', 'n'])
    df380 = pd.DataFrame(columns=['t', 'n'])
    for folder in range(1,6):
        folder_name = "./results-" +str(folder) +"/"
        #df = pd.read_csv(folder_name+"timestamps-2-200-1.2.csv", sep=';')
        df = pd.read_csv(folder_name+"timestamps-2-200-1.2.csv", sep=';')
        df200 = pd.concat([df200, df])
        df = pd.read_csv(folder_name+"timestamps-2-260-1.8.csv", sep=';')
        df260 = pd.concat([df260, df])
        df = pd.read_csv(folder_name+"timestamps-2-320-2.4.csv", sep=';')
        df320 = pd.concat([df320, df])
        df = pd.read_csv(folder_name+"timestamps-2-380-3.csv", sep=';')
        df380 = pd.concat([df380, df])

    df200 = pd.DataFrame({ "t": df200.groupby(['n'])['t'].mean(), "n": df200.groupby(['n']).groups.keys() })
    df260 = pd.DataFrame({ "t": df260.groupby(['n'])['t'].mean(), "n": df260.groupby(['n']).groups.keys() })
    df320 = pd.DataFrame({ "t": df320.groupby(['n'])['t'].mean(), "n": df320.groupby(['n']).groups.keys() })
    df380 = pd.DataFrame({ "t": df380.groupby(['n'])['t'].mean(), "n": df380.groupby(['n']).groups.keys() })

    # CURVA DE DESCARGA
    plt.plot(df200['t'], df200['n'], label="d = 1.2m")
    plt.plot(df260['t'], df260['n'], label="d = 1.8m")
    plt.plot(df320['t'], df320['n'], label="d = 2.4m")
    plt.plot(df380['t'], df380['n'], label="d = 3m")
    plt.xlabel('Tiempo (s)')
    plt.ylabel('Peatones evacuados')
    plt.legend()
    plt.savefig('ex-c-1.png')
    plt.show()

    # CAUDAL Q(t) = (n(t + dt) - n(t)) / dt
    dt = 2
    timeStep = 0.1
    timestamps = []
    Q200 = []
    Q260 = []
    Q320 = []
    Q380 = []
    for t in np.arange(0, int(df200['t'].max()) + 1, timeStep):
        timestamps.append(t)
        Q200.append(n(t, dt, df200))
        if(n(t, dt, df260) > 0):
            Q260.append(n(t, dt, df260))
        if(n(t, dt, df320) > 0):
            Q320.append(n(t, dt, df320))
        if(n(t, dt, df380) > 0):
            Q380.append(n(t, dt, df380))
    Q200 = np.nan_to_num(Q200).tolist()
    Q260 = np.nan_to_num(Q260).tolist()
    Q320 = np.nan_to_num(Q320).tolist()
    Q380 = np.nan_to_num(Q380).tolist()
    plt.plot(timestamps[0:len(Q200)], Q200, label="d = 1.2m")
    plt.plot(timestamps[0:len(Q260)], Q260, label="d = 1.8m")
    plt.plot(timestamps[0:len(Q320)], Q320, label="d = 2.4m")
    plt.plot(timestamps[0:len(Q380)], Q380, label="d = 3m")
    plt.xlabel('Tiempo (s)')
    plt.ylabel('Caudal (1/s)')
    plt.legend()
    plt.savefig('ex-c-2.png')
    plt.show()

    # CAUDAL MEDIO
    Q200 = Q200[int(6/timeStep) : int(50/timeStep)] # 6 a 50
    Q260 = Q260[int(2.5/timeStep) : int(26/timeStep)] # 2.5 a 26
    Q320 = Q320[int(3/timeStep) : int(17/timeStep)] # 3 a 17
    Q380 = Q380[int(4/timeStep) : int(14/timeStep)] # 4 a 14

    A = np.array(Q200).mean()**2 + np.array(Q260).mean()**2 + np.array(Q320).mean()**2 + np.array(Q380).mean()**2
    B = (-2 * np.array(Q200).mean() * math.pow(1.2, 1.5)) + (-2 * np.array(Q260).mean() * math.pow(1.8, 1.5)) + (-2 * np.array(Q320).mean() * math.pow(2.4, 1.5)) + (-2 * np.array(Q380).mean() * math.pow(3, 1.5))
    D = 1.2**3 + 1.8**3 + 2.4**3 + 3**3
    c = -B/(2*D)

    ec = A + B*c + D * c**2
    print("c = " +str(c) +"\tE(c) = " +str(ec))

    Xs = []
    Ys = []
    for d in np.arange(1, 3.5, 0.25):
        Xs.append(d)
        Ys.append(c*math.pow(d, 1.5))
    plt.plot(Xs, Ys, color='grey', lw='0.25')

    plt.errorbar(1.2, np.array(Q200).mean(), yerr=np.array(Q200).std(), color='blue', ecolor='gray', fmt='-o', ms=4)
    plt.errorbar(1.8, np.array(Q260).mean(), yerr=np.array(Q260).std(), color='blue', ecolor='gray', fmt='-o', ms=4)
    plt.errorbar(2.4, np.array(Q320).mean(), yerr=np.array(Q320).std(), color='blue', ecolor='gray', fmt='-o', ms=4)
    plt.errorbar(3, np.array(Q380).mean(), yerr=np.array(Q380).std(), color='blue', ecolor='gray', fmt='-o', ms=4)
    plt.xlabel('Ancho de apertura (m)')
    plt.ylabel('<Q> (1/s)')
    plt.savefig('ex-d-1.png')
    plt.show()

    C = np.arange(c-0.25, c+0.25+0.01, 0.01)
    Ec = []
    for c_aprox in C:
        ec = A + B*c_aprox + D * c_aprox**2
        Ec.append( ec )
    plt.plot(C, Ec)
    plt.xlabel("c  " +r'$(\frac{m^{1.5}}{s})$')
    plt.ylabel("E(c)  " +r'$(1/s^{2})$')
    plt.savefig('ex-d-2.png')
    plt.show()

if __name__ == "__main__":
    main()
