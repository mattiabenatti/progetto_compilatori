push 0
lhp
push 0
add
push 10
lhp
sw
push 1
lhp
add
shp
push function1
lhp
sw
push 1
lhp
add
shp
push 13
push -2
lfp
add
lw
lfp
push -2
lfp
add
lw
push 1
add
lw
js
print
halt

function0:
cfp
lra
push 1
lfp
add
lw
push 0
add
lw
srv
sra
sfp
pop
lrv
lra
js

function1:
cfp
lra
push 1
lfp
add
lw
push 0
add
lw
push 1
add
srv
sra
sfp
pop
lrv
lra
js
