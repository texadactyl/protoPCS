set -e

COUNT=100

producer  $COUNT  data/saucisse.data

java  -cp consumer  consumer  $COUNT  data/saucisse.data

