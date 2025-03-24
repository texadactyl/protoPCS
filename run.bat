set COUNT=100

producer  %COUNT%  saucisse.data

java  -cp consumer  consumer  %COUNT%  saucisse.data

