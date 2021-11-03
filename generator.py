import random
from math import sqrt

def main():
    width = 20
    height = 20
    gapSize = 1.2
    desiredV = 2
    file = open("input.txt", 'w')
    particles = []
    i = 0
    while(i < 200):
      flag = True
      j = 0
      radius = random.uniform(0.25, 0.29) # Diametro = (0.5, 0.58)
      mass = 80
      x = random.uniform(radius, width - radius)
      y = random.uniform(radius, height - radius)

      while (j < len(particles) and flag):
        if(sqrt((particles[j][0] - x)**2 + (particles[j][1] - y)**2) < radius + particles[j][2]):
          flag = False
        else:
          j = j+1

      if(flag):
        particles.append([x,y,radius,mass])
        i += 1

    file.write(str(width) + '\n')
    file.write(str(height) + '\n')
    file.write(str(gapSize) + '\n')
    file.write(str(desiredV) + '\n')
    file.write(str(len(particles)) + '\n' + '\n')
    for p in particles:
      file.write(str(p[0]) + '  ' + str(p[1]) + '  ' + str(p[2]) + '  ' + str(p[3]) + '\n')

if __name__ == "__main__":
    main()
