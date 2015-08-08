f(x)=a*(1-(sinh(log(1+sqrt(2))*Tc/x))**(-4.0))**(1.0/8.0)
fit f(x) "magnetization.dat" using 1:($2>0?$2:-$2) via a,Tc
set terminal png size 800, 600
set output "magnetization_glauber.png"
plot "magnetization.dat" using 1:($2>0?$2:-$2):3 title "Monte Carlo data" with yerrorbars, f(x) title "Exact Solution" with lines lw 3
