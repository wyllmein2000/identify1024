#!/bin/csh

set out=aaa

set n=576
set i=0
set s=''

while ($i < $n)
@ i += 1	
set s=${s}'0f,'
end
echo $s >> $out
